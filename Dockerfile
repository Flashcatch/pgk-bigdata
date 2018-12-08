# Создаем образ на основе play-framework
FROM ingensi/play-framework:latest
MAINTAINER  sandquattro sandquattro@gmail.com
# Создать директорию app
WORKDIR /pgk-bigdata
# Скопировать исходники в app
COPY . /pgk-bigdata
EXPOSE 9000
#Запускать можно и вручную
#RUN ["activator", "run"]