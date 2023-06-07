const express = require('express')
const app = express()
const fs = require('fs');
const swaggerUI = require('swagger-ui-express');
const swaggerJsDoc = require('swagger-jsdoc');
const cors = require('cors');
const path = require('path');
const prometheus = require('prom-client');
require('dotenv').config();

const static = require('./static');
const package = require('./package.json');

const PORT = process.env.PORT || static.PORT;
const IP = process.env.IP || "127.0.0.1";
const PROTOCOL = process.env.PROTOCOL ? "https" : "http";
// const URL = PROTOCOL + "://" + IP + ":" + (process.env.PORT ? process.env.PORT : PORT);
const URL = "http://social-api-mixit.deti";


// Expose public content
app.use('/public', express.static(path.join(__dirname, 'public')));

// Enable Cors Middleware
app.use(cors({
  origin: '*',
  methods: ['GET', 'POST', 'PUT', 'DELETE'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));
app.options('*', cors());

// Setup Swagger Docs
const swagger_options = {
  definition: {
    openapi: "3.0.3",
    info: {
      title: package["name"],
      version: package["version"],
      description: package["description"],
      contact: { name: "André Clérigo", email: "andreclerigo@ua.pt" }
    },
    servers: [{ url: URL, description: "Social API" }]
  },
  apis: ["./routes/*.js", "./docs/mixit-social-api.yml"],
}

// Swagger Docs customization
const swagger_specs = swaggerJsDoc(swagger_options);
app.use("/v1/docs", swaggerUI.serve, swaggerUI.setup(swagger_specs, {
  customSiteTitle: "MIXIT Social API",
  customfavIcon: "/public/mixit-favicon.png"
}));

// Redirect API root to docs webpage
app.get('/', (req, res) => {
  res.redirect('/v1/docs'); 
})

app.get('/v1/docs.json', (req, res) => {
  res.setHeader('Content-Type', 'application/json');
  res.send(swagger_specs);
});


// Setup routes
fs.readdir(static.ROUTES_DIR, (err, files) => {
  files.forEach(file => app.use(`/v1/${file.replace(".js", "")}`, require(static.ROUTES_DIR + file.replace(".js", ""))));

  // Middleware to handle 404 when nothing else responded
  app.use((req, res, next) => {
    res.status(404).json({
      "message": `404 | Endpoint ${req.url} Not Found!`,
      "url": URL+req.url,
      "timestamp": new Date().toISOString()
    });
    
    return;
  });
});

// Metrics endpoint
app.get('/metrics', async (req, res) => {
  try {
    const metrics = await prometheus.register.metrics();
    res.set('Content-Type', prometheus.register.contentType);
    res.send(metrics);
  } catch (error) {
    console.error('Error generating metrics:', error);
    res.status(500).send('Error generating metrics');
  }
});

// Startup Message
app.listen(PORT, () => {
  console.log(`App listening on ${URL}`)
})
