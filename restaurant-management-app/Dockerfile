# ---------- Build Stage ----------
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /project

# คัดลอกไฟล์ทั้งหมดเข้าไปใน container build
COPY . /project/

# สั่ง build โดยไม่รันเทส (เพื่อลดเวลา)
RUN mvn clean package -DskipTests -B

# ---------- Runtime Stage ----------
FROM eclipse-temurin:21-jdk-alpine

# กำหนด environment variables สำหรับโฟลเดอร์และชื่อไฟล์
ENV APP_DIR=/application
ENV APP_FILE=app.jar

# สร้าง working directory ภายใน container
WORKDIR $APP_DIR

# คัดลอก jar จาก build stage มา
COPY --from=build /project/target/restaurant-management-app-0.0.1-SNAPSHOT.jar $APP_DIR/$APP_FILE

# เปิดพอร์ต 8080
EXPOSE 8090

# สั่งรัน Spring Boot app
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar $APP_FILE --spring.config.location=classpath:/,file:/application/config/"]



