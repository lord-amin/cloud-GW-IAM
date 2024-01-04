FROM ubuntu-jdk17:latest
ENV TZ=Asia/Tehran
WORKDIR /root
ENV JAVA_OPTS="-Xmx1024m -Dfile.encoding=UTF8 -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8091"
COPY target/api-gateway-0.0.1-SNAPSHOT.jar  app.jar
EXPOSE 8091
EXPOSE 8071
CMD ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar  ${JVM_ARGS}"]