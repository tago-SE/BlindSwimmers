bool connectedToDevice = false;

void blePeripheralConnectHandler(BLEDevice central)
{
  // central connected event handler
  Serial.print("Connected event, central: ");
  Serial.println(central.address());
  connectedToDevice = true;
}

void blePeripheralDisconnectHandler(BLEDevice central)
{
  // central disconnected event handler
  Serial.print("Disconnected event, central: ");
  Serial.println(central.address());
  connectedToDevice = false;
}

void switchCharacteristicWritten(BLEDevice central, BLECharacteristic characteristic)
{
  // central wrote new value to characteristic, update LED
  //Serial.print("Characteristic event, written: ");

  /*if (switchCharacteristic.value()) {
    Serial.println("LED on");
    digitalWrite(ledPin, HIGH);
  } else {
    Serial.println("LED off");
    digitalWrite(ledPin, LOW);
  }
  */
}

const int writeByteArraySize = 16;
char writeByteArray[writeByteArraySize];
/**
 * Test function to send data when connected to a device (random number)
 */
void sendData()
{
  if(connectedToDevice)
  {
    writeByteArray[0] = 'h';
    writeByteArray[1] = 'i';
    writeByteArray[2] = ' ';
    writeByteArray[3] = 'f';
    writeByteArray[4] = 'r';
    writeByteArray[5] = 'o';
    writeByteArray[6] = 'm';
    writeByteArray[7] = ' ';
    writeByteArray[8] = 'a';
    writeByteArray[9] = 'r';
    writeByteArray[10] = 'd';
    writeByteArray[11] = 'u';
    writeByteArray[12] = 'i';
    writeByteArray[13] = 'n';
    writeByteArray[14] = 'o';
    writeByteArray[14] = '\0';
    //switchCharacteristic.setValue("hi from arduino", writeByteArraySize);
    //switchCharacteristic.writeValue(writeByteArray, writeByteArraySize);
    //switchCharacteristic.writeValue(17);
  }
}
