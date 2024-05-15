import React from 'react';
import { View, StyleSheet } from 'react-native';
import SupervisorListScreen from './SupervisorListScreen';

const App: React.FC = () => {
  return (
    <View style={styles.container}>
      <SupervisorListScreen />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#fff',
    padding: 20,
  },
});

export default App;
