# syntax=docker/dockerfile:latest
ARG BUILD_IMAGE=adoptopenjdk:11-jdk-hotspot
ARG JRE_IMAGE=adoptopenjdk:11-jre-hotspot
FROM $BUILD_IMAGE as build
SHELL [ "/bin/bash", "-cx" ]
RUN --mount=type=cache,target=/var/lib/cache/ \
  --mount=type=cache,target=/var/lib/apt/cache \
  apt-get update; \
  apt-get install -y gnupg; \
  echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list; \
  echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list; \
  curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add; \
  apt-get update; \
  apt-get install -y sbt unzip
WORKDIR /app
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
