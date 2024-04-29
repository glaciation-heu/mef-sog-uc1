# Kafka on Kubernetes

## 1. Namespace
Create a dedicated namespace for this Kafka cluster. A dedicated namespace allows us to isolate this cluster from other apps and is a good organizational tool for organizing resources

    kubectl create namespace kafka

## 2. Persistent Volumes (PV) 
Before installing the Kafka broker and Zookeeper, we need to finalize our persistent volume. For this use case, we went with a dedicated NFS driver which will be shared between all the Kafka brokers and zookeepers. 
We will set up a 3 broker and 3 zookeeper node setup, therefore we will need 6 persistent volumes in total. The persistent volume claims will be generated automatically by the helm chart at the runtime.

```yaml
apiVersion: v1
kind: PersistentVolume
metadata:
  name: kafka-dev-pv1
spec:
  accessModes:
    - ReadWriteMany
  capacity:
    storage: 8Gi
  nfs:
    server: 10.2.2.10 #TODO: update with correct NFS server
    path: /mnt/kafka/kafka-broker1
  volumeMode: Filesystem
---
apiVersion: v1
kind: PersistentVolume
metadata:
  name: kafka-dev-pv2
spec:
  accessModes:
    - ReadWriteMany
  capacity:
    storage: 8Gi
  nfs:
    server: 10.2.2.10 #TODO: update with correct NFS server
    path: /mnt/kafka/kafka-broker2
  volumeMode: Filesystem
---
apiVersion: v1
kind: PersistentVolume
metadata:
  name: kafka-dev-pv3
spec:
  accessModes:
    - ReadWriteMany
  capacity:
    storage: 8Gi
  nfs:
    server: 10.2.2.10 #TODO: update with correct NFS server
    path: /mnt/kafka/kafka-broker3
  volumeMode: Filesystem
---
apiVersion: v1
kind: PersistentVolume
metadata:
  name: kafka-dev-zk-pv1
spec:
  accessModes:
    - ReadWriteMany
  capacity:
    storage: 10Gi
  nfs:
    server: 10.2.2.10 #TODO: update with correct NFS server
    path: /mnt/kafka/kafka-zk1
  volumeMode: Filesystem
---
apiVersion: v1
kind: PersistentVolume
metadata:
  name: kafka-dev-zk-pv2
spec:
  accessModes:
    - ReadWriteMany
  capacity:
    storage: 10Gi
  nfs:
    server: 10.2.2.10 #TODO: update with correct NFS server
    path: /mnt/kafka/kafka-zk2
  volumeMode: Filesystem
---
apiVersion: v1
kind: PersistentVolume
metadata:
  name: kafka-dev-zk-pv3
spec:
  accessModes:
    - ReadWriteMany
  capacity:
    storage: 10Gi
  nfs:
    server: 10.2.2.10 #TODO: update with correct NFS server
    path: /mnt/kafka/kafka-zk3
  volumeMode: Filesystem
```

In case you are just testing this for POC purposes and do not have access to an NFS drive, you can utilize a hostPath. However, it is not a good practice to use hostPaths for a live environment as it has many security risks. Here is an example of creating a PV using hostPath in cluster with 1 knot:

```yaml
# Sample HostPath based PV
---
apiVersion: v1
kind: PersistentVolume
metadata:
  name: data-kafka-zookeeper-0
spec:
  capacity:
    storage: 10Gi
  volumeMode: Filesystem
  storageClassName: local-path
  accessModes:
    - ReadWriteOnce
  local:
    path: /home/ubuntu/volume/tm-gla/kafka/kafka-zk-0
  nodeAffinity:
    required:
      nodeSelectorTerms:
        - matchExpressions:
            - key: kubernetes.io/hostname
              operator: In
              values:
                - <host-work-n>
---
apiVersion: v1
kind: PersistentVolume
metadata:
  name: data-kafka-broker-0
spec:
  capacity:
    storage: 8Gi
  volumeMode: Filesystem
  storageClassName: local-path
  accessModes:
    - ReadWriteOnce
  local:
    path: /home/ubuntu/volume/tm-gla/kafka/kafka-broker-0
  nodeAffinity:
    required:
      nodeSelectorTerms:
        - matchExpressions:
            - key: kubernetes.io/hostname
              operator: In
              values:
                - <host-work-n>
---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: data-kafka-broker-0
  namespace: kafka
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 8Gi
  storageClassName: local-path
  volumeName: data-kafka-broker-0
---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: data-kafka-zookeeper-0
  namespace: kafka
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 10Gi
  storageClassName: local-path
  volumeName: data-kafka-zookeeper-0
```
Create a PersistentVolume and a PersistentVolumeClaim for each broker and zookeeper pod on each worker node by substituting the node name for <host-work-n>.

    kubectl apply -f ./config/pv.yaml


## 3. Customize Helm Values 
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

controller:
  replicaCount: 0

zookeeper:
  enabled: true
  replicaCount: 3
  podAntiAffinityPreset: hard

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

## 4. Customize Logging
We can also customize the logging of our Kafka cluster by passing a custom log4j config file in a config map.


    kubectl create configmap kafka-log4j-config -n kafka --from-file=./config/log4j.properties 

## 5. Install Helm Chart
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

Test Producer:
    
    kafka-console-producer.sh --broker-list kafka.kafka.svc.cluster.local:9092 --topic test

Test Consumer:

    kafka-console-consumer.sh --bootstrap-server kafka.kafka.svc.cluster.local:9092 --topic test --from-beginning

