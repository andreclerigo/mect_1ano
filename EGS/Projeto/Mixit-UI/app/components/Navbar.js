import React from 'react';
import { StyleSheet, TouchableOpacity, View } from 'react-native';
import MaterialCommunityIcons from 'react-native-vector-icons/MaterialCommunityIcons';

function Navbar({ state, descriptors, navigation }) {
  return (
    <View style={styles.navbar}>
      {state.routes.map((route, index) => {
        const { options } = descriptors[route.key];
        const label = options.tabBarIcon !== undefined
            ? options.tabBarIcon
            : options.title !== undefined
            ? options.title
            : route.name;

        const isFocused = state.index === index;

        const onPress = () => {
          const event = navigation.emit({
            type: 'tabPress',
            target: route.key,
            canPreventDefault: true,
          });

          if (!isFocused && !event.defaultPrevented) {
            navigation.navigate(route.name);
          }
        };

        const onLongPress = () => {
          navigation.emit({
            type: 'tabLongPress',
            target: route.key,
          });
        };

        return (
          <TouchableOpacity
            key={index}
            accessibilityRole="button"
            accessibilityState={isFocused ? { selected: true } : {}}
            onPress={onPress}
            onLongPress={onLongPress}
            style={isFocused ? styles.activeNavButton : styles.navButton}
          >
            <MaterialCommunityIcons
              name={label}
              size={26}
              color={isFocused ? styles.activeNavButtonText.color : styles.navButtonText.color}
            />
          </TouchableOpacity>
        );
      })}
    </View>
  );
}

const styles = StyleSheet.create({
    navbar: {
      flexDirection: 'row',
      backgroundColor: '#1B1B1B',
      justifyContent: 'space-evenly',
      alignItems: 'center',
      height: 60,
      borderTopWidth: 1,
      borderTopColor: '#333',
    },
    navButton: {
      justifyContent: 'center',
      alignItems: 'center',
      height: '100%',
    },
    navButtonText: {
      color: '#dbdbdb',
    },
    activeNavButton: {
      borderBottomWidth: 2,
      borderBottomColor: '#1DA1F2',
    },
    activeNavButtonText: {
      color: '#1DA1F2',
    },
});

export { Navbar };
