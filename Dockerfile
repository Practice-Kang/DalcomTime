# Step 1: Gradle 빌드를 위한 베이스 이미지 사용
FROM gradle:7.6.1-jdk17 AS builder

# Step 2: 프로젝트 파일을 복사하고 작업 디렉토리 설정
COPY . /app
WORKDIR /app

# Step 3: Gradle Wrapper를 사용하여 빌드 수행
RUN ./gradlew bootJar --no-daemon

# Step 4: OpenJDK 이미지를 사용하여 최종 이미지 생성
FROM openjdk:17-jdk-slim

# Step 5: 빌드된 JAR 파일을 복사
COPY --from=builder /app/build/libs/dalcom-0.0.1-SNAPSHOT.jar app.jar

# Step 6: 컨테이너 실행 시 명령어 정의
ENTRYPOINT ["java", "-jar", "app.jar"]
