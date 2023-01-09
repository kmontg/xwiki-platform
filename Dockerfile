FROM google/cloud-sdk:alpine as builder

WORKDIR /xwiki

RUN gsutil cp gs://xwiki-release/target/xwiki-platform-distribution-jetty-hsqldb-15.0-SNAPSHOT.zip .

FROM debian:buster-slim
RUN set -x && apt-get update && DEBIAN_FRONTEND=noninteractive apt-get install -y \
    --no-install-recommends \
    ca-certificates && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /

COPY --from=builder /xwiki/xwiki-platform-distribution-jetty-hsqldb-15.0-SNAPSHOT.zip /xwiki
RUN unzip /xwiki/xwiki-platform-distribution-jetty-hsqldb-15.0-SNAPSHOT.zip -d /xwiki
RUN rm /xwiki/xwiki-platform-distribution-jetty-hsqldb-15.0-SNAPSHOT.zip

CMD ["/xwiki/xwiki-platform-distribution-jetty-hsqldb-15.0-SNAPSHOT/start_xwiki.sh"]


