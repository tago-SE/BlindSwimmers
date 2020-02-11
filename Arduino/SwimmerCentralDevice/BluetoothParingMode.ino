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

long randNumber; //just now for testing
/**
 * Test function to send data when connected to a device (random number)
 */
void sendData()
{
  if(connectedToDevice)
  {
    randNumber = random(300);
    switchCharacteristic.writeValue(randNumber);
  }
}
