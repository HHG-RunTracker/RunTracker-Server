# runtracker/nginx/Dockerfile
FROM nginx:alpine
LABEL authors="Mangjun"
# 환경변수 치환을 위한 쉘 사용
RUN apk add --no-cache bash coreutils

# Nginx 설정 템플릿 복사
COPY ./default.conf.template /etc/nginx/templates/default.conf.template

# nginx 실행 전에 envsubst로 환경변수 치환된 설정을 적용
CMD ["/bin/sh", "-c", "envsubst < /etc/nginx/templates/default.conf.template > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"]
