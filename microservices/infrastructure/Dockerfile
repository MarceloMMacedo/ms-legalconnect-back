# Use a imagem oficial do NGINX como base
FROM nginx:alpine

# Remove a configuração padrão (se existir)
RUN rm -f /etc/nginx/nginx.conf

# Cria diretório para nossos arquivos de configuração
WORKDIR /app

# Copia o arquivo de configuração (certifique-se que nginx.conf é um arquivo, não diretório)
COPY  /nginx/nginx.conf /etc/nginx/nginx.conf

# Verifica se o arquivo foi copiado corretamente
RUN ls -la /etc/nginx/ && \
    test -f /etc/nginx/nginx.conf && \
    echo "Arquivo de configuração existe" || \
    (echo "ERRO: nginx.conf não é um arquivo válido" && exit 1)

# Verifica a sintaxe do NGINX
RUN nginx -t

# Expõe a porta 80
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]