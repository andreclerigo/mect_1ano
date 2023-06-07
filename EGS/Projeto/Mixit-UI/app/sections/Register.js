import React, { useState } from 'react';
import { SafeAreaView } from 'react-native';
import { Alert, StyleSheet, TouchableOpacity, Text, TextInput, View, Image, StatusBar } from 'react-native';
import CryptoJS from "crypto-js";

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


function Register({navigation}) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');
    const [twitterId, setTwitterId] = useState(''); 

    const handleRegister = async () => {
        if (!username.trim() || !password.trim() || !email.trim() || !twitterId.trim()) {
            Alert.alert(
                "Registration Error",
                "Please fill in all fields.",
                [
                    { text: "OK", onPress: () => console.log("OK Pressed") }
                ]
            );
            return;
        }

        const uuid = hashUsername(username);

        const socialEndpoint = `http://social-api-mixit.deti/v1/users/${uuid}?name=AmaralAndreViegasPedro&username=${username}&location=Portugal&twitter_id=${twitterId}`;

        const socialResponse = await fetch(socialEndpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!socialResponse.ok){
            Alert.alert(
                "Registration Error",
                "An error occurred while trying to register your account in Social API. Please try again.",
                [
                    { text: "OK", onPress: () => console.log("OK Pressed") }
                ]
            );
            return;
        }

        // Continue with registration if social-api-mixit.deti request was successful
        const endpoint = "http://mixit-egs.duckdns.org/registerSubmit";
        const body = JSON.stringify({
            username: username,
            email: email,
            password: password
        });

        fetch(endpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: body,
        })
        .then(async (response) => {
            let json;
        
            // check if the response is JSON
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.indexOf('application/json') !== -1) {
                json = await response.json();  // Get JSON from the response
            } else {
                // If it's not JSON, just get the text from the response
                const text = await response.text();
                json = { message: text };
            }
        
            console.log(json);
        
            if (response.ok) {
                return json;
            } else {
                // Here, we throw the JSON object instead of the Error, so we can inspect it in the catch block
                throw json;
            }
        })
        .then((json) => {
            Alert.alert(
                "Registration Successful",
                "Your account has been created successfully. You will be redirected to the login page.",
                [
                    { text: "OK", onPress: () => navigation.navigate('Login') }
                ]
            );
        })
        .catch((json) => {
            // The error object should be the JSON we threw in the then block
            console.error('Error:', json);
            if (json.message === 'User already exists') {
                Alert.alert(
                    "Registration Error",
                    "The user with this email already exists. Please try logging in instead.",
                    [
                        { text: "OK", onPress: () => console.log("OK Pressed") }
                    ]
                );
            } else if (json.message === 'Register failed') {
                Alert.alert(
                    "Registration Error",
                    "Registration failed. Please check your input data.",
                    [
                        { text: "OK", onPress: () => console.log("OK Pressed") }
                    ]
                );
            } else {
                Alert.alert(
                    "Registration Error",
                    "An error occurred while trying to register your account. Please try again.",
                    [
                        { text: "OK", onPress: () => console.log("OK Pressed") }
                    ]
                );
            }
        }); 
        
    };

    return (
        <View style={styles.containerGeneric}>
            <View style={styles.containerTopBar}>
                {/* Search */}
                <TouchableOpacity>
                    <Image source={require('../assets/search.png')} style={styles.search}/>
                </TouchableOpacity>
                {/* Logo */}
                <Image source={require('../assets/mixit_square.png')} style={styles.logo}/>
                {/* Notifications */}
                <TouchableOpacity>
                    <Image source={require('../assets/notifications.png')} style={styles.notifications}/>
                </TouchableOpacity>
            </View>

            <View style={styles.containerRegisterArea}>
                {/* Logo image */}
            <Image source={require('../assets/mixit_x.png')} style={styles.smallLogo}/>
            <Text style={styles.registerText}>Username</Text>
            <TextInput style={styles.inputUser} placeholder="Username" onChangeText={text => setUsername(text)} value={username}/>
            
            <Text style={styles.registerText}>Email</Text>
            <TextInput style={styles.inputUser} placeholder="Email" onChangeText={text => setEmail(text)} value={email}/>
            
            <Text style={styles.passwordText}>Password</Text>
            <TextInput style={styles.inputPass} placeholder="Password" secureTextEntry={true} onChangeText={text => setPassword(text)} value={password}/>
            
            <Text style={styles.passwordText}>Repeat Password</Text>
            <TextInput style={styles.inputPass} placeholder="Repeat Password" secureTextEntry={true}/>
            
            <Text style={styles.registerText}>Twitter ID</Text>
            <TextInput style={styles.inputUser} placeholder="Twitter ID" onChangeText={text => setTwitterId(text)} value={twitterId}/>

            <TouchableOpacity style={styles.registerButton} onPress={handleRegister}>
                <Text style={styles.registerButtonText}>Register</Text>
            </TouchableOpacity>

            <TouchableOpacity onPress={() => navigation.navigate('Login')}>
                <Text style={styles.haveAccountText}>I already have an account!</Text>
            </TouchableOpacity>
            </View>
        </View>
    );
}

const styles = StyleSheet.create({
    containerGeneric: {
        backgroundColor: '#DBDBDB',
        flex: 1,
    },
    containerTopBar: {
        backgroundColor: '#1B1B1B',
        flexDirection: 'row',
        justifyContent: 'space-around',
        alignItems: 'center',
        paddingTop: StatusBar.currentHeight,
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
    containerRegisterArea: {
        backgroundColor: '#2D2D2D',
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
    },
    smallLogo: {
        width: 100,
        height: 100,
        marginBottom: 20,
    },

    registerText: {
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
        height: 30,
        borderRadius: 10,
        marginBottom: 10,
        paddingLeft: 10,
        
    },
    inputPass: {
        backgroundColor: '#FFF',
        width: 300,
        height: 30,
        borderRadius: 10,
        marginBottom: 20,
        paddingLeft: 10,
    },
    registerButton: {
        backgroundColor: '#B9383A',
        width: 300,
        height: 30,
        borderRadius: 10,
        alignItems: 'center',
        justifyContent: 'center',
        marginTop: 20,
    },
    registerButtonText: {
        color: '#dbdbdb',
        fontSize: 20,
        fontWeight: 'bold',
    },
    haveAccountText: {
        color: '#dbdbdb',
        fontSize: 16,
        marginTop: 20,
        textDecorationLine: 'underline',
    },
});

export default Register;
