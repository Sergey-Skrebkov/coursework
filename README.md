## Описание

Фул стек приложение - справочник документов

## Структура

- `backend` - Бэкенд на java.
- `ui` - Фронтенд на react + redux.

## Подготовка

Установите:

- [node](https://nodejs.org) - front
- [openjdk](https://openjdk.java.net) 15 - java бэк

## Запуск

### Собрать Jar файл

```
./gradlew backend:bootJar
```
### Запуск docker-compose
```
docker-compose up -d
```

### Адрес страницы
```
http://localhost:3006/#/
```