const { periodicUpdateCacheFromAPI } = require('./cache');
const Queue = require('bull');
const logger = require('./logger');

// Connect to the same Redis instance
const redisConfig = { redis: { port: 6379, host: 'redis' } };
const periodicQueue = new Queue('periodicQueue', redisConfig);

// Listen for errors on the queue
periodicQueue.on('error', (error) => {
  logger.error(`[WORKER] Failed to create the queue in worker.js: ${error}`);
  console.error(`An error occurred while creating the queue in worker.js: ${error}`);
});

const startWorker = () => {
  logger.debug(`[WORKER] Worker started`);
  console.log('Started bull worker');
  
  periodicQueue.process(async (job) => {
    logger.info(`[WORKER] Consumed periodic update queue process`);
    console.log('Consumed periodic update queue process');
    //periodicUpdateCacheFromAPI(job.data); 
  });
};

module.exports = { startWorker };
