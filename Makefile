.PHONY: all test clean

IMAGE=vivekassignments/mfp_server
VERSION=6

prepare:
	export PATH=/usr/local/apache-maven-3.5.0/bin:$PATH
	mvn install

create-host-volume:
	mkdir -p host_volume/haproxy1
	mkdir -p host_volume/mysql1
	mkdir -p host_volume/kafka1

start-dev:
	sudo launchctl load -F /Library/LaunchDaemons/com.oracle.oss.mysql.mysqld.plist
	mvn package
	mvn exec:java

image:
	docker build -t $(IMAGE) .

tag:
	docker tag $(IMAGE):latest $(IMAGE):$(VERSION)

start:
	make create-host-volume
	cp haproxy/haproxy_pristine.cfg host_volume/haproxy1/haproxy.cfg
	echo "    server mfpassignment_server1_1 mfpassignment_server1_1:8080 check" >> ./host_volume/haproxy1/haproxy.cfg
	echo "    server mfpassignment_server2_1 mfpassignment_server2_1:8080 check" >> ./host_volume/haproxy1/haproxy.cfg
	docker-compose -f docker-compose.yml up -d

add-server:
	docker run -dt --tty --net mfpassignment_mynet123 --name mfpassignment_server$(number)_1 --hostname mfpassignment_server$(number)_1 $(IMAGE):$(VERSION)
	echo "    server mfpassignment_server$(number)_1 mfpassignment_server$(number)_1:8080 check" >> ./host_volume/haproxy1/haproxy.cfg
	docker kill -s HUP mfpassignment_haproxy1_1

remove-server:
	docker stop mfpassignment_server$(number)_1
	docker rm mfpassignment_server$(number)_1
	sed -i.bak "/server mfpassignment_server$(number)_1 mfpassignment_server$(number)_1:8080 check/d" ./host_volume/haproxy1/haproxy.cfg
	docker kill -s HUP mfpassignment_haproxy1_1

stop:
	docker stop `docker ps --no-trunc -aq`
	docker rm `docker ps --no-trunc -aq`

sits:
	make stop
	docker ps -a
	make image
	make tag
	make start
	docker ps -a

clean-classes:
	mvn clean

clean-db:
	rm -rf host_volume/mysql1/*

clean-all:
	make clean-classes
	make clean-db
