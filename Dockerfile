# syntax=docker/dockerfile:latest
ARG BUILD_IMAGE=adoptopenjdk:11-jdk-hotspot
ARG JRE_IMAGE=adoptopenjdk:11-jre-hotspot

FROM $BUILD_IMAGE as build
ENV SBT_VERSION=1.7.0
SHELL [ "/bin/bash", "-cx" ]
WORKDIR /tmp
RUN curl -sLo - https://github.com/sbt/sbt/releases/download/v$SBT_VERSION/sbt-$SBT_VERSION.tgz | tar -xvzf -; \
  mv sbt/bin/* /usr/local/bin/; \
  rm -rf /tmp/*
WORKDIR /app
ENV JAVA_OPTS="-Xms4G -Xmx8G -Xss6M -XX:ReservedCodeCacheSize=256M -XX:+CMSClassUnloadingEnabled -XX:+UseG1GC"
ENV JVM_OPTS="-Xms4G -Xmx8G -Xss6M -XX:ReservedCodeCacheSize=256M -XX:+CMSClassUnloadingEnabled -XX:+UseG1GC"
ENV SBT_OPTS="-Xms4G -Xmx8G -Xss6M -XX:ReservedCodeCacheSize=256M -XX:+CMSClassUnloadingEnabled -XX:+UseG1GC"
COPY . .
RUN --mount=type=cache,target=/root/.sbt \
  --mount=type=cache,target=/root/.cache \
  --mount=type=cache,target=/root/.ivy2 \
  sbt dist

FROM $BUILD_IMAGE as extract
SHELL [ "/bin/bash", "-cx" ]
WORKDIR /app
RUN --mount=type=cache,target=/var/lib/apt/cache \
  --mount=type=cache,target=/var/lib/cache \
  apt update; \
  apt install unzip -y
COPY --from=build /app/target/universal/ /app
RUN cp *.zip mantleplace.zip; \
  ls -alt; \
  unzip mantleplace.zip; \
  ls -alt; \
  rm *.zip; \
  ls -alt; \
  mv mantleplace* mantleplace; \
  ls -alt
  # awk 'NR==1{print; print "set -x"} NR!=1' mantleplace/bin/mantleplace > mantleplace/bin/mantleplace.tmp; \
  # mv mantleplace/bin/mantleplace.tmp mantleplace/bin/mantleplace; \
  # chmod +x mantleplace/bin/mantleplace; \
  # head -n 10 mantleplace/bin/mantleplace

FROM $JRE_IMAGE
LABEL org.opencontainers.image.title=mantleplace
LABEL org.opencontainers.image.base.name=$JRE_IMAGE
LABEL org.opencontainers.image.description=mantleplace
LABEL org.opencontainers.image.source=https://github.com/assetmantle/mantleplace
LABEL org.opencontainers.image.documentation=https://github.com/assetmantle/mantleplace
WORKDIR /
RUN --mount=type=cache,target=/var/lib/apt/cache \
  --mount=type=cache,target=/var/lib/apt/lists \
  --mount=type=cache,target=/var/lib/cache \
  --mount=type=cache,target=/var/cache/apt/archives \
  apt update; \
  apt install -y openssl libexpat1 libsasl2-2 libssl1.1 libsasl2-modules-db
COPY --from=extract /app/mantleplace /mantleplace
ENTRYPOINT [ "/mantleplace/bin/mantleplace" ]
