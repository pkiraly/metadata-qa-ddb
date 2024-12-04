FROM ubuntu:jammy
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
		php-http-request2 \
		php-mysql \
		php-sqlite3 \
		pip \
		sqlite3 \
		supervisor \
		wget

# Install R
RUN wget -qO- https://cloud.r-project.org/bin/linux/ubuntu/marutter_pubkey.asc \
     | gpg --dearmor -o /usr/share/keyrings/r-project.gpg && \
	echo "deb [signed-by=/usr/share/keyrings/r-project.gpg] https://cloud.r-project.org/bin/linux/ubuntu jammy-cran40/" \
     | tee -a /etc/apt/sources.list.d/r-project.list && \
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
COPY --chown=${RUN_USER}:${RUN_GROUP} scripts scripts
COPY --chown=${RUN_USER}:${RUN_GROUP} target/metadata-qa-ddb-${MQA_VERSION}-jar-with-dependencies.jar target/metadata-qa-ddb.jar
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

ENTRYPOINT ["supervisord", "-c", "/opt/metadata-qa-ddb/supervisord.conf"]
EXPOSE 4200
