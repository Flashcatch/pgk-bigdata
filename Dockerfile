# Создаем образ на основе play-framework
FROM hseeberger/scala-sbt:latest
MAINTAINER  sandquattro sandquattro@gmail.com
ENV REDIS_URL="192.168.100.65"
# Создаем рабочую директорию
WORKDIR /pgk-bigdata
# Скопировать исходники в app
COPY . /pgk-bigdata
RUN apt-get update
RUN apt-get install git
EXPOSE 9000