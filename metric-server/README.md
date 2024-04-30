# Metric-Server

## Installing

    kubectl apply -f metric-server.yaml 

Important! --> Edit file and add - --kubelet-insecure-tls to args list:

```yaml
labels:
k8s-app: metrics-server
spec:
containers:
- args:
    - --cert-dir=/tmp
    - --secure-port=443
    - --kubelet-preferred-address-types=InternalIP,ExternalIP,Hostname
    - --kubelet-use-node-status-port
    - --metric-resolution=15s
    - --kubelet-insecure-tls # add this line
      ...
```
