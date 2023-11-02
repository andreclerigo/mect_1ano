# Diving into docker

# Base image
FROM python:3.8-alpine

# Author of the dockerfile
MAINTAINER Joao Paulo Barraca

# Label
LABEL version="1.0"

# Execute the command to create a folder
RUN mkdir /app

# Define the working directory of a Docker container
WORKDIR /app

# Copy external files into the working directory
COPY requirements.txt requirements.txt

# Execute the pip to install the dependencies
RUN pip install -r requirements.txt

# Copy the external code into the working directory
COPY app.py app.py

# Expose the port of the service
EXPOSE 8080/tcp

# Define the entrypoint
ENTRYPOINT ["python3", "app.py"]
