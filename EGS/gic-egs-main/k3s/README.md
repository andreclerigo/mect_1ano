# Kubernetes 101

## Terminology

Node
: A worker machine in Kubernetes, part of a cluster.

Cluster
: A set of Nodes that run containerized applications managed by Kubernetes. 
For this example, and in most common Kubernetes deployments, nodes in the cluster are not part of the public internet.

Edge router
: A router that enforces the firewall policy for your cluster. 
This could be a gateway managed by a cloud provider or a physical piece of hardware.

Cluster network
: A set of links, logical or physical, that facilitate communication 
within a cluster according to the Kubernetes networking model.

Service
: A Kubernetes Service that identifies a set of Pods using label selectors. 
Unless mentioned otherwise, Services are assumed to have virtual IPs only routable within the cluster network.

Pods
: Pods are the smallest deployable units of computing that you can create and manage in Kubernetes.
A Pod is a group of one or more containers, with shared storage and network resources, and a specification for how to run the containers.

## Instalation

### kubectl

The Kubernetes command-line tool, [kubectl](https://kubernetes.io/docs/tasks/tools/),
allows you to run commands against Kubernetes clusters.
You can use kubectl to deploy applications, inspect and manage cluster resources, and view logs.

1. Download the latest release with the command (linux 64 bits):
```bash
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
```
2. Install kubectl

```bash
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
```

3. Install the cluster config

```bash
mkdir -p ~/.kube/
cp -vf config ~/.kube/config
```

### Lens

Lens Desktop is the only application you need to take control of your Kubernetes clusters.
It's built on open source and free.

Download the application from [here](https://k8slens.dev/desktop.html).

## Validation

```bash
kubectl get nodes
```

## Examples

Create a namespace for yourself (use your mechanographic number):

```bash
kubectl create namespace [mec]
```

In Kubernetes, namespaces provides a mechanism for isolating groups 
of resources within a single cluster.
Names of resources need to be unique within a namespace, but not
across namespaces.

```bash
kubectl get namespaces
```

### App

A DaemonSet ensures that all (or some) Nodes run a copy of a Pod.
As nodes are added to the cluster, Pods are added to them.
As nodes are removed from the cluster, those Pods are garbage collected.
Deleting a DaemonSet will clean up the Pods it created.

```bash
docker buildx build --platform linux/amd64 --network=host -t registry.deti:5000/prof/app:v1 .
docker push registry.deti:5000/prof/app:v1

kubectl apply -f deployment.yaml
kubectl get pods -n prof

kubectl delete -f deployment.yaml
```

### App service

Service is a method for exposing a network application that is running 
as one or more Pods in your cluster.
A key aim of Services in Kubernetes is that you don't need to modify 
your existing application to use an unfamiliar service discovery mechanism. 
You can run code in Pods, whether this is a code designed for a 
cloud-native world, or an older app you've containerized. 
You use a Service to make that set of Pods available on the 
network so that clients can interact with it.

```bash
docker buildx build --platform linux/amd64 --network=host -t registry.deti:5000/prof/app-persistence:v1 .
docker push registry.deti:5000/prof/app-persistence:v1

kubectl apply -f deployment.yaml
kubectl get pods -n prof
kubectl get services -n prof

kubectl delete -f deployment.yaml
```

### App routing

Ingress exposes HTTP and HTTPS routes from outside the cluster to services within the cluster. 
Traffic routing is controlled by rules defined on the Ingress resource.

```bash
docker buildx build --platform linux/amd64 --network=host -t registry.deti:5000/prof/app-routing:v1 .
docker push registry.deti:5000/prof/app-routing:v1

kubectl apply -f deployment.yaml
kubectl get pods -n prof
kubectl get services -n prof
kubectl get ingress -n prof

kubectl delete -f deployment.yaml
```

### App lb

Load balancing refers to efficiently distributing incoming network traffic across a group 
of backend servers, also known as a server farm or server pool.

A ReplicaSet's purpose is to maintain a stable set of replica Pods running at any given time. 
As such, it is often used to guarantee the availability of a specified number of identical Pods.

```bash
docker buildx build --platform linux/amd64 --network=host -t registry.deti:5000/prof/app-lb:v1 .
docker push registry.deti:5000/prof/app-lb:v1

kubectl apply -f deployment.yaml
kubectl get pods -n prof
kubectl get services -n prof
kubectl get ingress -n prof

kubectl delete -f deployment.yaml
```

### App custom lb

```bash
docker buildx build --platform linux/amd64 --network=host -t registry.deti:5000/prof/app-custom-lb:v1 -f Dockerfile.app .
docker buildx build --platform linux/amd64 --network=host -t registry.deti:5000/prof/nginx:v1 -f Dockerfile.nginx .
docker push registry.deti:5000/prof/app-custom-lb:v1
docker push registry.deti:5000/prof/nginx:v1

kubectl apply -f deployment.yaml
kubectl get pods -n prof
kubectl get services -n prof
kubectl get ingress -n prof

kubectl delete -f deployment.yaml
```

### Volumes

A volume can be thought of as a directory which is accessible to the containers in a pod.
We have different types of volumes in Kubernetes and the type defines how the volume is created and its content.

Persistent Volume (PV)
: It’s a piece of network storage that has been provisioned by the administrator.
It’s a resource in the cluster which is independent of any individual pod that uses the PV.

Persistent Volume Claim (PVC)
: The storage requested by Kubernetes for its pods is known as PVC.
The user does not need to know the underlying provisioning.
The claims must be created in the same namespace where the pod is created.

Longhorn is a lightweight, reliable, and powerful distributed block storage system for Kubernetes.

Longhorn implements distributed block storage using containers and microservices.
Longhorn creates a dedicated storage controller for each block device volume and synchronously 
replicates the volume across multiple replicas stored on multiple nodes.
The storage controller and replicas are themselves orchestrated using Kubernetes.

```bash
docker buildx build --platform linux/amd64 --network=host -t registry.deti:5000/prof/app-volumes:v1 -f Dockerfile.app .
docker buildx build --platform linux/amd64 --network=host -t registry.deti:5000/prof/nginx-volumes:v1 -f Dockerfile.nginx .
docker push registry.deti:5000/prof/app-volumes:v1
docker push registry.deti:5000/prof/nginx-volumes:v1

kubectl apply -f storage.yaml
kubectl get pvc -n prof

kubectl apply -f deployment.yaml
kubectl get pods -n prof
kubectl get services -n prof
kubectl get ingress -n prof

kubectl cp www/* prof/[nginx-pod]:/var/www/static

kubectl exec -it [nginx-pod] -n prof -- /bin/bash

kubectl delete -f deployment.yaml
kubectl delete -f storage.yaml
```

### Configs

A ConfigMap is an API object used to store non-confidential data in key-value pairs. 
Pods can consume ConfigMaps as environment variables, command-line arguments, or as 
configuration files in a volume.

A ConfigMap allows you to decouple environment-specific configuration from your container 
images, so that your applications are easily portable.

```bash
docker buildx build --platform linux/amd64 --network=host -t registry.deti:5000/prof/app-config:v1 -f Dockerfile.app .
docker push registry.deti:5000/prof/app-config:v1

kubectl apply -f storage.yaml
kubectl get pvc -n prof

kubectl apply -f deployment.yaml
kubectl get pods -n prof
kubectl get services -n prof
kubectl get ingress -n prof

kubectl cp www/* prof/[nginx-pod]:/var/www/static
kubectl exec -it [nginx-pod] -n prof -- /bin/bash

kubectl delete -f deployment.yaml
kubectl delete -f storage.yaml
```

### Secrets

A Secret is an object that contains a small amount of sensitive data such as a 
password, a token, or a key. Such information might otherwise be put in a Pod 
specification or in a container image. Using a Secret means that you don't need 
to include confidential data in your application code.

```bash
docker buildx build --platform linux/amd64 --network=host -t registry.deti:5000/prof/app-secret:v1 -f Dockerfile.app .
docker push registry.deti:5000/prof/app-secret:v1

kubectl apply -f storage.yaml
kubectl get pvc -n prof

kubectl create secret generic app-secret --from-file=secret -n prof
kubectl get secrets -n prof

kubectl apply -f deployment.yaml
kubectl get pods -n prof
kubectl get services -n prof
kubectl get ingress -n prof

kubectl cp www/* prof/[nginx-pod]:/var/www/static

kubectl delete -f deployment.yaml
kubectl delete -f storage.yaml
kubectl delete secret app-secret -n prof
```

### Affinity

You can constrain a Pod so that it is restricted to run on particular node(s), 
or to prefer to run on particular nodes. There are several ways to do this and t
the recommended approaches all use label selectors to facilitate the selection. 
Often, you do not need to set any such constraints; the scheduler will automatically 
do a reasonable placement (for example, spreading your Pods across nodes so as not 
place Pods on a node with insufficient free resources). However, there are some 
circumstances where you may want to control which node the Pod deploys to, for 
example, to ensure that a Pod ends up on a node with an SSD attached to it, or to 
co-locate Pods from two different services that communicate a lot into the same 
availability zone.

```bash
docker buildx build --platform linux/amd64 --network=host -t registry.deti:5000/prof/app-affinity:v1 -f Dockerfile.app .
docker buildx build --platform linux/amd64 --network=host -t registry.deti:5000/prof/nginx-affinity:v1 -f Dockerfile.nginx .
docker push registry.deti:5000/prof/app-affinity:v1
docker push registry.deti:5000/prof/nginx-affinity:v1

kubectl apply -f deployment.yaml
kubectl get pods -n prof
kubectl get services -n prof
kubectl get ingress -n prof

kubectl delete -f deployment.yaml
```

## References

1. [kubernetes.io](https://kubernetes.io/docs/home/)
2. [longhorn](https://longhorn.io/)
3. [lens](https://k8slens.dev/)
4. [Traefik](https://www.virtualizationhowto.com/2022/05/traefik-ingress-example-yaml-and-setup-in-k3s/)
5. [kubectl Cheat Sheet](https://kubernetes.io/docs/reference/kubectl/cheatsheet/)