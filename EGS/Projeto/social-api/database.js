const { MongoClient } = require("mongodb");
const { deleteFromCache } = require("./cache");
const logger = require('./logger');

const fetchFromDatabase = async (collectionName, param, paramValue, dbQuery) => {
  const mongoClient = new MongoClient("mongodb://mongodb:27017");
  
  try {
    await mongoClient.connect();
    const database = mongoClient.db("mixit");
    const collection = database.collection(collectionName);
    let data = await collection.findOne({ [param]: paramValue });

    if (data) {
      // Filtering for database local fields
      data = Object.fromEntries(
        Object.entries(data).filter(([key, value]) => dbQuery.includes(key))
      );
    }
    logger.info(`[DATABASE] Fetched data from ${collectionName} collection with ${param}: ${paramValue}`);
    return data;
  } catch (error) {
    logger.error({ message: `[DATABASE] Failed to fetch data from ${collectionName} collection: ${error.message}`, error });
    console.error(error);     // Debugging purposes
    throw new Error("Failed to fetch data from database");
  } finally {
    await mongoClient.close();
  }
};
  
const addToDatabase = async (collectionName, data) => {
  const mongoClient = new MongoClient("mongodb://mongodb:27017");
  
  try {
    await mongoClient.connect();
    const database = mongoClient.db("mixit");
    const collection = database.collection(collectionName);
    await collection.insertOne(data);
    logger.info(`[DATABASE] Added data to ${collectionName} collection`);
  } catch (error) {
    logger.error({ message: `[DATABASE] Failed to add data to ${collectionName} collection: ${error.message}`,error });
    console.error(error);     // Debugging purposes
    throw new Error("Failed to post data from database");
  } finally {
    await mongoClient.close();    
  }
};

const updateDatabase = async (collectionName, param, paramValue, newValues) => {
  const mongoClient = new MongoClient("mongodb://mongodb:27017");

  try {
    await mongoClient.connect();
    const database = mongoClient.db("mixit");
    const collection = database.collection(collectionName);
    let data = await collection.updateOne({ [param]: paramValue }, { $set: newValues});
    data = await collection.findOne({ [param]: paramValue });

    logger.info(`[DATABASE] Updated data in ${collectionName} collection with ${param}: ${paramValue}`);
    return data;
  } catch (error) {
    logger.error({ message: `[DATABASE] Failed to update data in ${collectionName} collection: ${error.message}`, error });
    console.error(error);     // Debugging purposes
    throw new Error("Failed to update data from database");
  } finally {
    await mongoClient.close();    
  }
};

const deleteFromDatabase = async (collectionName, param, paramValue) => {
  const mongoClient = new MongoClient("mongodb://mongodb:27017");

  try {
    await mongoClient.connect();
    const database = mongoClient.db("mixit");
    const collection = database.collection(collectionName);
    const data = await collection.findOne({ [param]: paramValue });

    if (data) {
      await collection.deleteOne({ [param]: paramValue });
      let user = await collection.findOne({ "external_information.id": data.external_information.id });
      // Delete the data from the cache
      if (!user) await deleteFromCache(data.external_information.id);
    }

    logger.warning(`[DATABASE] Deleted data from ${collectionName} collection with ${param}: ${paramValue}`);
    return data;
  } catch (error) {
    logger.error({ message: `[DATABASE] Failed to delete data from ${collectionName} collection: ${error.message}`, error });
    console.error(error);     // Debugging purposes
    throw new Error("Failed to delete data from database");
  } finally {
    await mongoClient.close();    
  }
};

module.exports = { fetchFromDatabase, addToDatabase, updateDatabase, deleteFromDatabase };
