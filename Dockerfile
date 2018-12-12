# Создаем образ на основе scala-sbt(она использует openj)
FROM hseeberger/scala-sbt:latest
# Автор
MAINTAINER  sandquattro sandquattro@gmail.com
#Устанавливаем переменные окружения для использования в application.conf
ENV REDIS_URL="192.168.100.82"
ENV IMPALA_HOST="192.168.100.51"
ENV IMPALA_PORT="21050"
ENV PGDBIP="192.168.100.75"
ENV JAVA_OPTS="-Xms512m -Xmx2G"
# Создаем рабочую директорию
WORKDIR /pgk-bigdata
# Скопировать исходники в pgk-bigdata
COPY . /pgk-bigdata
# Запустить обновление пакетов операционной системы
RUN apt-get update
# устанавливаем git в ОС
RUN apt-get install -y git
# EXPOSE указывает Docker что контейнер слушает определенные порты после запуска.
# EXPOSE не делает порты контейнера доступными для хоста. Для этого нужно использовать
# флаг -p (что бы открыть диапазон портов) или флаг -P что бы открыть все порты из EXPOSE.
EXPOSE 9000