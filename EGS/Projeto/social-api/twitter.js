const { TwitterApi } = require("twitter-api-v2");
const logger = require("./logger");

const fetchFromTwitter = async (key, id, query, next_token) => {
  let twitterApiResponse;

  try {
    if (key === "external_information") {
      const twitterUserResult = await fetchUserFromTwitter(id, query);
      twitterApiResponse = { twitter_name: twitterUserResult.name, twitter_username: twitterUserResult.username };
    }

    if (key === "public_metrics") {
      const twitterUserResult = await fetchUserFromTwitter(id, query);
      twitterApiResponse = { followers_count: twitterUserResult.public_metrics.followers_count, following_count: twitterUserResult.public_metrics.following_count };
    }

    if (key === "followers") {
      const twitterFollowersResult = await fetchFollowersFromTwitter(id, query, next_token);
      twitterApiResponse = twitterFollowersResult;
    }

    if (key === "following") {
      const twitterFollowingResult = await fetchFollowingFromTwitter(id, query, next_token);
      twitterApiResponse = twitterFollowingResult;
    }

    if (key === "user_tweets") {
      const twitterTweetsResult = await fetchUserTweetsFromTwitter(id, next_token);
      twitterApiResponse = twitterTweetsResult;
    }

    if (key === "timeline") {
      const twitterTimelineResult = await fetchTimelineFromTwitter(id, next_token);
      twitterApiResponse = twitterTimelineResult;
    }

    if (key == "tweet") {
      const twitterTweetResult = await fetchTweetFromTwtitter(id);
      twitterApiResponse = twitterTweetResult;
    }

    if (key == "replies") {
      const twitterTweetResult = await fetchTweetRepliesFromTwitter(id, next_token);
      twitterApiResponse = twitterTweetResult;
    }
    
    if (key == "liking_users") {
      const twitterTweetResult = await fetchTweetLikingUsersFromTwitter(id, query, next_token);
      twitterApiResponse = twitterTweetResult;
    }

    return twitterApiResponse;
 } catch (error) {
    logger.error({ message: `[TWITTER] There was an error with the returned response from the API`, error });
    console.log('Twitter API had an error with the returned response', error);
  }
};

const fetchUserFromTwitter = async (id, query) => {
  try {
    const userInfo = await readOnlyClient.v2.users(id, query, {});
    logger.info(`[TWITTER] Fetched User Data from API for user with id: ${id}`);
    return userInfo.data[0];
  } catch (error) {
    logger.error({ message: `[TWITTER] Failed to fetch User Data from API for user with id: ${id}`, error, code: error.code });
    console.log('Twitter API had an error while fetching info', error, 'Error code:', error.code);
  }
};

const fetchFollowersFromTwitter = async (id, query, next_token) => {
  try {
    if (next_token !== undefined) query = { ...query, 'pagination_token': next_token };
    
    const followerInfo = await readOnlyClient.v2.followers(id, query);
    logger.info(`[TWITTER] Fetched Followers Data from API for user with id: ${id}`);
    return { data: followerInfo.data, next_token: followerInfo.meta.next_token };
  } catch (error) {
    logger.error({ message: `[TWITTER] Failed to fetch Followers Data from API for user with id: ${id}`, error });
    console.log('Twitter API had an error while fetching followers', error);
  }
};

const fetchFollowingFromTwitter = async (id, query, next_token) => {
  try {
    if (next_token !== undefined) query = { ...query, 'pagination_token': next_token };
    const followerInfo = await readOnlyClient.v2.followers(id, query);

    logger.info(`[TWITTER] Fetched Following Data from API for user with id: ${id}`);
    return { data: followerInfo.data, next_token: followerInfo.meta.next_token };
  } catch (error) {
    logger.error({ message: `[TWITTER] Failed to fetch Following Data from API for user with id: ${id}`, error });
    console.log('Twitter API had an error while fetching following', error);
  }
};

