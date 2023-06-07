const express = require('express');
const router = express.Router();
const { handleUserPostResponse, handleTimelineResponse, handlePostResponse, handlePostRepliesResponse, handlePostLikingUsersResponse } = require('../responses');
const logger = require('../logger');
const tweetsRequestedCounter = require('../metrics');


router.get("/:id", async (req, res) => {
  await handlePostResponse(req, res);
  tweetsRequestedCounter.inc();
});

// Middleware to log request info
router.use((req, res, next) => {
  logger.info({ message: `[ENDPOINT] ${req.method} ${req.path}`, ip:req.ip, params: req.params, query: req.query });

  next();
});

// Middleware to log response time
router.use((req, res, next) => {
  const startHrTime = process.hrtime();

  res.on('finish', () => { // 'finish' event is emitted when response finished sending
    const elapsedHrTime = process.hrtime(startHrTime);
    const elapsedTimeInMs = elapsedHrTime[0] * 1000 + elapsedHrTime[1] / 1e6;

    logger.info({ message: `[RESPONSE] ${req.method} ${req.path}`, status_code: res.statusCode, time_elapsed: elapsedTimeInMs.toFixed(3) + ` ms` });
  });

  next();
});

/**
 * @swagger
 * /v1/posts/users/{uuid}:
 *  get:
 *      summary: Returns a list of Posts created by the user, specified by the requested UID.
 *      tags: [Posts]
 *      parameters:
 *          - in: path
 *            name: uuid
 *            schema:
 *              type: string
 *            required: true
 *            description: The UID of the user in mixit platform
 *          - in: query
 *            name: next_token
 *            schema:
 *              type: string
 *            description: The fields can contain the next_token if to get the next "set" of posts.
 *      responses:
 *          200:
 *              description: List of Posts Object
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/Post'                            
 *          400:
 *              description: Bad request
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/ErrorInvalid'
 *          404:
 *              description: User not found
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/ErrorNotFound'       
 */
router.get("/users/:uuid", async (req, res) => {
  await handleUserPostResponse(req, res);
});

/**
 * @swagger
 * /v1/posts/{uuid}/timeline:
 *  get:
 *      summary: Returns the home timeline for a user specified by the requested UUID.
 *      tags: [Posts]
 *      parameters:
 *          - in: path
 *            name: uuid
 *            schema:
 *              type: string
 *            required: true
 *            description: The UID of the user in mixit platform
 *          - in: query
 *            name: next_token
 *            schema:
 *              type: string
 *            description: The fields can contain the next_token if to get the next "set" of posts.
 *      responses:
 *          200:
 *              description: List of Posts Object
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/Post'                            
 *          400:
 *              description: Bad request
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/ErrorInvalid'
 *          404:
 *              description: User not found
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/ErrorNotFound'       
 */
router.get("/:uuid/timeline", async (req, res) => {
  await handleTimelineResponse(req, res);
});

/**
 * @swagger
 * /v1/posts/{id}:
 *  get:
 *      summary: Returns detailed information about a post using it's ID.
 *      tags: [Posts]
 *      parameters:
 *          - in: path
 *            name: id
 *            schema:
 *              type: string
 *            required: true
 *            description: The ID of the post
 *      responses:
 *          200:
 *              description: List of Posts Object
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/Post'                            
 *          400:
 *              description: Bad request
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/ErrorInvalid'
 *          404:
 *              description: Post not found
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/ErrorNotFound'
 */
router.get("/:id", async (req, res) => {
  await handlePostResponse(req, res);
  tweetsRequestedCounter.inc();
});

/**
 * @swagger
 * /v1/posts/{id}/replies:
 *  get:
 *      summary: Returns a list of replie tweets, specified by the requested UID. This will only work for recent tweets.
 *      tags: [Posts]
 *      parameters:
 *          - in: path
 *            name: id
 *            schema:
 *              type: string
 *            required: true
 *            description: The ID of the post
 *          - in: query
 *            name: next_token
 *            schema:
 *              type: string
 *            description: The fields can contain the next_token if to get the next "set" of posts.
 *      responses:
 *          200:
 *              description: List of Posts Object
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/OriginalPost'                            
 *          400:
 *              description: Bad request
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/ErrorInvalid'
 *          404:
 *              description: Replies not found
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/ErrorNotFound'
 */
router.get("/:id/replies", async (req, res) => {
  await handlePostRepliesResponse(req, res);
  tweetsRequestedCounter.inc();
});


/**
 * @swagger
 * /v1/posts/{id}/liking_users:
 *  get:
 *      summary: Returns a list of users that have liked the Post specified by the requested UID.
 *      tags: [Posts]
 *      parameters:
 *          - in: path
 *            name: id
 *            schema:
 *              type: string
 *            required: true
 *            description: The ID of the post
 *          - in: query
 *            name: next_token
 *            schema:
 *              type: string
 *            description: The fields can contain the next_token if to get the next "set" of users.
 *      responses:
 *          200:
 *              description: List of FollowUser Object
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/TwitterUser'                            
 *          400:
 *              description: Bad request
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/ErrorInvalid'
 *          404:
 *              description: Replies not found
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/ErrorNotFound'
 */
router.get("/:id/liking_users", async (req, res) => {
  await handlePostLikingUsersResponse(req, res);
  tweetsRequestedCounter.inc();
});

module.exports = router;
