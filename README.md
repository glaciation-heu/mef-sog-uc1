# MEF - SOGEI - UC1

The UC1 use case is presented in the doc: D2.1 provides the GLACIATION platform architecture blueprint together with the service components definition.

## Component Overview:

- **Description**: Distributed Knowledge Graph refers to the Novel Metadata Fabric of the GLACIATION platform, contains a Use Case that requires the following components:
* [LongHorn](./longhorn/README.md)
* [Metric-Server component](./metric-server/README.md)
* [Prometheus & Grafana ](./monitoring/README.md)
* [Kafka component](./kafka/README.md)
* [Kafdrop](./kafdrop/README.md)
* [KEDA](./keda/README.md)
* [Scheduled-client component](./workload-core/scheduled-client/README.md)
* [Elaboration-timb component](./workload-core/elaboration-timb-ms/README.md)
* [NGINX Reverse Proxy](./nginx/README.md)
* [Test File](./file-timbrature/README.md)

## Service Dependencies:
For each component, there will be separate ```.md``` file under the ```docs``` folder of each service describing its dependencies.

## Service Architecture:
- DeamonSet in K8S cluster, 1 for each node, 4 in total in Dell setting.
