
const int advertisingLedLoopMAX = 1000000;
int advertisingLedLoop = 0;
bool advertisingLedBool = false;

void connectingModeSetup()
{
  if(dedugging)
  {
    Serial.println("Paring mode");
    Serial.println("Start advertising, waiting for connections...");
  }
  // start advertising
  BLE.advertise();
}

void connectingModeLoop()
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
