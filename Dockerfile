# ---------- Etapa 1: build com Maven + JDK 21 ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia os POMs primeiro para aproveitar cache das dependencias
COPY pom.xml .
COPY civia-domain/pom.xml civia-domain/
COPY civia-infrastructure/pom.xml civia-infrastructure/
COPY civia-application/pom.xml civia-application/
COPY civia-presentation/pom.xml civia-presentation/
RUN mvn -B -q dependency:go-offline || true

COPY . .
RUN mvn -B -q package -DskipTests

# ---------- Etapa 2: runtime enxuto (JRE 21) ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Horarios dos servicos sao do fuso de Sao Paulo
ENV TZ=America/Sao_Paulo
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -Duser.timezone=America/Sao_Paulo"

COPY --from=build /app/civia-presentation/target/civia-api.jar app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
