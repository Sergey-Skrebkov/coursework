# Используйте официальный образ Gradle для сборки проекта
FROM gradle:6.7.1-jdk15 as builder
WORKDIR /app
# Копируйте файлы проекта
COPY ./ /app
# Выполняйте сборку проекта с помощью Gradle
RUN gradle bootJar

FROM openjdk:15
COPY --from=builder /app/build/libs/app-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]