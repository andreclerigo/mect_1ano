const prometheus = require('prom-client');

// Define metrics
const tweetsRequestedCounter = new prometheus.Counter({
  name: 'tweets_requested_total',
  help: 'Total number of tweets requested',
});

module.exports = tweetsRequestedCounter;
