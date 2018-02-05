#!/bin/sh
sudo apt-get update
sudo apt-get -y -f install git
sudo apt-get -y -f install docker
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install -y -f oracle-java8-installer
wget http://www-eu.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
sudo tar -xvzf apache-maven-3.3.9-bin.tar.gz
export M2_HOME=/home/vicky_bhandari/maven
export PATH=${M2_HOME}/bin:${PATH}
sudo apt-get install make
sudo apt-get install apt-transport-https ca-certificates curl software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
sudo apt-get update
sudo apt-get -y -f install docker-ce
sudo curl -L https://github.com/docker/compose/releases/download/1.18.0/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
