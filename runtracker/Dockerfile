# runtracker/Dockerfile
FROM openjdk:17-alpine
LABEL authors="Mangjun"

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 경로 ARG로 받기
ARG JAR_FILE=build/libs/*.jar

# JAR 파일을 WORKDIR(/app)로 복사
COPY ${JAR_FILE} app.jar

# WORKDIR(/app) 내의 app.jar 실행
ENTRYPOINT ["java","-jar","app.jar"]