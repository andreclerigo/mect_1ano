const express = require('express');
const MongoClient = require('mongodb').MongoClient;
const app = express();
const port = 5000;
const cors = require('cors');

// Use cors middleware and allow all origins
app.use(cors());

let db, collection;

(async () => {
  const mongoClient = new MongoClient("mongodb://localhost:27017", { useUnifiedTopology: true });

  try {
    await mongoClient.connect();
    console.log("Connected successfully to MongoDB server");
    db = mongoClient.db("rsa");
    collection = db.collection('batman');
    
    // Remove all data form the collection
    await collection.deleteMany({});
  } catch (error) {
    console.error(error);     // Debugging purposes
    throw new Error("Failed to connect to the database");
  }
})();

app.use(express.json());

app.post('/batman', (req, res) => {
  const data = {
    "rtt_min": req.body.rtt_min,
    "rtt_avg": req.body.rtt_avg,
    "rtt_max": req.body.rtt_max,
    "rtt_mdev": req.body.rtt_mdev,
    "jitter": req.body.jitter,
    "pdr": req.body.pdr,
    "timestamp": new Date()
  }
  
  try {
    collection.insertOne(data);
    res.status(200).send('success');
  } catch (error) {
    console.error(error);
    res.status(500).send(error);
  }
});

app.get('/batman', async (req, res) => {
  try {
    let data = await collection.find({}).sort({timestamp: -1}).toArray();
    // console.log(data);
    res.status(200).send(data);
  } catch (error) {
    console.error(error);
    res.status(500).send(error);
  }
});

app.listen(port, () => {
  console.log(`Server listening at http://localhost:${port}`);
});
