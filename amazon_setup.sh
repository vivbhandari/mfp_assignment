#!/bin/sh
sudo yum update -y
sudo yum install -y git
sudo yum install -y docker
sudo wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
sudo sed -i s/\$releasever/6/g /etc/yum.repos.d/epel-apache-maven.repo
sudo yum install -y apache-maven
sudo yum -y groupinstall "Development Tools"
sudo yum install -y make
sudo yum install -y bash-completion --enablerepo=epel
sudo curl -L https://github.com/docker/compose/releases/download/1.14.0/docker-compose-`uname -s`-`uname -m` > docker-compose  
sudo chown root docker-compose
sudo mv docker-compose /usr/local/bin  
sudo chmod +x /usr/local/bin/docker-compose  
sudo gpasswd -a ec2-user docker  
sudo service docker restart  
newgrp docker
git clone https://github.com/vivekassignments/mfp_assignment /home/ec2-user/mfp_assignment
sudo chown -R ec2-user:docker /home/ec2-user/mfp_assignment
/etc/bash_completion
