FROM ubuntu:24.04

LABEL maintainer="Péter Király <pkiraly@gwdg.de>"

LABEL description="A metadata quality assessment tool for Deutsche Digitale Bibliothek."
# the Github repo labels
LABEL org.opencontainers.image.description="Metadata quality assessment of Deutsche Digitale Bibliothek metadata."
LABEL org.opencontainers.image.source=https://github.com/pkiraly/metadata-qa-ddb
LABEL org.opencontainers.image.licenses="GNU General Public License v3.0"

ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=Europe/Berlin
ENV RUN_USER=nobody
ENV RUN_GROUP=0
ENV MQA_VERSION=1.0.0

# Install requirements
RUN sed -i 's|http://|http://de.|g' /etc/apt/sources.list && \
	apt-get update && \
	apt-get install -y \
		curl \
		gnupg \
		# htop \
		jq \
		lsof \
		maven \
		mysql-client \
		nano \
		openjdk-17-jdk \
		php \
		php-http \
		php-mysql \
		php-sqlite3 \
        php-raphf \
		pip \
		sqlite3 \
		supervisor \
		wget

# Install R
RUN echo "echo "deb https://cloud.r-project.org/bin/linux/ubuntu focal-cran40/" > /etc/apt/sources.list.d/cran.list" && \
	apt-get update && \
	apt-get install -y \
              r-base \
              r-cran-gridextra \
              r-cran-stringr \
              r-cran-tidyverse

# Installing Prefect
# RUN pip install -U "prefect==2.8.4" "prefect-shell==0.1.5"

# Installing software
# RUN --chown=${RUN_USER}:${RUN_GROUP} cp -r scripts /opt/metadata-qa-ddb \

WORKDIR /opt/metadata-qa-ddb
# copy JAR file
COPY --chown=${RUN_USER}:${RUN_GROUP} target/metadata-qa-ddb-${MQA_VERSION}-jar-with-dependencies.jar target/metadata-qa-ddb.jar

# copy schemas
COPY --chown=${RUN_USER}:${RUN_GROUP} schemas schemas

# scripts needed to run the
COPY --chown=${RUN_USER}:${RUN_GROUP} scripts scripts
COPY --chown=${RUN_USER}:${RUN_GROUP} run-all.sh .
COPY --chown=${RUN_USER}:${RUN_GROUP} solr-functions.sh .

# docker configurations
COPY --chown=${RUN_USER}:${RUN_GROUP} docker-configuration/docker-entrypoint.sh .
COPY --chown=${RUN_USER}:${RUN_GROUP} docker-configuration/supervisord.conf .
COPY --chown=${RUN_USER}:${RUN_GROUP} docker-configuration/configuration.cnf.docker configuration.cnf

# RUN export MQA_VERSION=1.0.0 && \
# 	mvn package -DskipTests && \
# 	mv target/metadata-qa-ddb-${MQA_VERSION}-jar-with-dependencies.jar target/metadata-qa-ddb.jar && \
# 	rm -rf .git .github && \
# 	chmod +x docker-entrypoint.sh && \
# 	mv configuration.cnf.docker configuration.cnf

# RUN rm -rf .git .github && \
# 	chmod +x docker-entrypoint.sh && \
# 	mv configuration.cnf.docker configuration.cnf

# ENTRYPOINT ["supervisord", "-c", "/opt/metadata-qa-ddb/supervisord.conf"]
# CMD ["./run-all.sh"]
CMD ["./docker-entrypoint.sh"]
