# Estágio de Build
# Usamos uma imagem base que já inclui o Maven e o JDK 17.
FROM maven:3.9.6-eclipse-temurin-17-focal AS build

# Define o diretório de trabalho dentro do contêiner.
WORKDIR /app

# Copia o arquivo pom.xml para que as dependências sejam cacheadas.
COPY pom.xml .

# Copia o código-fonte da aplicação.
COPY src ./src

# Constrói a aplicação usando Maven.
# O build para o Gateway também pode pular os testes para agilizar.
RUN mvn clean install package -Dmaven.test.skip=true

# Estágio de Runtime
# Usamos uma imagem base com JRE (Runtime Environment) para rodar a aplicação.
FROM eclipse-temurin:17-jre-focal

# Define o diretório de trabalho dentro do contêiner.
WORKDIR /app

# Copia o JAR executável gerado no estágio de build para o estágio de runtime.
# O nome do JAR é baseado no artifactId e version do pom.xml do Gateway.
COPY --from=build /app/target/gateway-0.0.1-SNAPSHOT.jar legalconnect-gateway.jar

# Expõe a porta em que o Gateway será executado.
# Conforme o application.properties, a porta padrão é 8080.
EXPOSE 8080

# Define o comando de entrada que será executado quando o contêiner iniciar.
# Isso executa o JAR do Spring Boot.
ENTRYPOINT ["java", "-jar", "legalconnect-gateway.jar"]

