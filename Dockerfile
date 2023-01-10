FROM google/cloud-sdk:alpine as builder

ARG GOOGLE_APPLICATION_CREDENTIALS

WORKDIR /xwiki/

ARG GCLOUD_KEY_FILE_CONTENT
RUN echo $GCLOUD_KEY_FILE_CONTENT | gcloud auth activate-service-account --key-file=- \
  && gsutil cp gs://xwiki-release/target/xwiki-platform-distribution-jetty-hsqldb-15.0-SNAPSHOT.zip ./

FROM openjdk:11

WORKDIR /xwiki/

COPY --from=builder /xwiki/xwiki-platform-distribution-jetty-hsqldb-15.0-SNAPSHOT.zip ./
RUN unzip /xwiki/xwiki-platform-distribution-jetty-hsqldb-15.0-SNAPSHOT.zip -d /xwiki
RUN rm ./xwiki-platform-distribution-jetty-hsqldb-15.0-SNAPSHOT.zip

CMD ["./xwiki-platform-distribution-jetty-hsqldb-15.0-SNAPSHOT/start_xwiki.sh"]