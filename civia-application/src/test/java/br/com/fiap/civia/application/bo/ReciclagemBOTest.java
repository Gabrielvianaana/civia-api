package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.application.dto.CartaoRequest;
import br.com.fiap.civia.application.dto.CartaoResponse;
import br.com.fiap.civia.application.dto.EntregaRequest;
import br.com.fiap.civia.application.dto.EntregaResponse;
import br.com.fiap.civia.application.dto.TrocaRequest;
import br.com.fiap.civia.application.exception.RegraNegocioException;
import br.com.fiap.civia.domain.dao.CartaoDAO;
import br.com.fiap.civia.domain.dao.ColetaDAO;
import br.com.fiap.civia.domain.dao.EntregaDAO;
import br.com.fiap.civia.domain.dao.MaterialDAO;
import br.com.fiap.civia.domain.dao.OrganizacaoDAO;
import br.com.fiap.civia.domain.dao.PostoReciclagemDAO;
import br.com.fiap.civia.domain.dao.RecompensaDAO;
import br.com.fiap.civia.domain.dao.TrocaDAO;
import br.com.fiap.civia.domain.dao.UsuarioPainelDAO;
import br.com.fiap.civia.domain.model.Cartao;
import br.com.fiap.civia.domain.model.Entrega;
import br.com.fiap.civia.domain.model.Material;
import br.com.fiap.civia.domain.model.Organizacao;
import br.com.fiap.civia.domain.model.PerfilAcesso;
import br.com.fiap.civia.domain.model.PostoReciclagem;
import br.com.fiap.civia.domain.model.Recompensa;
import br.com.fiap.civia.domain.model.StatusCartao;
import br.com.fiap.civia.domain.model.StatusPosto;
import br.com.fiap.civia.domain.model.TipoOrganizacao;
import br.com.fiap.civia.domain.model.Troca;
import br.com.fiap.civia.domain.model.UsuarioPainel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Regras do modulo de reciclagem nos BOs: RN07, RN08, RN09, RN11 e RN12. */
class ReciclagemBOTest {

    private final CartaoDAO cartaoDAO = mock(CartaoDAO.class);
    private final EntregaDAO entregaDAO = mock(EntregaDAO.class);
    private final PostoReciclagemDAO postoDAO = mock(PostoReciclagemDAO.class);
    private final MaterialDAO materialDAO = mock(MaterialDAO.class);
    private final ColetaDAO coletaDAO = mock(ColetaDAO.class);
    private Cartao cartao;

