# Elaboration-timb-ms

## Description
The purpose of the microservice is to analyze the file containing employees' entrance and exit timestamps according to the pattern expected and generate an output file with normalized data while identifying any errors that may have occurred during this process.

------------------------------------------------------------

## Prerequisites

Complete the installation of [scheduled-client](../scheduled-client/README.md).

## Build

The main objective is to create the image starting from the source code (elaboration-timb-ms folder). Copy the elaboration-timb-ms folder to a node where you have operational docker and position yourself inside the folder, then build the image:

    cd ../../elaboration-timb-ms
    sudo docker build -t elaboration-timb-ms:1.0.0 .

Export the image:

    sudo docker save -o ./elaboration-timb-ms.tar elaboration-timb-ms:1.0.0 


Copy 'elaboration-timb-ms.tar' to the worker node where you plan to create the pod. In our specific case the chosen node will be worker-101.
Import 'elaboration-timb-ms.tar' into your local kubernetes repository:

    sudo ctr -n=k8s.io images import elaboration-timb-ms.tar

## Persistent Volumes (PV)

The Persistent Volume associated to pod has been already declared in [scheduled-client](../scheduled-client/README.md#persistent-volumes-pv).

## Install

Copy the 'deployment.yaml' file into the master node. 
To install the elaboration-timb-ms run the following commands:

    kubectl apply -f deployment.yaml -n tm-gla

The deployment file will take care of installing the pod (on worker-101).

------------------------------------------------------------

## Service Features
The microservice is listening over Kafka topic specified in environment variable `SPRING_KAFKA_LISTENER_TOPIC` and receives a message like {"pathToFile":"folder\\nameFileToElaborate.extension"}

As soon as the KafkaListener consumes the message, the software analyzes the timestamps data contained within the file indicated in the message. At the end of the process, the file is moved from its location to:
- the folder of completed files, if the analysis is completed successfully (`PATH_TIMESTAMPS_ACQUIRED`)
- the folder of discarded files, otherwise (`PATH_TIMESTAMPS_DISCARDED`)

Furthermore, it is produced a file with the results of the elaboration in the location `PATH_TIMESTAMPS_ELABORATED`\\nameFileToElaborate.result.

------------------------------------------------------------


## Prometheus
In order to let [Prometheus](https://prometheus.io/) be capable of performing its analysis, a service for measuring CPU and memory resource utilization is available at: server:port/elaboration-timb-ms/actuator/prometheus

------------------------------------------------------------


## Realization
The service was created using:

*   [Spring Boot v2.5.14](https://docs.spring.io/spring-boot/docs/2.5.14/reference/html/) 
*   [Kafka v2.13-2.7.2](https://kafka.apache.org/27/documentation.html)
*   [Lombok v1.18.24](https://projectlombok.org/)
*   Prometheus metrics: [Micrometer](https://docs.micrometer.io/micrometer/reference/implementations/prometheus.html)
*   In-memory Database [H2](https://www.h2database.com/html/main.html)