const fetchUserTweetsFromTwitter = async (id, next_token) => {
  let queries = { 
    'expansions': ['attachments.media_keys', 'author_id', 'referenced_tweets.id'],
    'media.fields': ['url', 'preview_image_url'],
    'user.fields': ['profile_image_url', 'username'],
    'tweet.fields': ['public_metrics', 'created_at', 'conversation_id']
  };

  try {
    if (next_token !== undefined) queries = { ...queries, 'pagination_token': next_token };
    const userTweets = await readOnlyClient.v2.userTimeline(id, queries);

    // Iterate over tweets
    const parsedTweets = await Promise.all(userTweets.data.data.map(async tweet => {
      return await parseTweetsData(tweet, userTweets, false);
    }));

    userTweets.data.data = parsedTweets;

    // Remove unwanted meta information
    delete userTweets.meta.newest_id;
    delete userTweets.meta.oldest_id;
    delete userTweets.meta.result_count;
    
    let tweets = { data: userTweets.data.data};
    if ('next_token' in userTweets.meta) tweets.next_token = userTweets.meta.next_token;
    
    logger.info(`[TWITTER] Fetched User Tweets Data from API for user with id: ${id}`);
    return tweets;
  } catch (error) {
    logger.error({ message: `[TWITTER] Failed to fetch User Tweets Data from API for user with id: ${id}`, error });
    console.error('Twitter API had an error while fetching user tweets', error);
  }
};

const fetchTimelineFromTwitter = async (id, next_token) => {
  let queries = {
    'expansions': ['attachments.media_keys', 'author_id', 'referenced_tweets.id'],
    'media.fields': ['url', 'preview_image_url'],
    'user.fields': ['profile_image_url', 'username'],
    'tweet.fields': ['public_metrics', 'created_at', 'conversation_id']
  };

  try {
    if (next_token !== undefined) queries = { ...queries, 'pagination_token': next_token };
    const homeTimeline = await userClient.v2.homeTimeline(queries);

    // Iterate over tweets
    for (let i = 0; i < homeTimeline.data.data.length; i++) {
      homeTimeline.data.data[i] = await parseTweetsData(homeTimeline.data.data[i], homeTimeline, false, true);
    }

    // Remove unwanted meta information
    delete homeTimeline.meta.newest_id;
    delete homeTimeline.meta.oldest_id;
    delete homeTimeline.meta.result_count;
    
    let tweets = { data: homeTimeline.data.data};
    if ('next_token' in homeTimeline.meta) tweets.next_token = homeTimeline.meta.next_token;
    
    logger.info(`[TWITTER] Fetched Timeline Data from API`);
    return tweets;
  } catch (error) {
    logger.error({ message: `[TWITTER] Failed to fetch Timeline Data from API`, error });
    console.log('Twitter API had an error while fetching timeline', error);
  }
}

const fetchTweetFromTwtitter = async (id) => {
  try {
    let tweet = await readOnlyClient.v2.singleTweet(id, { 
      'expansions': ['attachments.media_keys', 'author_id', 'referenced_tweets.id'],
      'media.fields': ['url', 'preview_image_url'],
      'user.fields': ['profile_image_url', 'username'],
      'tweet.fields': ['public_metrics', 'created_at', 'conversation_id']
    });
    tweet = parseTweetsData(tweet.data, tweet, false);
    
    logger.info(`[TWITTER] Fetched Tweet Data from API for tweet with id: ${id}`);
    return tweet
  } catch (error) {
    logger.error({ message: `[TWITTER] Failed to fetch Tweet Data from API for tweet with id: ${id}`, error });
    console.log('Twitter API had an error while fetching tweet', error);
  }
}

const fetchOriginalTweetFromTwtitter = async (id) => {
  try {
    let tweet = await readOnlyClient.v2.singleTweet(id, { 
      'expansions': ['attachments.media_keys', 'author_id', 'referenced_tweets.id'],
      'media.fields': ['url', 'preview_image_url'],
      'user.fields': ['profile_image_url', 'username'],
      'tweet.fields': ['public_metrics', 'created_at', 'conversation_id']
    });
    tweet = parseTweetsData(tweet.data, tweet, true);

    logger.info(`[TWITTER] Fetched Original Tweet Data from API for tweet with id: ${id}`);
    return tweet
  } catch (error) {
    logger.error({ message: `[TWITTER] Failed to fetch Original Tweet Data from API for tweet with id: ${id}`, error });
    console.log('Twitter API had an error while fetching original tweet', error);
  }
}

