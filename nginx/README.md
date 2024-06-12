# NGINX Reverse Proxy

To access our components and manage routes we install an Nginx reverse proxy. With which we can access the following URLs:

* Prometheus: http://10.14.1.170:8081/
* Grafana: http://10.14.1.170:8081/grafana/
* Longhorn: http://10.14.1.170:8081/longhorn/
* Kafdrop: http://10.14.1.170:8081/kafdrop/
* Scheduled-client: http://10.14.1.170:8081/scheduled-client/swagger-ui/


## Installing

Use docker compose to install the Nginx container:
    
    sudo docker-compose up -d
