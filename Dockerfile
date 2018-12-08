# Создаем образ на основе play-framework
FROM ingensi/play-framework:latest
MAINTAINER  sandquattro sandquattro@gmail.com
# Создать директорию app
WORKDIR /app
# Скопировать исходники в app
COPY . /app
EXPOSE 9000
RUN ["activator", "run"]