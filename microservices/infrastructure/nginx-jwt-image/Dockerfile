# Usa a imagem base Alpine Linux, que é leve e segura.
# 'alpine:3.19' é uma versão explícita e estável para garantir reprodutibilidade e evitar problemas de cache/resolução.
FROM alpine:3.19

# Instala as dependências de build necessárias para compilar o Nginx e o módulo JWT.
# - git: para clonar o repositório do módulo nginx-jwt.
# - build-base: pacote que inclui ferramentas essenciais de compilação (gcc, make, etc.).
# - openssl-dev: cabeçalhos e bibliotecas de desenvolvimento para OpenSSL (necessário para SSL/TLS no Nginx).
# - pcre-dev: cabeçalhos e bibliotecas de desenvolvimento para PCRE (Perl Compatible Regular Expressions, usado pelo Nginx).
# - zlib-dev: cabeçalhos e bibliotecas de desenvolvimento para zlib (biblioteca de compressão, usada pelo Nginx).
# - curl: para baixar o código-fonte do Nginx.
# - gnupg: (Opcional, não estritamente necessário para este build, mas pode ser útil para verificação de assinaturas em outros contextos).
# - linux-headers: (Opcional, geralmente não necessário para compilação de Nginx, mais para módulos de kernel).
RUN apk add --no-cache \
  git \
  build-base \
  openssl-dev \
  pcre-dev \
  zlib-dev \
  curl \
  gnupg \
  linux-headers

# Define a versão do NGINX que será baixada e compilada.
ENV NGINX_VERSION=1.25.3

# Baixa o código-fonte do NGINX, extrai e move para o diretório de compilação.
# CORREÇÃO: Alterado 'NGING_VERSION' para 'NGINX_VERSION' na linha do 'tar'.
RUN curl -O http://nginx.org/download/nginx-${NGINX_VERSION}.tar.gz && \
    tar -zxvf nginx-${NGINX_VERSION}.tar.gz && \
    mv nginx-${NGINX_VERSION} /usr/src/nginx

# Clona o repositório do módulo nginx-jwt para um diretório temporário.
# É recomendável especificar uma tag ou commit hash para o módulo para garantir builds repetíveis.
# Ex: RUN git clone --branch v1.0.0 https://github.com/auth0/nginx-jwt.git /tmp/nginx-jwt
RUN git clone https://github.com/auth0/nginx-jwt.git /tmp/nginx-jwt

# Define o diretório de trabalho para o processo de compilação do Nginx.
WORKDIR /usr/src/nginx

# Configura e compila o NGINX com os módulos desejados, incluindo o nginx-jwt.
# - --prefix=/etc/nginx: Define o diretório de instalação base do Nginx.
# - --sbin-path=/usr/sbin/nginx: Caminho para o executável principal do Nginx.
# - --modules-path=/usr/lib/nginx/modules: Caminho para os módulos dinâmicos (se usados).
# - --conf-path=/etc/nginx/nginx.conf: Caminho para o arquivo de configuração principal.
# - --error-log-path, --http-log-path, --pid-path, --lock-path: Caminhos para logs e PID.
# - --http-client-body-temp-path, --http-proxy-temp-path, etc.: Caminhos para caches temporários.
# - --add-module=/tmp/nginx-jwt: Adiciona o módulo JWT compilado.
# - --with-http_ssl_module: Habilita o módulo SSL para HTTPS.
# - --with-http_realip_module: Habilita o módulo para obter o IP real do cliente.
# - --with-http_v2_module: Habilita o suporte a HTTP/2.
# - --with-http_gzip_static_module: Habilita o módulo para servir arquivos .gz estáticos.
# - --with-pcre: Habilita o suporte a expressões regulares PCRE.
# - --with-zlib: Habilita o suporte a compressão zlib (explícito).
# - --with-threads: Habilita o suporte a threads (para operações não bloqueantes).
# - --with-stream: Habilita o módulo stream (para proxy TCP/UDP).
# - --with-stream_ssl_module: Habilita SSL para o módulo stream.
# 'make' compila o Nginx, e 'make install' o instala.
# Os '&& \' garantem que cada comando é executado sequencialmente e que a build falha se um falhar.
RUN ./configure \
    --prefix=/etc/nginx \
    --sbin-path=/usr/sbin/nginx \
    --modules-path=/usr/lib/nginx/modules \
    --conf-path=/etc/nginx/nginx.conf \
    --error-log-path=/var/log/nginx/error.log \
    --http-log-path=/var/log/nginx/access.log \
    --pid-path=/var/run/nginx.pid \
    --lock-path=/var/run/nginx.lock \
    --http-client-body-temp-path=/var/cache/nginx/client_temp \
    --http-proxy-temp-path=/var/cache/nginx/proxy_temp \
    --http-fastcgi-temp-path=/var/cache/nginx/fastcgi_temp \
    --http-uwsgi-temp-path=/var/cache/nginx/uwsgi_temp \
    --http-scgi-temp-path=/var/cache/nginx/scgi_temp \
    --add-module=/tmp/nginx-jwt \
    --with-http_ssl_module \
    --with-http_realip_module \
    --with-http_v2_module \
    --with-http_gzip_static_module \
    --with-pcre \
    --with-threads \
    --with-stream \
    --with-stream_ssl_module && \
    make && \
    make install

# Limpeza de arquivos de build e cache APK para reduzir o tamanho final da imagem.
# Remove o código-fonte do Nginx, o módulo JWT clonado e o cache do gerenciador de pacotes.
# Também remove as dependências de build instaladas anteriormente.
RUN rm -rf /usr/src/nginx* /tmp/nginx-jwt /var/cache/apk/* && \
    apk del git build-base openssl-dev pcre-dev zlib-dev gnupg linux-headers curl

# Cria os diretórios de cache necessários para o Nginx.
# O Nginx precisa desses diretórios para armazenar arquivos temporários de proxy e cliente.
RUN mkdir -p /var/cache/nginx/client_temp \
    && mkdir -p /var/cache/nginx/proxy_temp

# Define o usuário e grupo para o processo Nginx.
# É uma boa prática de segurança rodar o Nginx com um usuário não-root.
# A imagem Alpine geralmente já tem um usuário 'nginx' ou 'www-data' pré-configurado.
USER nginx

# Comando padrão para iniciar o Nginx quando o contêiner é executado.
# "daemon off;" mantém o processo Nginx em primeiro plano, necessário para Docker.
CMD ["nginx", "-g", "daemon off;"]

# Expõe as portas 80 (HTTP) e 443 (HTTPS) do contêiner.
# Isso informa ao Docker que essas portas são usadas pela aplicação.
EXPOSE 80 443
