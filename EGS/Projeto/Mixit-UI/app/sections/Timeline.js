import React, { useEffect, useState } from 'react';
import { ScrollView, StyleSheet, View, TouchableOpacity, Image, ActivityIndicator } from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';
import AsyncStorage from '@react-native-async-storage/async-storage';

import Tweet from '../components/Tweet';
import Add from '../components/Add';
import logo from '../assets/mixit.png'; 

function Timeline({ navigation }) { 
  const [mixitId, setMixitId] = useState(null); 
  const [timelineData, setTimelineData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Get the MixitID from AsyncStorage
    AsyncStorage.getItem('@MixitId')
      .then((id) => {
        if (id !== null) {
          setMixitId(id);
          console.log("[TIMELINE] Mixit ID: " + id);
          fetch(`http://social-api-mixit.deti/v1/posts/${id}/timeline`)
            .then((response) => response.json())
            .then((data) => {
              setTimelineData(data.data);
              setLoading(false);
            })
            .catch((error) => console.error(error));
        }
      })
      .catch((error) => {
        console.error("AsyncStorage error: ", error);
      });
  }, []);

  if (loading) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <ActivityIndicator size="large" color="#0000ff" />
      </View>
    );
  }
  
  return (
    <View style={styles.container}>
      <View style={styles.logoContainer}>
        <Image source={logo} style={styles.logo} />
      </View>
      <TouchableOpacity onPress={() => navigation.navigate('Home', {screen:'Profile'})}>
        <Icon name="person" size={30} style={styles.profile}/>
      </TouchableOpacity>
      <ScrollView style={styles.tweets}>
        {timelineData.map((tweet, index) => (
          <React.Fragment key={tweet.id}>
          <Tweet 
            user={tweet.author_info.username}
            name={tweet.author_info.name} 
            text={tweet.text} 
            imageUrl={tweet.photo_urls ? tweet.photo_urls[0] : null} 
            metrics={tweet.public_metrics}
            avatar={tweet.author_info.profile_image}
            originalTweet={tweet.referenced_tweet ? tweet.referenced_tweet.originalTweet : null}
          />
            <View style={styles.separator}/>
            {index % 2 === 1 && (
              <>
                <Add  url="http://ads-api-mixit.deti/v1/ads?publisher_id=2"/>
                <View style={styles.separator}/>
              </>
            )}
          </React.Fragment>
        ))}
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#2d2d2d',
  },
  logoContainer: {
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: '8%',
    padding: 10,
  },
  logo: {
    width: 130,  
    height: 40,  
    resizeMode: 'contain',
  },
  tweets: {
    flex: 1,
    marginTop: 20,
    paddingHorizontal: 10,
  },
  separator: {
    borderBottomColor: 'gray',
    borderBottomWidth: 1,
  },
  profile: {
    marginLeft: '90%',
    color: '#E5E9F0',
  },
  webviewContainer: {
    height: 300, // Fixed height for the WebView container
    marginTop: 10,
  },
});

export default Timeline;
