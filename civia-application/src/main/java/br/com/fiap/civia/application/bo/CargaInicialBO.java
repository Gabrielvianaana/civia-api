package br.com.fiap.civia.application.bo;

import br.com.fiap.civia.domain.dao.PerfilAtendidoDAO;
import br.com.fiap.civia.domain.dao.TipoServicoDAO;
import br.com.fiap.civia.domain.model.PerfilAtendido;
import br.com.fiap.civia.domain.model.TipoServico;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Garante as tabelas de dominio (tipos de servico da tela inicial e perfis atendidos) quando estao vazias.
 * Os dados completos de exemplo (simulados) estao em db/03_dados.sql.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CargaInicialBO {

    private final TipoServicoDAO tipoDAO;
    private final PerfilAtendidoDAO perfilDAO;

    @Transactional
    public void carregarSeNecessario() {
        if (tipoDAO.contar() == 0) {
            tipoDAO.salvar(TipoServico.novo("Abrigo", "cama"));
            tipoDAO.salvar(TipoServico.novo("Refeicao", "prato"));
            tipoDAO.salvar(TipoServico.novo("Banho", "chuveiro"));
            tipoDAO.salvar(TipoServico.novo("Atendimento", "pessoa"));
            log.info(">>> CIVIA: tipos de servico iniciais carregados");
        }
        if (perfilDAO.contar() == 0) {
            for (String perfil : new String[]{"Homem", "Mulher", "Familia com crianca", "Pessoa LGBT+", "Pessoa idosa",
                    "Pessoa com animal"}) {
                perfilDAO.salvar(PerfilAtendido.novo(perfil));
            }
            log.info(">>> CIVIA: perfis atendidos iniciais carregados");
        }
    }
}
