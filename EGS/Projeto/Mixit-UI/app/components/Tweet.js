import React from 'react';
import { View, Text, TouchableOpacity, Image } from 'react-native';
import  Icon  from 'react-native-vector-icons/MaterialIcons';
import { ActivityIndicator } from 'react-native';

function Tweet({ user, text, imageUrl, metrics = {}, avatar, name, originalTweet }) {
  const displayUser = originalTweet ? originalTweet.author_info : {username: user, name: name, avatar: avatar};
  
  return (
    <View style={styles.tweet}>
      <View style={styles.userInfo}>
        <Image source={{uri: displayUser.avatar}} style={styles.avatar}/>
        <View>
          <Text style={styles.username}>{displayUser.name}</Text>
          <Text style={styles.username}>@{displayUser.username}</Text>
        </View>
      </View>

      {/* indication of retweet */}
      {originalTweet && (
        <Text style={styles.retweet}>Retweeted by @{user}</Text>
      )}

      <View style={styles.tweetContent}>
        <Text style={styles.text}>{text}</Text>
        {imageUrl && <Image source={{ uri: imageUrl }} style={styles.image}/>}
      </View>

      <View style={styles.actions}>
        <View style={styles.actionButton} >
          <Icon name="favorite" size={20} color="#E5E9F0"/>
          <Text style={styles.actionButtonText}>{metrics?.like_count || 0}</Text>
        </View>

        <View style={styles.actionButton}>
          <Icon name="chat" size={20} color="#E5E9F0"/>
          <Text style={styles.actionButtonText}>{metrics?.reply_count || 0}</Text>
        </View>

        <View style={styles.actionButton}>
          <Icon name="autorenew" size={20} color="#E5E9F0"/>
          <Text style={styles.actionButtonText}>{metrics?.retweet_count || 0}</Text>
        </View>
      </View>
    </View>
  );
}
const styles = {
  tweet: {
    backgroundColor: '#2d2d2d',
    borderRadius: 10,
    padding: 10,
  },
  userInfo: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 10,
  },
  avatar: {
    width: 40,
    height: 40,
    borderRadius: 20, // to make it circular
    marginRight: 10,
  },
  
  username: {
    fontWeight: 'bold',
    fontSize: 16,
    color: '#fff',
  },
  tweetContent: {
    marginBottom: 10,
  },
  text: {
    fontSize: 16,
    color: '#fff',
  },
  image: {
    width: '100%',
    height: 200,
    borderRadius: 10,
    marginTop: 10,
    resizeMode: 'cover',
  },
  retweet: {
    fontSize: 14,
    color: '#7f8c8d',
    marginBottom: 5,
  },
  actions: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    backgroundColor: '#2d2d2d',
  },
  actionButton: {
    flex: 1,
    paddingVertical: 10,
    alignItems: 'center',
    borderRadius: 10,
    backgroundColor: '#2d2d2d',
    marginHorizontal: 5,
  },
  actionButtonText: {
    fontWeight: 'bold',
    fontSize: 16,
    color: '#fff',
  },
  share: {
    width: 24,
    height: 24,
  },
};

export default Tweet;
