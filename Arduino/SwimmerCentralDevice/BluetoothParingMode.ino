bool connectedToDevice = false;

const int advertisingLedLoopMAX = 100000;
int advertisingLedLoop = 0;
bool advertisingLedBool = false;

/**
 * Device connects
 */
void blePeripheralConnectHandler(BLEDevice central)
{
  // central connected event handler
  if(dedugging)
  {
    Serial.print("Connected event, central: ");
    Serial.println(central.address());
  }
  
  connectedToDevice = true;
}

/**
 * Device disconnect
 */
void blePeripheralDisconnectHandler(BLEDevice central)
{
  // central disconnected event handler
  if(dedugging)
  {
    Serial.print("Disconnected event, central: ");
    Serial.println(central.address());
  }
  
  connectedToDevice = false;
}


int const receivedBytesArraySize = 20;
char receivedBytesArray [receivedBytesArraySize];

/**
 * handles all incoming bytes (strings) from mobil app
 */
void switchCharacteristicWritten(BLEDevice central, BLECharacteristic characteristic)
{
  // central wrote new value to characteristic, update LED
  if(dedugging)
  {
    Serial.print("Characteristic event, written.length: ");
    Serial.println(switchCharacteristic.valueLength());
  }
  
  switchCharacteristic.readValue(receivedBytesArray, receivedBytesArraySize);
  String receivedStrng = String(receivedBytesArray);

  if(dedugging)
  {
    Serial.print("Received from mobile: ");
    Serial.println(receivedStrng);
  }

  //What to do with the sent message
  handleAppSentMessage(receivedStrng);
  clearReceivedBytesArray();
  
  //switchCharacteristic.setValue(receivedBytesArray);
}

/**
 * STILL IN PROGRESS
 */
void switchCharacteristicNotify(BLEDevice central, BLECharacteristic characteristic)
{
  if(dedugging)
  {
    Serial.print("Characteristic event, notify");
  }
  
}

void parinModeLoop()
{
  if(connectedToDevice) //arduino connected to a mobil, led on
  {
    
    //switchCharacteristic.setValue("hi from arduino");
    digitalWrite(ledPin, HIGH);
    //switchCharacteristic.writeValue(writeByteArray, writeByteArraySize);
    //switchCharacteristic.writeValue(17);
    
  }
  else //if not connected blink led to indicat its not connected
  {
    if(advertisingLedLoop == 0)
    {
      advertisingLedLoop = advertisingLedLoopMAX;
      toggleLed();
    }
    else
    {
      advertisingLedLoop--;
    }
  }
}

void toggleLed()
{
  if(advertisingLedBool)
  {
    advertisingLedBool = false;
    digitalWrite(ledPin, LOW);
  }
  else
  {
    advertisingLedBool = true;
    digitalWrite(ledPin, HIGH);
  }
}

/**
 * handles ever message sent by the app
 */
void handleAppSentMessage(String str)
{
  //mode change
  if(str.startsWith("mode"))
  {
    str = str.substring(4);

    if(str.startsWith("_0"))
    {
      if(dedugging)
      {
        Serial.print("mode_0 received, changing mode to paring mode");
      }
      digitalWrite(ledPin, LOW);
      loopingMode = 2;
    }
    else if(str.startsWith("_1"))
    {
      if(dedugging)
      {
        Serial.print("mode_1 received, changing mode to training mode (paring done)");
      }
      digitalWrite(ledPin, LOW);
      loopingMode = 2;
    }
    else if(str.startsWith("_2"))
    {
      if(dedugging)
      {
        Serial.print("mode_2 received, changing mode to running mode (paring done)");
      }
      digitalWrite(ledPin, LOW);
      loopingMode = 2;
    }
  }
  //set sensor name
  else if(str.startsWith("SN"))
  {
    str = str.substring(2);
    if(str.startsWith("_1"))//sensor name 1
    {
      str = str.substring(2);
      sensorArray[0] = str;
      switchCharacteristic.setValue("");
    }
    else if(str.startsWith("_2")) //sensor name 2
    {
      str = str.substring(2);
      sensorArray[1] = str;
      switchCharacteristic.setValue("");
    }

    if(dedugging)
    {
      //prints number of sensors and there name
      Serial.print("  Number of edge sensors: ");
      Serial.println(sensorArraySize);
      for(int i = 0; i < sensorArraySize; i++)
      {
        Serial.print("  Sensor [");
        Serial.print((i + 1));
        Serial.print("]: ");
        Serial.println(sensorArray[i]);
      }
    }
  }
  else
  {
    
  }
}

void clearReceivedBytesArray()
{
  for(int i = 0; i < receivedBytesArraySize; i++)
  {
    receivedBytesArray[i] = '\0';
  }
}
