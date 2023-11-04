#Сборка реакт приложения в контейнере с нодой
FROM node:20-alpine as build
LABEL authors="sergejskrebkov"
WORKDIR /usr/app
COPY ./ /usr/app
RUN npm install
COPY . /app
RUN npm run build

#Копировать собранный билд с в контейнер с nginxom
FROM nginx:stable-alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
