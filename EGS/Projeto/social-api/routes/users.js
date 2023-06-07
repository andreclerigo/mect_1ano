const express = require('express');
const router = express.Router();
const { handleUserResponse, handleCreateUserRequest, handleUpdateUserRequest, handleDeleteUserRequest, userFollowHandler } = require('../responses');
const logger = require('../logger');


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
 * /v1/users/{uuid}:
 *  get:
 *      summary: Returns a variety of information about a single user specified by the requested UUID.
 *      tags: [Users]
 *      parameters:
 *          - in: path
 *            name: uuid
 *            schema:
 *              type: string
 *            required: true
 *            description: The UID of the user in mixit platform
 *          - in: query
 *            name: user.fields
 *            schema:
 *              type: string
 *            description: The fields can be location, created_at, public_metrics, and must be separated by commas. Make sure to not include a space between commas and fields.
 *      responses:
 *          200:
 *              description: A User Object
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/User'                            
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
router.get("/:uuid", async (req, res) => {
  await handleUserResponse(req, res);
});

/**
 * @swagger
 * /v1/users/{uuid}:
 *  post:
 *      summary: Adds a new user in the platform based on the UUID provided.
 *      tags: [Users]
 *      parameters:
 *          - in: path
 *            name: uuid
 *            schema:
 *              type: string
 *            required: true
 *            description: The UID of the user in mixit platform.
 *          - in: query
 *            name: name
 *            schema:
 *              type: string
 *            description: The name displayed in the user's profile in mixit platform.
 *            required: true
 *          - in: query
 *            name: username
 *            schema:
 *              type: string
 *            description: The username/handler of the user in mixit platform.
 *            required: true
 *          - in: query
 *            name: location
 *            schema:
 *              type: string
 *            description: The location selected by the user in mixit platform.
 *            required: true
 *          - in: query
 *            name: twitter_id
 *            schema:
 *              type: string
 *            description: The twitter id of the user associated with the mixit profile.
 *            required: true
 *      responses:
 *          200:
 *              description: A User Object
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/User'                            
 *          400:
 *              description: Bad request
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/ErrorInvalid'
 *          404:
 *              description: Twitter user not found
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/ErrorNotFound'
 *          409:
 *              description: User already registered in mixit
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/ErrorDuplicateResource'
 *                   
 */
router.post("/:uuid", async (req, res) => {
  await handleCreateUserRequest(req, res);
});

/**
 * @swagger
 * /v1/users/{uuid}:
 *  put:
 *      summary: Updates the information of the user added to the platform.
 *      tags: [Users]
 *      parameters:
 *          - in: path
 *            name: uuid
 *            schema:
 *              type: string
 *            required: true
 *            description: The UID of the user in mixit platform.
 *          - in: query
 *            name: name
 *            schema:
 *              type: string
 *            description: The name displayed in the user's profile in mixit platform.
 *          - in: query
 *            name: username
 *            schema:
 *              type: string
 *            description: The username/handler of the user in mixit platform.
 *          - in: query
 *            name: location
 *            schema:
 *              type: string
 *            description: The location selected by the user in mixit platform.
 *      responses:
 *          200:
 *              description: A User Object
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/User'                            
 *          400:
 *              description: Bad request
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/ErrorInvalid'
 *          404:
 *              description: Twitter user not found
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/ErrorNotFound'
 *          409:
 *              description: User already registered in mixit
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/ErrorDuplicateResource'
 *                   
 */
router.put("/:uuid", async (req, res) => {
  await handleUpdateUserRequest(req, res);
});

/**
 * @swagger
 * /v1/users/{uuid}:
 *  delete:
 *      summary: Removes an user from the platform based on the UUID provided.
 *      tags: [Users]
 *      parameters:
 *          - in: path
 *            name: uuid
 *            schema:
 *              type: string
 *            required: true
 *            description: The UID of the user in mixit platform
 *      responses:
 *          200:
 *              description: A User Object
 *              content:
 *                  application/json:
 *                      schema:
 *                        type: object
 *                        properties:
 *                          message:
 *                            type: string
 *                            description: A message indicating the success of the operation.
 *                            example: Succes operation
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
router.delete("/:uuid", async (req, res) => {
  await handleDeleteUserRequest(req, res);
});

/**
 * @swagger
 * /v1/users/by/username/{username}:
 *  get:
 *      summary: Returns a variety of information about a single user specified by the requested username.
 *      tags: [Users]
 *      parameters:
 *          - in: path
 *            name: username
 *            schema:
 *              type: string
 *            required: true
 *            description: The username of the user in mixit platform
 *          - in: query
 *            name: user.fields
 *            schema:
 *              type: string
 *            description: The fields can be location, created_at, public_metrics, and must be separated by commas. Make sure to not include a space between commas and fields.
 *            
 *      responses:
 *          200:
 *              description: A User Object
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/User'
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
router.get("/by/username/:username", async (req, res) => {
  await handleUserResponse(req, res);
});

/**
 * @swagger
 * /v1/users/{uuid}/followers:
 *  get:
 *      summary: Returns a list of users who are followers of the specified UUID (Twitter ID).
 *      tags: [Users]
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
 *            description: A value that encodes the next 'page' of results that can be requested.
 *      
 *      responses:
 *          200:
 *              description: A list of users who are followers.
 *              content:
 *                  application/json:
 *                      schema:
 *                        type: array
 *                        items:
 *                          $ref: '#/components/schemas/TwitterUser'
 *          400:
 *              description: Bad request
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/ErrorInvalid'
 * 
 *          404:
 *              description: User not found
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/ErrorNotFound'
*/
router.get("/:uuid/followers", async (req, res) => {
  await userFollowHandler(req, res, "followers");
});

/**
 * @swagger
 * /v1/users/{uuid}/following:
 *  get:
 *      summary: Returns a list of users that the specified UUID (Twitter ID) follows.
 *      tags: [Users]
 *      parameters:
 *          - in: path
 *            name: uuid
 *            schema:
 *              type: string
 *            required: true
 *            description: The UUID of the user in mixit platform
 *          - in: query
 *            name: next_token
 *            schema:
 *              type: string
 *            description: A value that encodes the next 'page' of results that can be requested.
 *      
 *      responses:
 *          200:
 *              description: A list of users who are followers.
 *              content:
 *                  application/json:
 *                      schema:
 *                        type: array
 *                        items:
 *                          $ref: '#/components/schemas/TwitterUser'
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
router.get("/:uuid/following", async (req, res) => {
  await userFollowHandler(req, res, "following");
});

module.exports = router;
