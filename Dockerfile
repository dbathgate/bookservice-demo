FROM openjdk:8u111-jdk-alpine

COPY /build/libs/*.jar app.jar
ADD ./run.sh run.sh

ENV JVM_ARGS=-Dapp.version=v2

ENTRYPOINT ["/run.sh"]