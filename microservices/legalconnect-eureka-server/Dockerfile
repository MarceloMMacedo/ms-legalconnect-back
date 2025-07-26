# Estágio de Build
# Usamos uma imagem base que já inclui o Maven e o JDK 17.
# maven:3.9.6-eclipse-temurin-17-focal é uma boa escolha.
FROM maven:3.9.6-eclipse-temurin-17-focal AS build

# Define o diretório de trabalho dentro do contêiner.
WORKDIR /app

# Copia o arquivo pom.xml para que as dependências sejam cacheadas.
# Isso evita que o Maven precise baixar todas as dependências novamente
# se apenas o código-fonte for alterado.
COPY pom.xml .

# Copia o código-fonte da aplicação.
COPY src ./src

# Constrói a aplicação usando Maven.
# -Dmaven.test.skip=true pula os testes para um build mais rápido no Docker.
# 'install' é adicionado para garantir que o projeto seja instalado no repositório Maven local do contêiner,
# o que pode ser útil para projetos multi-módulos, embora 'package' seja suficiente para este caso.
RUN mvn clean install package -Dmaven.test.skip=true

# Estágio de Runtime
# Usamos uma imagem base com JRE (Runtime Environment) para rodar a aplicação.
# JRE é menor que JDK, resultando em uma imagem final mais leve.
FROM eclipse-temurin:17-jre-focal

# Define o diretório de trabalho dentro do contêiner.
WORKDIR /app

# Copia o JAR executável gerado no estágio de build para o estágio de runtime.
# O nome do JAR é baseado no artifactId e version do pom.xml.
COPY --from=build /app/target/eureka-server-0.0.1-SNAPSHOT.jar eureka-server.jar

# Expõe a porta em que o Eureka Server será executado.
# Conforme o application.properties, a porta padrão é 8761.
EXPOSE 8761

# Define o comando de entrada que será executado quando o contêiner iniciar.
# Isso executa o JAR do Spring Boot.
ENTRYPOINT ["java", "-jar", "eureka-server.jar"]

