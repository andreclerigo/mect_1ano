import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';

import Timeline from '../sections/Timeline';
import Profile from '../sections/Profile';
import {Navbar} from './Navbar';

const Tab = createBottomTabNavigator();

export default function TabNavigator() {
  return (
    <Tab.Navigator tabBar={props => <Navbar {...props} />}>
      <Tab.Screen
        name="Timeline"
        component={Timeline}
        options={{ tabBarIcon: 'format-list-bulleted',
                   headerShown: false }}
      />
      <Tab.Screen
        name="Profile"
        component={Profile}
        options={{ tabBarIcon: 'account',
                   headerShown: false }}
      />
    </Tab.Navigator>
  );
}