const fetchTweetRepliesFromTwitter = async (id, next_token) => {
  let queries = { 
    'expansions': ['attachments.media_keys', 'author_id'],
    'media.fields': ['url', 'preview_image_url'],
    'user.fields': ['profile_image_url', 'username'],
    'tweet.fields': ['public_metrics', 'created_at']
  };

  try {
    if (next_token !== undefined) queries = { ...queries, 'pagination_token': next_token };
    let replies = await readOnlyClient.v2.search('conversation_id:' + id, queries);

    // no replies found
    if (Object.keys(replies.data).length < 2) return { data : [] };

    // Iterate over tweets
    for (let i = 0; i < replies.data.data.length; i++) {
      replies.data.data[i] = await parseTweetsData(replies.data.data[i], replies);
    }

    // Remove unwanted meta information
    delete replies.meta.newest_id;
    delete replies.meta.oldest_id;
    delete replies.meta.result_count;
    
    let tweets = { data: replies.data.data};
    if ('next_token' in replies.meta) tweets.next_token = replies.meta.next_token;
    
    logger.info(`[TWITTER] Fetched Replies from API for tweet with id: ${id}`);
    return tweets;
  } catch (error) {
    logger.error({ message: `[TWITTER] Failed to fetch Replies from API for tweet with id: ${id}`, error });
    console.log('Twitter API had an error while fetching info', error);
  }
}

const fetchTweetLikingUsersFromTwitter = async (id, query, next_token) => {
  try {
    if (next_token !== undefined) query = { ...query, 'pagination_token': next_token };
    const likingUsers = await readOnlyClient.v2.tweetLikedBy(id, query);

    logger.info(`[TWITTER] Fetched users that liked a tweet from API for tweet with id: ${id}`);
    return { data: likingUsers.data, next_token: likingUsers.meta.next_token };
  } catch (error) {
    logger.error({ message: `[TWITTER] Failed to fetch users that liked a tweet from API for tweet with id: ${id}`, error });
    console.log('Twitter API had an error while fetching info', error);
  }
}

const parseTweetsData = async (tweet, tweetBase, original, timeline=false) => {
  try {
    if ('attachments' in tweet) {
      tweet.attachments.media_keys.forEach(key => {
        // Get media object
        const media = tweetBase.includes.media.find(media => media.media_key === key);
        if (media.type === 'video') {
          // Add video url to tweet
          if ("preview_urls" in tweet) {
            tweet.preview_urls.push(media.preview_image_url);
          } else {
            tweet.preview_urls = [media.preview_image_url];
          }
        }

        // If media is a photo
        if (media.type === 'photo') {
          // Add photo url to tweet
          if ("photo_urls" in tweet) {
            tweet.photo_urls.push(media.url);
          } else {
            tweet.photo_urls = [media.url];
          }
        }
      });
    }

    // Add referenced tweet information if it's a quote tweet
    if ('referenced_tweets' in tweet && !original) {
      // console.log('referenced_tweets');
      const originalTweet = await fetchOriginalTweetFromTwtitter(tweet.referenced_tweets[0].id);
      // console.log('originalTweet');
      // console.log(originalTweet);

      tweet.referenced_tweet = { originalTweet };
      delete tweet.referenced_tweets;
    }
    
    if (!timeline) {
      tweet.author_info = {};
      tweet.author_info.username = tweetBase.includes.users[0].username;
      tweet.author_info.name = tweetBase.includes.users[0].name;
      tweet.author_info.profile_image = tweetBase.includes.users[0].profile_image_url;
    } else {
      // Search for author info in includes
      tweet.author_info = {};
      tweet.author_info.username = tweetBase.includes.users.find(user => user.id === tweet.author_id).username;
      tweet.author_info.name = tweetBase.includes.users.find(user => user.id === tweet.author_id).name;
      tweet.author_info.profile_image = tweetBase.includes.users.find(user => user.id === tweet.author_id).profile_image_url;
    }
  
    delete tweet.author_id;
    delete tweet.edit_history_tweet_ids;
    delete tweet.attachments;
  
    return tweet;
  } catch (error) {
    console.log('Twitter API had an error while fetching info', error);
  }
}

const twitterClient = new TwitterApi(process.env.TWITTER_BEARER_TOKEN);
const readOnlyClient = twitterClient.readOnly;
const userClient =  new TwitterApi({
  appKey: process.env.TWITTER_CONSUMER_KEY,
  appSecret: process.env.TWITTER_CONSUMER_SECRET,
  accessToken: process.env.TWITTER_ACCESS_TOKEN,
  accessSecret: process.env.TWITTER_TOKEN_SECRET,
});

module.exports = { fetchFromTwitter };
