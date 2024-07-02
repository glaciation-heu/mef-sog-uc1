# Kafka on Kubernetes

## 1. Namespace
Create a dedicated namespace for this Kafka cluster. A dedicated namespace allows us to isolate this cluster from other apps and is a good organizational tool for organizing resources

    kubectl create namespace kafka

## 2. Customize Helm Values 
The helm chart usually comes with a default set of values. We can override the values with our values.yaml at the runtime. Below is a sample values.yaml file for a Kafka Cluster comprising of 3 brokers and 3 zookeepers in PLAINTEXT mode, and HA configuration. We have added podAntiAffinityPreset: hard to make sure that no two broker/zookeeper pods are on the same node. These Kafka brokers will be accessed via Nodeport on the specified ports.

```yaml
image:
  tag: 3.5.1-debian-11-r25 #update as per latest chart version
  pullPolicy: Always

existingLog4jConfigMap: "kafka-log4j-config"

kraft:
  enabled: false

auth:
  interBrokerProtocol: tls

broker:
  replicaCount: 3
  podAntiAffinityPreset: hard
  persistence:
    enabled: true
    storageClass: longhorn
    accessModes:
      - ReadWriteOnce
    size: 2Gi

controller:
  replicaCount: 0

zookeeper:
  enabled: true
  replicaCount: 3
  podAntiAffinityPreset: hard
  persistence:
    enabled: true
    storageClass: longhorn
    accessModes:
      - ReadWriteOnce
    size: 5Gi

listeners:
  client:
    protocol: SSL

  interbroker:
    protocol: SSL

  external:
    protocol: SSL

externalAccess:
  enabled: false
```

## 3. Install Helm Chart
We are now ready to install the Helm Chart in our cluster. This will install two StatefulSets — kafka-broker and kafka-zookeeper which will further manage the pods.


    helm install kafka ./kafka -f values-prod.yaml -n kafka

Kafka can be accessed by consumers via port 9092 on the following DNS name from within your cluster:

    kafka.kafka.svc.cluster.local

Each Kafka broker can be accessed by producers via port 9092 on the following DNS name(s) from within your cluster:

    kafka-broker-0.kafka-broker-headless.kafka.svc.cluster.local:9092

# Kafka-client
## Install
Kafka client is a utility pod, inside it has several tools that integrate with kafka.
Create a pod that you can use as a Kafka client run the following commands:

    kubectl run kafka-client --restart='Never' --image docker.io/bitnami/kafka:3.5.1-debian-11-r25 --namespace kafka --command -- sleep infinity

```
kubectl exec --tty -i kafka-client --namespace kafka -- bash
cd /opt/bitnami/kafka/bin/ --> ls

connect-distributed.sh	      
connect-mirror-maker.sh       
connect-standalone.sh	      
kafka-acls.sh		      
kafka-broker-api-versions.sh  
kafka-cluster.sh	      
kafka-configs.sh	      
kafka-console-consumer.sh    
kafka-console-producer.sh    
kafka-consumer-groups.sh	   
kafka-consumer-perf-test.sh  
kafka-delegation-tokens.sh   
kafka-delete-records.sh	   
kafka-dump-log.sh		   
kafka-e2e-latency.sh      
kafka-features.sh	     
kafka-get-offsets.sh      
kafka-jmx.sh		     
kafka-leader-election.sh  
kafka-log-dirs.sh	     
kafka-metadata-quorum.sh  
kafka-metadata-shell.sh	    
kafka-mirror-maker.sh	    
kafka-producer-perf-test.sh    
kafka-reassign-partitions.sh   
kafka-replica-verification.sh  
kafka-run-class.sh		    
kafka-server-start.sh	    
kafka-server-stop.sh		
kafka-storage.sh
kafka-streams-application-reset.sh	
kafka-topics.sh			
kafka-transactions.sh		
kafka-verifiable-consumer.sh	
kafka-verifiable-producer.sh
trogdor.sh
zookeeper-security-migration.sh
zookeeper-server-start.sh
zookeeper-server-stop.sh
zookeeper-shell.sh
```

## Test
Log in to the kafka-client pod:

    kubectl exec --tty -i kafka-client --namespace kafka -- bash

Topic:

    kafka-topics.sh --create --bootstrap-server kafka.kafka.svc.cluster.local:9092 --replication-factor 1 --partitions 1 --topic test
    kafka-topics.sh --delete --bootstrap-server kafka.kafka.svc.cluster.local:9092 --topic test
    kafka-topics.sh --list --bootstrap-server kafka.kafka.svc.cluster.local:9092
    kafka-topics.sh --alter --bootstrap-server kafka.kafka.svc.cluster.local:9092 --topic test --replication-factor 1

    kafka-topics.sh --bootstrap-server kafka.kafka.svc.cluster.local:9092 --alter --topic test --partitions 12

Test Producer:
    
    kafka-console-producer.sh --broker-list kafka.kafka.svc.cluster.local:9092 --topic test

Test Consumer:

    kafka-console-consumer.sh --bootstrap-server kafka.kafka.svc.cluster.local:9092 --topic test --from-beginning

