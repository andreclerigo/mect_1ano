
import React, { useState } from "react";
import { Alert, StyleSheet, TouchableOpacity, Text, TextInput, View, Image, StatusBar, Button, Modal } from "react-native";
import Icon from "react-native-vector-icons/FontAwesome";
import { WebView } from "react-native-webview";
import { Linking } from "react-native";
import AsyncStorage from '@react-native-async-storage/async-storage';
import { Dimensions } from 'react-native';
import CryptoJS from "crypto-js";

function Login({ navigation }) {
  const [showWebView, setShowWebView] = useState(false);
  // hold url to authenticate
  const [authUrl, setAuthUrl] = useState(""); 
  const [token, setToken] = useState(null);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [twitterId, setTwitterId] = useState("");
  const [isTwitterIdModalVisible, setIsTwitterIdModalVisible] = useState(false);
  const [twitterFormVisible, setTwitterFormVisible] = useState(false);
  const [fullScreen, setFullScreen] = useState(false);

  function hashUsername(username) {
    const hash = CryptoJS.MD5(username).toString();
    let numbers = '';
    for (let i = 0; i < hash.length; i++) {
        if (!isNaN(parseInt(hash[i]))) {
            numbers += hash[i];
        }
        if (numbers.length === 5) {
            break;
        }
    }
    return numbers;
  }

  const handleSubmitButtonPress = () => {
    setIsTwitterIdModalVisible(false);
    handleTwitterSubmit();
  };

  const handleTwitterSubmit = async () => {
    console.log("Submitting Twitter ID");
    console.log("Twitter ID: " + twitterId);
    console.log("Token: " + token);
    // hash
    const hashAsBefore = hashUsername(token);
    console.log("Hash: " + hashAsBefore);
    const response = await fetch(`http://social-api-mixit.deti/v1/users/${hashAsBefore}?name=AmaralAndreViegasPedro&username=${token}&location=Portugal&twitter_id=${twitterId}`, {
      method: "POST"
    });

    if (response.ok) {
        console.log("User registered successfully");
        setTwitterFormVisible(false);  // Hide the form after successful registration
        // save the Mixit ID to AsyncStorage
        AsyncStorage.setItem('@MixitId', hashAsBefore).catch((error) => {
          console.error("AsyncStorage error: ", error);
        });
        navigation.replace("Home", { screen: "Timeline" });
    } else {
        console.log("Error: " + response.status)
        console.log("Error: " + response.statusText)
        console.log("Error: " + response.body)
        console.log("Error registering user");
    }
  };

  const parseUrl = (url) => {
    const match = url.match(/^http[s]?:\/\/([^:]+):(\d+)(\/.*)?$/);
    if (match) {
      const hostName = match[1];
      const port = match[2];
      return { hostName, port };
    }
    return {};
  };

  const handleNavigationChange = async ({ url }) => {
    const { hostName, port } = parseUrl(url);

    if (hostName === "127.0.0.1" && port === "5100") {
      const tokenMatch = url.match(/token=([^&]+)/);
      const token = tokenMatch && tokenMatch[1];

      if (token) {
        setToken(token);
        if (token === null) {
            console.log("Token is null");
        }
        console.log("Token: " + token);

        // Check if the user is already registered with the social API
        const response = await fetch(`http://social-api-mixit.deti/v1/users/by/username/${token}`);
        if (response.ok) {
          // User is already registered, do the normal process
            console.log("User already registered");
        } else {
            // User not registered, register the user
            setIsTwitterIdModalVisible(true); // Open the modal to get the Twitter ID
        }
        
        setShowWebView(false);
      }
    }
  };

  const handleLogin = async () => {
    const response = await fetch("http://mixit-egs.duckdns.org/loginSubmit", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        username,
        password,
      }),
    });
  
    const data = await response.json();
    try {
      if (response.ok) {
        setToken(data.token);  // if the server sends back a token
  
        // fetch request to get the Mixit ID
        const mixitResponse = await fetch(`http://social-api-mixit.deti/v1/users/by/username/${username}`);
        const mixitData = await mixitResponse.json();
        
        if (mixitResponse.ok) {
            if (mixitData && mixitData.data && mixitData.data.uuid) {
                const mixitId = mixitData.data.uuid;  // Get Mixit ID from the response
                console.log("[LOGIN] Mixit ID: ", mixitId);
        
                // Save the MixitID to AsyncStorage
                AsyncStorage.setItem('@MixitId', mixitId).catch((error) => {
                  console.error("AsyncStorage error: ", error);
                });
        
                navigation.replace("Home", { screen: "Timeline" });
            } else {
                console.error("Error getting Mixit ID: Invalid data structure", mixitData);
            }
        } else {
            console.error("Error getting Mixit ID:", mixitData.message);
        }
        
        navigation.navigate("Home", { screen: "Timeline" });
        Alert.alert("Login successful!");
      } else if (!response.ok) {
        // The request failed - handle the error
        console.error("Error:", data.message);
        Alert.alert("Login failed!", data.message);
      }
    } catch (error) {
      console.error("Network error:", error);
      Alert.alert("Network error", "Please check your internet connection and try again.");
    }
  };
  

  const handleAuth = (url) => {
    setAuthUrl(url);
    setShowWebView(true);
    setTimeout(() => {
      setShowWebView(false);
    }, 50000000);
  };

  const handleGoogleAuth = () => handleAuth("https://mixit-egs.duckdns.org");
  const handleGithubAuth = () => handleAuth("http://mixit-egs.duckdns.org/github");

    
    return (
      <>

        <View style={styles.containerGeneric}>
             
            <View style={styles.containerTopBar}>
                {/* Logo */}
                <Image source={require('../assets/mixit_square.png')} style={styles.logo}/> 
            </View>

            <View style={styles.containerLoginArea}>
                {/* Logo image */}
                <Image source={require('../assets/mixit_x.png')} style={styles.smallLogo}/>
                
                <Text style={styles.loginText}>Username</Text>
                <TextInput style={styles.inputUser} placeholder="Username" value={username} onChangeText={setUsername}/>
                
                <Text style={[styles.passwordText, {marginTop: 20}]}>Password</Text>
                <TextInput style={styles.inputPass} placeholder="Password" secureTextEntry={true} value={password} onChangeText={setPassword} />
                
                {twitterFormVisible && (
                  <View style={styles.twitterForm}>
                    <Text>Please enter your Twitter ID:</Text>
                    <TextInput value={twitterId} onChangeText={setTwitterId} />
                    <TouchableOpacity style={styles.submitButton} onPress={handleTwitterSubmit}>
                      <Text style={styles.submitButtonText}>Submit Twitter ID</Text>
                    </TouchableOpacity>
                  </View>
                )}

                <TouchableOpacity style={styles.loginButton} onPress={handleLogin}>
                    <Text style={styles.loginButtonText}>Login</Text>
                </TouchableOpacity>

                <View style={styles.container}>
                    <TouchableOpacity style={styles.button} onPress={handleGoogleAuth}>
                        <Icon name="google" size={20} color="#FFFFFF" />
                        <Text style={styles.buttonText}>Login with Google</Text>
                    </TouchableOpacity>

                    <TouchableOpacity style={styles.button} onPress={handleGithubAuth}>
                        <Icon name="github" size={20} color="#FFFFFF" />
                        <Text style={styles.buttonText}>Login with Github</Text>
                    </TouchableOpacity>
                    
                </View>

                <TouchableOpacity onPress={() => navigation.navigate('Register')}>
                    <Text style={styles.createAccountText}>I don't have an account yet!</Text>
                </TouchableOpacity>
            </View>
            <Modal visible={isTwitterIdModalVisible} animationType="slide" onRequestClose={() => setIsTwitterIdModalVisible(false)}>
              <Text>Please enter your Twitter ID:</Text>
              <TextInput value={twitterId} onChangeText={setTwitterId} />
              <Button
                title="Submit"
                onPress={handleSubmitButtonPress}
              />
            </Modal>
        
        </View>
        {showWebView ? (
          <WebView
              style={fullScreen ? styles.fullScreenWebView : styles.normalWebView}
              source={{ uri: authUrl }}
              onNavigationStateChange={handleNavigationChange}
          />
        ) : null}
      </>
    );
}

