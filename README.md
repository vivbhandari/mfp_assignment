#1 make image

	It creates a new docker image for the server. It will pick latest changes and compile the maven project.

#2 make start

	It create host volumes if they don't exist. Also set up ha proxy config and start all microservices using docker-compose.

#3 make add-server number=unique_id_for_the_container

	Convenience target to add a new instance of server on then fly. Number needs to be unique. Cache will get updated through kafka and haproxy will get updated too.

#4 make remove-server number=unique_id_for_the_container

	Convenience target to remove one of the existing server instances on the fly. Container will be removed and haproxy will get updated too.

#5 make stop

	Convenience target to stop and remove all containers.
