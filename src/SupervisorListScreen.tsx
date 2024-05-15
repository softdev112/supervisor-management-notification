import React, { useState, useEffect } from 'react';
import { View, Text, TextInput, Button, Alert, StyleSheet, Pressable } from 'react-native';
import { Picker } from '@react-native-picker/picker';
import { NativeModules } from 'react-native';

const SupervisorListScreen: React.FC = () => {
  const [supervisors, setSupervisors] = useState<string[]>([]);
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [selectedSupervisor, setSelectedSupervisor] = useState('');
  const [showPicker, setShowPicker] = useState(false);

  useEffect(() => {
    // Fetch supervisors when component mounts
    NativeModules.SupervisorModule.getSupervisors((error: string, supervisorList: string[]) => {
      if (error) {
        Alert.alert('Error', error);
      } else {
        console.log(supervisorList);
        setSupervisors(supervisorList);
      }
    });
  }, []);

  const handleSubmit = () => {
    // Validate form data
    if (!firstName || !lastName || !selectedSupervisor) {
      Alert.alert('Error', 'Please fill in all required fields');
      return;
    }

    // Submit employee data
    NativeModules.SupervisorModule.submitEmployee(
      firstName,
      lastName,
      email,
      phoneNumber,
      selectedSupervisor,
      (error: string | null) => {
        if (error) {
          Alert.alert('Error', error);
        } else {
          Alert.alert('Success', 'Employee data submitted successfully');
          // Clear form fields
          setFirstName('');
          setLastName('');
          setEmail('');
          setPhoneNumber('');
          setSelectedSupervisor('');
        }
      }
    );
  };

  return (
    <View style={styles.container}>
      <Text>First Name (Required)</Text>
      <TextInput
        style={styles.textInput}
        placeholder="First Name"
        value={firstName}
        onChangeText={setFirstName}
      />
      <Text>Last Name (Required)</Text>
      <TextInput
        style={styles.textInput}
        placeholder="Last Name"
        value={lastName}
        onChangeText={setLastName}
      />
      <Text>Email (Optional)</Text>
      <TextInput
        style={styles.textInput}
        placeholder="Email"
        value={email}
        onChangeText={setEmail}
      />
      <Text>Phone Number (Optional)</Text>
      <TextInput
        style={styles.textInput}
        placeholder="Phone Number"
        value={phoneNumber}
        onChangeText={setPhoneNumber}
      />
      <Text>Supervisor (Required)</Text>
      <View style={styles.picker} >
        <Picker
          placeholder="Supervisor"
          selectedValue={selectedSupervisor}
          onValueChange={(itemValue: React.SetStateAction<string>) => setSelectedSupervisor(itemValue)}
        >
          {supervisors.map((supervisor, index) => (
            <Picker.Item key={index} label={supervisor} value={supervisor} />
          ))}
        </Picker>
      </View>
      <Button title="Submit" onPress={handleSubmit} />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    justifyContent: 'space-between',
    paddingHorizontal: 10,
    paddingVertical: 10,
    width: '100%',
  },
  textInput: {
    borderWidth: 1,
    borderRadius: 4,
    borderColor: '#ccc',
    paddingHorizontal: 10,
    paddingVertical: 8,
    height: 40,
    marginBottom: 20,
  },
  picker: {
    borderWidth: 1,
    borderRadius: 4,
    borderColor: '#ccc',
    height: 40,
    marginBottom: 20,
  },
  input: {
    fontSize: 16,
  },
});

export default SupervisorListScreen;
