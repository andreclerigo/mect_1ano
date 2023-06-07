# Node.js LTS image
FROM node:lts-alpine

# Working directory for the app
WORKDIR /usr/src/app

# Copy package.json and package-lock.json files to the working directory
COPY package*.json ./

# Install the app dependencies
RUN npm install

# Copy the app source code to the working directory
COPY . .

ENV PORT=3000

# Expose the port that the app will run on
EXPOSE 3000

# Start the app
CMD [ "npm", "run", "dev" ]
