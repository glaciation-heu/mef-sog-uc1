# KEDA

## Description
Scale applications based on an Apache Kafka topic or other services that support Kafka protocol.

## Install

The same as before we will install KEDA on Kubernetes with Helm. Let’s add the following Helm repo:

    helm repo add kedacore https://kedacore.github.io/charts

Don’t forget to update the repository. We will install the operator in the keda namespace. Let’s create the namespace first:

    kubectl create namespace keda

Finally, we can install the operator:

    helm install keda kedacore/keda --namespace keda

## Reference

* https://piotrminkowski.com/2022/01/18/autoscaling-on-kubernetes-with-keda-and-kafka/
* https://keda.sh/docs/2.14/scalers/apache-kafka/


