import React, {useEffect} from 'react';
import { StyleSheet, Image, Text, View, TouchableOpacity} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

function WelcomeScreen({navigation}) {
    useEffect(() => {
        // Get the MixitID from AsyncStorage
        AsyncStorage.getItem('@MixitId')
            .then((id) => {
                if (id !== null) {
                    console.log("[WelcomeScreen] Mixit ID: " + id);
                    navigation.replace("Home", { screen: "Timeline" });
                }
            })
            .catch((error) => {
                console.error("AsyncStorage error: ", error);
            });
    }, [navigation]);  // Adding navigation to the dependency array

    return (
        <View style={styles.container}>
           
           <Image 
                source={require('../assets/mixit_x.png')}
                style={styles.logo}
           />

            <Text style={styles.welcomePhrase}>
                Welcome!
            </Text>

            {/* Image of mixit.png */}
            <Image
                source={require('../assets/mixit_square.png')}
                style={styles.mixitFullLogo}
            />

            {/* Founders names */}
            <Text style={styles.founders}>
                By: Pedro Rocha, André Clérigo, João Amaral and João Viegas
            </Text>

            <TouchableOpacity 
                style={styles.loginButton}
                onPress={() => navigation.navigate('Login')}
                >
                <Text style={styles.loginText}>
                    Login
                </Text>
            </TouchableOpacity>
                
            <TouchableOpacity 
                style={styles.registerButton}
                onPress={() => navigation.navigate('Register')}
                >
                <Text style={styles.registerText}>
                    Register
                </Text>
            </TouchableOpacity>

        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#2D2D2D',
        alignItems: 'center',
        justifyContent: 'flex-end',
    },
    logo: {
        width: 100,
        height: 100,
        position: 'absolute',
        top: 80,
    },
    welcomePhrase: {
        fontSize: 20,
        color: '#D9D9D9',
        fontWeight: 'bold',
        position: 'absolute',
        top: 200,
    },
    mixitFullLogo: {
        width: 300,
        height: 300,
        position: 'absolute',
        top: 250,
    },
    founders: {
        fontSize: 12,
        color: '#D9D9D9',
        fontWeight: 'bold',
        position: 'absolute',
        top: 460,
    },
    loginButton: {
        width: '100%',
        height: 70,
        backgroundColor: '#004E64',
        borderRadius: 4,
        justifyContent: 'center',
        alignItems: 'center',
    },
    
    loginText: {
        fontSize: 20,
        color: '#D9D9D9',
        fontWeight: 'bold',
    },
    
    registerButton: {
        width: '100%',
        height: 70,
        borderRadius: 4,
        backgroundColor: '#B9383A',
        justifyContent: 'center',
        alignItems: 'center',
    },
    
    registerText: {
        fontSize: 20,
        color: '#D9D9D9',
        fontWeight: 'bold',
    },
})


export default WelcomeScreen;
