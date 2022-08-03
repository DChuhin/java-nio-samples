FROM openjdk:11

ENV JAVA_OPTS -Dcom.sun.management.jmxremote \
  -Dcom.sun.management.jmxremote.authenticate=false \
  -Dcom.sun.management.jmxremote.ssl=false \
  -Dcom.sun.management.jmxremote.local.only=false \
  -Dcom.sun.management.jmxremote.port=9010 \
  -Dcom.sun.management.jmxremote.rmi.port=9010 \
  -Djava.rmi.server.hostname=127.0.0.1 \
  -Xms512m \
  -Xmx512m

COPY src src
COPY static src/static
WORKDIR src
RUN javac InternalServerApp.java
RUN javac NioProxyApp.java
RUN javac BlockingProxyApp.java

CMD java ${JAVA_OPTS} ${APP}