    @BeforeEach
    void setUp() {
        cartao = Cartao.reconstituir(1L, "CVA7K2M9", 10L, null, Dados.AGORA.minusDays(5), StatusCartao.ATIVO, 30);
        when(cartaoDAO.buscarPorCodigo("CVA7K2M9")).thenReturn(Optional.of(cartao));
        when(cartaoDAO.buscarPorId(1L)).thenReturn(Optional.of(cartao));
        when(cartaoDAO.salvar(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Nested
    class Entregas {

        private EntregaBO bo;

        @BeforeEach
        void setUp() {
            bo = new EntregaBO(entregaDAO, cartaoDAO, postoDAO, materialDAO, coletaDAO, Dados.RELOGIO);
            when(postoDAO.buscarPorId(1L)).thenReturn(Optional.of(PostoReciclagem.reconstituir(1L, 1L, 4L,
                    new BigDecimal("100.00"), new BigDecimal("10.00"), StatusPosto.ATIVO)));
            when(materialDAO.buscarPorId(1)).thenReturn(Optional.of(Material.reconstituir(1, "Lata de aluminio", 10, true)));
            when(materialDAO.buscarPorId(3)).thenReturn(Optional.of(Material.reconstituir(3, "Vidro", 0, false)));
            when(coletaDAO.ultimaColetaDoPosto(1L)).thenReturn(Optional.empty());
            when(entregaDAO.somarPesoDoPosto(eq(1L), isNull(), isNull())).thenReturn(new BigDecimal("20.00"));
            when(entregaDAO.salvar(any())).thenAnswer(inv -> {
                Entrega e = inv.getArgument(0);
                return Entrega.reconstituir(9L, e.getCartaoId(), e.getPostoId(), e.getMaterialId(), e.getPesoKg(),
                        e.getCreditosGerados(), e.getDataEntrega());
            });
        }

        private void jaEntregueHoje(String kg) {
            when(entregaDAO.somarPesoDoCartao(eq(1L), eq(Dados.AGORA.toLocalDate().atStartOfDay()),
                    eq(Dados.AGORA.toLocalDate().plusDays(1).atStartOfDay()), isNull())).thenReturn(new BigDecimal(kg));
        }

        @Test
        void entregaCreditaOCartao() {
            jaEntregueHoje("0.00");
            EntregaResponse r = bo.registrar(new EntregaRequest("cva7k2m9", 1L, 1, new BigDecimal("2.5")));
            assertEquals(25, r.creditosGerados());
            assertEquals(55, cartao.getSaldoCreditos());
            assertEquals(Dados.AGORA, r.dataEntrega());
        }

        @Test
        void rn08LimiteDiarioPorCartao() {
            jaEntregueHoje("8.00");
            assertThrows(RegraNegocioException.class,
                    () -> bo.registrar(new EntregaRequest("CVA7K2M9", 1L, 1, new BigDecimal("2.01"))));
            assertEquals(30, cartao.getSaldoCreditos());
            verify(entregaDAO, never()).salvar(any());
        }

        @Test
        void rn08PermiteAteOLimiteExato() {
            jaEntregueHoje("8.00");
            assertEquals(20, bo.registrar(new EntregaRequest("CVA7K2M9", 1L, 1, new BigDecimal("2.00"))).creditosGerados());
        }

        @Test
        void rn09VidroERecusado() {
            jaEntregueHoje("0.00");
            assertThrows(RegraNegocioException.class,
                    () -> bo.registrar(new EntregaRequest("CVA7K2M9", 1L, 3, BigDecimal.ONE)));
        }
    }

    @Nested
    class Trocas {

        private TrocaBO bo;
        private final TrocaDAO trocaDAO = mock(TrocaDAO.class);
        private final RecompensaDAO recompensaDAO = mock(RecompensaDAO.class);
        private final UsuarioPainelDAO usuarioDAO = mock(UsuarioPainelDAO.class);

        @BeforeEach
        void setUp() {
            bo = new TrocaBO(trocaDAO, cartaoDAO, recompensaDAO, usuarioDAO, Dados.RELOGIO);
            when(recompensaDAO.buscarPorId(1L)).thenReturn(Optional.of(Recompensa.reconstituir(1L, 5L, "Kit de higiene", 20, true)));
            when(recompensaDAO.buscarPorId(2L)).thenReturn(Optional.of(Recompensa.reconstituir(2L, 5L, "Cobertor", 40, true)));
            when(usuarioDAO.buscarPorId(7L)).thenReturn(Optional.of(
                    UsuarioPainel.reconstituir(7L, 10L, "Ana (simulada)", "ana@civia.exemplo.org", PerfilAcesso.EQUIPE, true)));
            when(usuarioDAO.buscarPorId(8L)).thenReturn(Optional.of(
                    UsuarioPainel.reconstituir(8L, 4L, "Diego (simulado)", "diego@civia.exemplo.org", PerfilAcesso.COOPERATIVA, true)));
            when(trocaDAO.salvar(any())).thenAnswer(inv -> {
                Troca t = inv.getArgument(0);
                return Troca.reconstituir(3L, t.getCartaoId(), t.getRecompensaId(), t.getUsuarioId(), t.getCreditosUsados(), t.getDataTroca());
            });
        }

        @Test
        void trocaDebitaOCustoDaRecompensa() {
            assertEquals(20, bo.registrar(new TrocaRequest("CVA7K2M9", 1L, 7L)).creditosUsados());
            assertEquals(10, cartao.getSaldoCreditos());
        }

        @Test
        void rn11SaldoInsuficienteERecusado() {
            assertThrows(RegraNegocioException.class, () -> bo.registrar(new TrocaRequest("CVA7K2M9", 2L, 7L)));
            assertEquals(30, cartao.getSaldoCreditos());
        }

        @Test
        void cooperativaNaoRegistraTroca() {
            assertThrows(RegraNegocioException.class, () -> bo.registrar(new TrocaRequest("CVA7K2M9", 1L, 8L)));
        }
    }

    @Nested
    class Cartoes {

        private CartaoBO bo;
        private final OrganizacaoDAO organizacaoDAO = mock(OrganizacaoDAO.class);

        @BeforeEach
        void setUp() {
            bo = new CartaoBO(cartaoDAO, organizacaoDAO, entregaDAO, mock(TrocaDAO.class), Dados.RELOGIO);
            when(organizacaoDAO.buscarPorId(10L)).thenReturn(Optional.of(
                    Organizacao.reconstituir(10L, "Centro POP (simulado)", TipoOrganizacao.CENTRO_POP, null, null)));
            when(organizacaoDAO.buscarPorId(11L)).thenReturn(Optional.of(
                    Organizacao.reconstituir(11L, "ONG (simulada)", TipoOrganizacao.ONG, null, null)));
        }

        @Test
        void rn07CartaoNovoTemCodigoAleatorioESemSaldo() {
            CartaoResponse r = bo.emitir(new CartaoRequest(10L, null));
            assertEquals(8, r.codigo().length());
            assertEquals(0, r.saldoCreditos());
        }

        @Test
        void rn12SubstituicaoTransfereOSaldoEBloqueiaOAnterior() {
            CartaoResponse r = bo.emitir(new CartaoRequest(10L, 1L));
            assertEquals(30, r.saldoCreditos());
            assertEquals(1L, r.cartaoAnteriorId());
            assertEquals(StatusCartao.BLOQUEADO, cartao.getStatus());
            assertEquals(0, cartao.getSaldoCreditos());
        }

        @Test
        void soOCentroPopEmiteCartoes() {
            assertThrows(RegraNegocioException.class, () -> bo.emitir(new CartaoRequest(11L, null)));
        }
    }
}
