# MEF - SOGEI - UC1

Il caso d'uso UC1 è presentato all'interno del doc: D2.1 provides the GLACIATION platform architecture blueprint together with the service components definition.

## Component Overview:

- **Description**: Distributed Knowledge Graph refers to the Novel Metadata Fabric of the GLACIATION platform, contains a Use Case with 3 components as follows:
* [Metric-Server component on K8S](./metric-server/README.md)
* [Kafka component](./kafka/README.md)
* [scheduled-client component](./scheduled-client/README.md)

## Service Dependencies:
For each component, there will be separate ```.md``` file under the ```docs``` folder of each service describing its dependencies.

## Service Architecture:
- DeamonSet in K8S cluster, 1 for each node, 4 in total in Dell setting.
