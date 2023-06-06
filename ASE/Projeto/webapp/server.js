const express = require('express');
const MongoClient = require('mongodb').MongoClient;
const app = express();
const port = 5000;
const cors = require('cors');

// Use cors middleware and allow all origins
app.use(cors());

let db, collection_dht11, collection_mq5;

(async () => {
  const mongoClient = new MongoClient("mongodb://localhost:27017", { useUnifiedTopology: true });

  try {
    await mongoClient.connect();
    console.log("Connected successfully to MongoDB server");
    db = mongoClient.db("rover");
    collection_dht11 = db.collection('dht11_data');
    collection_mq5 = db.collection('mq5_data');
    
    // Remove all data form the collection
    await collection_dht11.deleteMany({});
    await collection_mq5.deleteMany({});
  } catch (error) {
    console.error(error);     // Debugging purposes
    throw new Error("Failed to connect to the database");
  }
})();

app.use(express.json());

app.post('/dht11', (req, res) => {
  const dht11_data = {
    temperature: req.body.temperature,
    humidity: req.body.humidity,
    timestamp: new Date()
  };

  const mq5_data = { 
    ppm: req.body.mq5,
    timestamp: new Date()
  };
  
  try {
    collection_dht11.insertOne(dht11_data);
    collection_mq5.insertOne(mq5_data);
    res.status(200).send('success');
  } catch (error) {
    console.error(error);
    res.status(500).send(error);
  }
});

app.get('/dht11', async (req, res) => {
  try {
    let data = await collection_dht11.find().sort({timestamp: -1}).limit(30).toArray();
    res.status(200).json(data);
  } catch (error) {
    console.error(error);
    res.status(500).send(error);
  }
});

app.get('/mq5', async (req, res) => {
  try {
    // Get the latest ppm value
    let data = await collection_mq5.find().sort({timestamp: -1}).limit(1).toArray();
    res.status(200).json(data);
  } catch (error) {
    console.error(error);
    res.status(500).send(error);
  }
});

app.listen(port, () => {
  console.log(`Server listening at http://localhost:${port}`);
});
