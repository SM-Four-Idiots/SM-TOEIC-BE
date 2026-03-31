# 도커 허브에서 JAVA 21 가져옴
FROM eclipse-temurin:21-jre
# 패스 설정
WORKDIR /app
# 빌드된 .jar를 컨테이너로 복사
COPY build/libs/*.jar app.jar
# 컨테이너 시작 시 .jar 실행
ENTRYPOINT ["java", "-jar", "app.jar"]