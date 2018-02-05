# fetch basic image
FROM ubuntu:17.10

# Install JDK with no add-ons
RUN apt-get update && \
    apt-get -y -f install openjdk-8-jdk && \
    apt-get -y -f install mysql-client && \
    apt-get -y -f install curl && \
    apt-get -y -f install iputils-ping

# Install Maven
ENV MAVEN_VERSION 3.3.9
RUN curl -sSL http://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
  && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn
ENV MAVEN_HOME /usr/share/maven

# application placed into /opt/app
RUN mkdir -p /opt/app
WORKDIR /opt/app

# selectively add the POM file and
# install dependencies
COPY pom.xml /opt/app/
RUN mvn install

# rest of the project
COPY src /opt/app/src
RUN mvn package

# local application port
EXPOSE 8080

# execute it
CMD ["mvn", "-llogs", "exec:java"]

