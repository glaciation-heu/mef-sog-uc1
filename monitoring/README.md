# Monitoring: Grafana & Prometheus

## Create the namespace — monitoring

    kubectl create namespace monitoring

## Installing kube-prometheus-stack with Helm

Run the below command to add the Prometheus-community helm repo.

    helm repo add prometheus-community https://prometheus community.github.io/helm-charts

Next, run the below command after adding the Helm repo to deploy the kube-prometheus-stack Helm chart.

    helm install prometheus prometheus-community/kube-prometheus-stack -f values.yaml --namespace monitoring

To confirm and check your kube-prometheus-stack deployment, use the command below.

    kubectl get all -n monitoring


## Accessing the Prometheus UI

Open your web browser and navigate to the URLs below to access Prometheus UI.
http://YOUR-MASTER-NODE-PUBLIC-IP:NodePort

    Ex: http://18.143.107.40:30090

## Accessing the Grafana Dashboard

Grafana was automatically installed and configured during the kube-prometheus-stack helm deployment, so you can configure Grafana access on your Cluster.

To access your Grafana dashboard, run the following command to change the “Type” of the prometheus-grafana service from ClusterIP to NodePort.

    kubectl --namespace monitoring patch svc prometheus-grafana -p '{"spec": {"type": "NodePort"}}'

Open your web browser and navigate to the URLs below to access the Grafana dashboard.
http://YOUR-MASTER-NODE-PUBLIC-IP:NodePort

    Ex: http://18.143.107.40:32021

If your Grafana service is working, your web browser should display the following login page.

The default.

    — Username: admin
    — Password: prom-operator

To find out the password if necessary:

    kubectl get secret --namespace monitoring prometheus-grafana  -o jsonpath="{.data.admin-password}" | base64 --decode ; echo


## Prometheus Target bug fix

If you encounter a problem with Prometheus targets, in which the following pointings are down:
* Targets kube-prometheus-stack-kube-controller-manager are down
* Targets kube-prometheus-stack-kube-etcd are down
* Targets kube-prometheus-stack-kube-proxy are down
* Targets kube-prometheus-stack-kube-scheduler are down

Follow the tickets: 
* https://github.com/prometheus-community/helm-charts/issues/204
* https://stackoverflow.com/questions/60734799/all-kubernetes-proxy-targets-down-prometheus-operator
* https://stackoverflow.com/questions/65901186/kube-prometheus-stack-issue-scraping-metrics/66276144#66276144

## Reference

Installation from website: https://milindasenaka96.medium.com/setup-prometheus-and-grafana-to-monitor-the-k8s-cluster-e1d35343d7a9
Another type of installation: https://medium.com/@gayatripawar401/deploy-prometheus-and-grafana-on-kubernetes-using-helm-5aa9d4fbae66
Another type of installation: https://www.bigbinary.com/blog/prometheus-and-grafana-integration
Installation Grafana official website: https://grafana.com/docs/grafana/latest/setup-grafana/installation/kubernetes/
Prometheus specific configuration: https://spacelift.io/blog/prometheus-helm-chart
