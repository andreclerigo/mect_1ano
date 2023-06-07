const winston = require('winston');
const winstonGelf = require('winston-gelf');
const process = require('process');

const logger = winston.createLogger({
  transports: [
    new winston.transports.Console(),
    new winstonGelf({
      gelfPro: {
        fields: {
          env: process.env.NODE_ENV || 'development'
        },
        adapterName: 'udp',
        adapterOptions: {
          host: 'graylog',
          port: 12201,
        }
      }
    })
  ]
});

module.exports = logger;
