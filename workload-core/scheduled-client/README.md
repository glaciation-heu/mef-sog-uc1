# Scheduled-client

## Build

The main objective is to create the image starting from the source code (scheduled-client folder). Copy the scheduled-client folder to a node where you have operational docker and position yourself inside the folder, then build the image:

    cd ../../scheduled-client
    sudo docker build -t scheduled-client:1.0.0 .

Export the image:

    sudo docker save -o ./scheduled-client.tar scheduled-client:1.0.0

Copy 'scheduled-client.tar' to the worker node where you plan to create the pod. In our specific case the chosen node will be worker-101.
Import 'scheduled-client.tar' into your local kubernetes repository:

    sudo ctr -n=k8s.io images import scheduled-client.tar

## Persistent Volumes (PV)

Longhorn is used as cloud storage:

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: data-mef-pvc
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 10Gi
  storageClassName: longhorn
```

## Install

Copy the 'deployment.yaml' file into the master node. 
To install the scheduled-client run the following commands:

    kubectl create ns tm-gla
    kubectl apply -f deployment.yaml -n tm-gla

The deployment file will take care of installing the volumes, the pod (on worker-101) and the service.

## Test

It is possible to carry out a preliminary test without swagger, by inserting the files into the folder:

    /home/ubuntu/volume/tm-gla/mef/timbrature/

of worker node 101, and executing the post calls:

    curl -X POST http://localhost:30001/scheduled-client/api.noipa.it/sec/workload/V1/startWorkload
    curl -X POST http://localhost:30001/scheduled-client/api.noipa.it/sec/workload/V1/stopWorkload

## Service Features - Swagger
The service provides 3 endpoints accessible via swagger at server:port/scheduled-client/swagger-ui/

The 3 endpoints are (in order of steps):
1.  /scheduled-client/api.noipa.it/sec/file/timestamps/V1/upload   
    Upload service for placing the files in the volume configured in the variable `PATH_TIMESTAMPS`

2.  /scheduled-client/api.noipa.it/sec/workload/V1/startWorkload   
    A service that starts task scheduling and sends out a message over Kafka with the file location to be processed.
    A task scheduler is set up to move all of the files from volume `PATH_TIMESTAMPS` to volume `PATH_TIMESTAMPS_TO-BE-ELABORATED` every `FIXEDRATE_WATCHING_FOLDER_SECONDS` number of seconds. For each file that is moved, a message like {"pathToFile":"%PATH_TIMESTAMPS_TO-BE-ELABORATED%\\nameFile.extension"} is sent over a Kafka topic.

3.  /scheduled-client/api.noipa.it/sec/workload/V1/stopWorkload   
    Stop-service of the task scheduler prevoiusly started

## Prometheus
In order to let [Prometheus](https://prometheus.io/) be capable of performing its analysis, a service for measuring CPU and memory resource utilization is available at: server:port/scheduled-client/actuator/prometheus

## Realization
The service was created using:

*   [Spring Boot v2.5.14](https://docs.spring.io/spring-boot/docs/2.5.14/reference/html/)
*   Scheduler: [Task Scheduling](https://docs.spring.io/spring-boot/docs/2.5.14/reference/html/features.html#features.task-execution-and-scheduling)
*   [Kafka v2.13-2.7.2](https://kafka.apache.org/27/documentation.html)
*   Swagger: [Springfox v3.0.0](http://springfox.io/)
*   [Lombok v1.18.24](https://projectlombok.org/)
*   Prometheus metrics: [Micrometer](https://docs.micrometer.io/micrometer/reference/implementations/prometheus.html)


