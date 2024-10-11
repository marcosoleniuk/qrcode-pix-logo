FROM eclipse-temurin:22-jre-alpine

WORKDIR /app

COPY target/BackendGerarQRCodePix.jar /app/BackendGerarQRCodePix.jar

RUN apk update && \
    apk add tzdata && \
    cp /usr/share/zoneinfo/America/Sao_Paulo /etc/localtime && \
    echo "America/Sao_Paulo" > /etc/timezone && \
    apk del tzdata

EXPOSE 8581

CMD ["java", "-jar", "BackendGerarQRCodePix.jar"]