const styles = StyleSheet.create({
    containerGeneric: {
        backgroundColor: '#DBDBDB',
        flex: 1,
        position: 'relative',
    },
    containerTopBar: {
        backgroundColor: '#1B1B1B',
        flexDirection: 'row',
        justifyContent: 'space-around',
        alignItems: 'center',
        paddingTop: StatusBar.currentHeight,
    },
    logo: {
        width: 100,
        height: 100,
        bottom: 10,
    },
    containerLoginArea: {
        backgroundColor: '#2D2D2D',
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
    },
    smallLogo: {
        width: 100,
        height: 100,
        marginBottom: 20,
        marginTop: 20,
    },

    loginText: {
        color: '#dbdbdb',
        fontSize: 20,
        fontWeight: 'bold',
        marginBottom: 10,
    },
    passwordText: {
        color: '#dbdbdb',
        fontSize: 20,
        fontWeight: 'bold',
        marginBottom: 10,
    },
    inputUser: {
        backgroundColor: '#FFF',
        width: 300,
        height: 40,
        borderRadius: 10,
        marginBottom: 10,
        paddingLeft: 10,
        
    },
    inputPass: {
        backgroundColor: '#FFF',
        width: 300,
        height: 40,
        borderRadius: 10,
        marginBottom: 80,
        paddingLeft: 10,
    },
    loginButton: {
        backgroundColor: '#B9383A',
        width: 300,
        height: 40,
        borderRadius: 10,
        alignItems: 'center',
        justifyContent: 'center',
    },
    loginButtonText: {
        color: '#dbdbdb',
        fontSize: 20,
        fontWeight: 'bold',
    },
    createAccountText: {
        color: '#dbdbdb',
        fontSize: 16,
        marginBottom: '10%',
        textDecorationLine: 'underline',
    },
    container:  {
        flex: 1,
        justifyContent: 'center',
        paddingHorizontal: 10,
    },
    button: {
        backgroundColor: '#004E64',
        padding: 10,
        marginVertical: 5,
        flexDirection: 'row',
        justifyContent: 'center',
        alignItems: 'center',
        borderRadius: 5,
      },
      buttonText: {
        color: '#FFFFFF',
        marginLeft: 10,
      },
      twitterForm: {
        backgroundColor: '#FFF',
        padding: 10,
        borderRadius: 10,
      },
      submitButton: {
        backgroundColor: '#004E64',
        padding: 10,
        borderRadius: 5,
        marginTop: 10,
      },
      submitButtonText: {
        color: '#FFFFFF',
        textAlign: 'center',
      },
      fullScreenWebView: {
        position: 'absolute', 
        top: 0, 
        left: 0, 
        bottom: 0, 
        right: 0,
    },
    
    normalWebView: {
        // Define your normal WebView style here...
        width: "80%", 
        height: "80%",
        alignSelf: "center",
    },
});

export default Login;