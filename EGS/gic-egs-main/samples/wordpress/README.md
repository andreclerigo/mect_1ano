# Deploy WordPress on K3S

This tutorial shows you how to deploy a WordPress site and a MySQL database using K3S.
Both applications use PersistentVolumes and PersistentVolumeClaims to store data.

This tutorial uses 3 files:
1. **kustomization** that has the information of the database password as a secret
2. **mysql** the deployment of a single node for the database
3. **wordpress** a single deployment of a wordpress instance

**Note:** to try this deployment please change the namespace in all configuration files.

Run the following commands:
```bash
kubectl apply -k ./
kubectl get secrets -n prof
kubectl get pvc -n prof
kubectl get pods -n prof
kubectl get services -n prof
kubectl get ingress -n prof
```

Access the service through [here](wordpress-prof.k3s).

To remove the deployment simpy run:
```bash
kubectl delete -k ./
```
