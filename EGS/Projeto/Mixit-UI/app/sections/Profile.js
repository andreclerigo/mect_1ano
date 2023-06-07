import React, { useEffect, useState } from 'react';
import { StyleSheet, Image, Text, View, TouchableOpacity, StatusBar, ScrollView, ActivityIndicator } from 'react-native';
import Tweet from '../components/Tweet';
import AsyncStorage from '@react-native-async-storage/async-storage';
import Add from '../components/Add';

function Profile({navigation}) {
    const [tweets, setTweets] = useState([]);
    const [uuid, setUuid] = useState(null);
    const [loading, setLoading] = useState(true); // Introduce loading state
    const [user, setUser] = useState(null); // Introduce user state
    const [profileImageUrl, setProfileImageUrl] = useState(null);

    const handleLogout = async () => {
        // Clear the MixitId from AsyncStorage
        try {
            await AsyncStorage.removeItem('@MixitId');
            console.log("[Profile] Mixit ID cleared from storage");
            navigation.replace('Welcome');
        } catch (error) {
            console.error("AsyncStorage error: ", error);
        }
    };

    useEffect(() => {
        if (uuid) {
            const url = `http://social-api-mixit.deti/v1/posts/users/${uuid}`;
            
            fetch(url)
                .then(response => response.json())
                .then(data => {
                    setTweets(data.data);
                    // Extract image URL from the first tweet (if it exists) and save it in state
                    const firstTweetImageUrl = data.data[0]?.author_info.profile_image;
                    setProfileImageUrl(firstTweetImageUrl);
                    setLoading(false); // After fetching data, set loading to false
                })
                .catch(error => {
                    console.error('Error:', error);
                    setLoading(false); // Even in case of error, set loading to false
                });
        }
    }, [uuid]);
    

    useEffect(() => {
        const fetchUUID = async () => {
            const id = await AsyncStorage.getItem('@MixitId');
            console.log("[Profile] Mixit ID: " + id);
            setUuid(id);
        };

        fetchUUID();
    }, []);

    useEffect(() => {
        if (uuid) {
            const url = `http://social-api-mixit.deti/v1/posts/users/${uuid}`;
        
            fetch(url)
                .then(response => response.json())
                .then(data => {
                    setTweets(data.data);
                    setLoading(false); // After fetching data, set loading to false
                })
                .catch(error => {
                    console.error('Error:', error);
                    setLoading(false); // Even in case of error, set loading to false
                });
        }
    }, [uuid]);

    useEffect(() => {
        if (uuid) {
            const url = `http://social-api-mixit.deti/v1/users/${uuid}?user.fields=public_metrics`;
            
            // "public_metrics": {
                //   "followers_count": 6520351,
                //   "following_count": 4624
                // }
                
            fetch(url)
                .then(response => response.json())
                .then(data => {
                    console.log(data);
                    setUser(data);

                    setLoading(false); // After fetching data, set loading to false
                })
                .catch(error => {
                    console.error('Error:', error);
                    setLoading(false); // Even in case of error, set loading to false
                });
        }
    }, [uuid]);
    
    if (loading) { // loading spinner
        return (
            <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
                <ActivityIndicator size="large" color="#0000ff" />
            </View>
        );
    }

    return (
        <View style={styles.container}>
            <View style={styles.containerTopBar}>      
                {/* Logo */}
                <Image source={require('../assets/mixit_square.png')} style={styles.logo}/>
            </View>

            <View style={styles.topContainer}>  
                <View style={styles.profileInfoContainer}>
                <Image source={profileImageUrl ? { uri: profileImageUrl } : require('../assets/profile-placeholder.png')} style={styles.profilePic} />

                    <View style={styles.profileInfo}>
                        <Text style={styles.nameText}>{user?.data.name}</Text>
                        
                        <Text style={styles.handlerText}>Mixit handler: @{user?.data.username}</Text>

                        <Text style={styles.metricsText}>Followers: {user?.data.public_metrics.followers_count} | Following: {user?.data.public_metrics.following_count}</Text>

                    </View>
                </View>

                <TouchableOpacity style={styles.logoutButton} onPress={handleLogout}>
                    <Text style={styles.logoutButtonText}>Log out</Text>
                </TouchableOpacity>
            </View>

            <View style={styles.webviewContainer}>
            <ScrollView style={styles.tweets}>
                {tweets.map((tweet, index) => (
                    <React.Fragment key={index}>
                        <View style={styles.tweetContainer}>
                        <Tweet 
                            user={tweet.author_info.username}
                            name={tweet.author_info.name} 
                            text={tweet.text} 
                            imageUrl={tweet.photo_urls ? tweet.photo_urls[0] : null} 
                            avatar={tweet.author_info.profile_image} 
                            metrics={tweet.public_metrics} 
                        />
                        </View>
                        {index % 2 === 1 && (
                            <>
                                <Add  url="http://ads-api-mixit.deti/v1/ads?publisher_id=2"/>
                            </>
                        )}
                    </React.Fragment>
                ))}
            </ScrollView>
            </View>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    containerTopBar: {
        backgroundColor: '#1B1B1B',
        flexDirection: 'row',
        justifyContent: 'space-around',
        alignItems: 'center',
        paddingTop: StatusBar.currentHeight,
    },
    topContainer: {
        backgroundColor: '#1B1B1B',
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        paddingHorizontal: 10,
        height: "12%"
    },
    search: {
        width: 30,
        height: 30,
        top: 10,
        right: 10,
    },
    logo: {
        width: 100,
        height: 100,
        bottom: 10,
    },
    notifications: {
        width: 30,
        height: 30,
        top: 10,
        left: 10,
    },
    profileInfoContainer: {
        flexDirection: 'row',
        alignItems: 'center',
    },
    profilePic: {
        width: 70,
        height: 70,
        borderRadius: 35,
        marginRight: 10,
    },
    profileInfo: {
        justifyContent: 'center',
    },
    nameText: {
        color: '#dbdbdb',
        fontSize: 18,
        fontWeight: 'bold',
        marginBottom: 5,
    },
    handlerText: {
        color: '#dbdbdb',
        fontSize: 14,
        marginBottom: 5,
    },
    locationContainer: {
        flexDirection: 'row',
        alignItems: 'center',
    },
    locationIcon: {
        width: 10,
        height: 10,
        marginRight: 5,
    },
    locationText: {
        color: '#dbdbdb',
        fontSize: 14,
    },
    logoutButton: {
        backgroundColor: '#B9383A',
        borderRadius: 5,
        paddingVertical: 5,
        paddingHorizontal: 10,
    },
    logoutButtonText: {
        color: '#dbdbdb',
        fontSize: 14,
        fontWeight: 'bold',
    },
    webviewContainer: {
        flex: 1,
        backgroundColor: '#2d2d2d',
    },
    tweets: {
        flex: 1,
        marginTop: 20,
        paddingHorizontal: 10,
    },
    tweetContainer: {
        marginBottom: 10,
    },
    metricsText: {
        color: '#dbdbdb',
        fontSize: 12,
        marginBottom: 15 ,
    },
});

export default Profile;