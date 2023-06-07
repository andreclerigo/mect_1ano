import React, { useState, useEffect } from 'react';
import { StyleSheet, View, Image, TouchableOpacity, Text } from 'react-native';
import  Icon  from 'react-native-vector-icons/MaterialIcons';

function Add({ url }) {
    const [adImage, setAdImage] = useState("");
    const [adRedirect, setAdRedirect] = useState("");

    useEffect(() => {
        fetch('http://ads-api-mixit.deti/v1/ads?publisher_id=2', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        })
        .then((response) => {
            response.json().then((data) => {
                setAdImage(data.ad_creative);
                setAdRedirect(data.ad_redirect);
            });
        })
        .catch((error) => {
            console.error(error);
        }
        );
    }, [url]); // re-run effect when `url` changes

    return (
        <View style={styles.add}>
          <TouchableOpacity onPress={() => console.log("Clicked on ad!")}>
            <View style={styles.adContent}>
              {adImage && <Image source={{ uri: adImage }} style={styles.image}/>}
            </View>
          </TouchableOpacity>
        </View>
    );
}

const styles = {
  add: {
    backgroundColor: '#2d2d2d',
    borderRadius: 10,
    padding: 10,
  },
  adContent: {
    marginBottom: 10,
  },
  image: {
    width: '100%',
    height: 200,
    borderRadius: 10,
    resizeMode: 'cover',
  },
};

export default Add;
