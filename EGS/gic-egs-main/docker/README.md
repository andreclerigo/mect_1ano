# Docker 101

## Instalation

### Linux

1. Uninstall old versions 

```bash
sudo apt remove docker docker-engine docker.io containerd runc
sudo apt update
sudo apt-get install ca-certificates curl gnupg lsb-release
```

2. Add Docker's official GPG key

```bash
sudo mkdir -m 0755 -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
```

3. Set up the repository

```bash
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
$(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```
4. Install Docker Engine

```bash
sudo apt update
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

5. Create the docker group

```bash
sudo groupadd docker
```

6. Add your user to the docker group

```bash
sudo usermod -aG docker $USER
```

7. Log out and log in

### Windows

Follow the steps into this [link](https://docs.docker.com/desktop/windows/wsl/).

### MacOS

Follow the steps into this [link](https://docs.docker.com/desktop/install/mac-install/).
Take care to select the correct CPU type (M1 and M2 are arm based).

## Configure network

Edit the file `/etc/docker/daemon.json` and add:
```json
{
  "default-address-pools" : [
    {
      "base" : "10.139.0.0/16",
      "size" : 24
    }
  ]
}
```

## Validation

```bash
docker run hello-world
docker run -it ubuntu bash
```

## Docker registry

Create the folder `/etc/docker/certs.d/registry.deti\:5000/` and inside create a `ca.crt` file with:
```txt
-----BEGIN CERTIFICATE-----
MIICYDCCAgagAwIBAgIUBr/dXkfJFGCpBoW/tjN5x0f/KQQwCgYIKoZIzj0EAwIw
IzEhMB8GA1UEAwwYazNzLXNlcnZlci1jYUAxNjQ5NzE2MTA4MB4XDTIyMDQyMTEw
MDc1MFoXDTMyMDIyODEwMDc1MFowVDELMAkGA1UEBhMCUFQxDzANBgNVBAgMBkF2
ZWlybzEPMA0GA1UEBwwGQXZlaXJvMQswCQYDVQQLDAJVQTEWMBQGA1UEAwwNcmVn
aXN0cnkuZGV0aTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAKtQ0O2p
74NNIa1ffGDtoWQUHtZMqJOI3SQTV7f/age5PAKpomN9uWoqdd0CAnJ3Er4beYST
mtXTs6fv+hD/5EoYotE3FxyVdm1RBu5KJ1xluxbUSalDOFhJ0snwIXOs+ZcXJOo5
JkxyfarKXy7LuvQnIT8P1wUXBzNzWFRGh6xqDzLTSMsLAJoWY2GmMyF8RGWrI0No
TZrXsKpEAgJwgl45XTzbpmORoWrl5flJt9Oc1hSEIUwmVIE+HMm/4+ZhdDcelZgT
O2VPmwF0wKXC9jItrXJrUCqviddtC59kQhaDhiq0sFiWAhkQryRHITCm6GZ7J79r
2HKUFVlqz8snX1kCAwEAAaMcMBowGAYDVR0RBBEwD4INcmVnaXN0cnkuZGV0aTAK
BggqhkjOPQQDAgNIADBFAiBUznchVB46FyKfPXVJeK+6FJpgibdATuPafDzPuQF3
TAIhAMgv2vfFBGdbfEu41tsHWnxRVMV5jiELuj3mvZ34ItlD
-----END CERTIFICATE-----
```

## Examples

### App

```bash
docker build --network=host -t registry.deti:5000/prof/app:v1 .
docker push registry.deti:5000/prof/app:v1
docker run -p 8080:8080 registry.deti:5000/prof/app:v1
```

### App persistence

```bash
docker build --network=host -t registry.deti:5000/prof/app-persistence:v1 .
docker push registry.deti:5000/prof/app-persistence:v1
docker run -p 8080:8080 --mount type=bind,source="$(pwd)"/www,target=/app/www registry.deti:5000/prof/app-persistence:v1
```

### App compose

```bash
docker compose up
```

### App Swarm

Skip due to lack of access

### App config

```bash
docker compose up
```

### App secrets

```bash
docker compose up
```

## Cheat Sheet

Remove all the unused images, volumes and networks

```
docker system prune --all
```

## References

1. [Dockerfile](https://docs.docker.com/engine/reference/builder/)
2. [The Ultimate Docker Cheat Sheet](https://dockerlabs.collabnix.com/docker/cheatsheet/)
3. [Docker Commands Cheat Sheet](https://pagertree.com/blog/docker-commands-cheat-sheet)