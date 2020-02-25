
//String ArduinoName = "Arduino Swimmer 1"; <-- does't work with BLE.setLocalName(ArduinoName); ..?

BLEService swimmerService("19B10001-E8F2-537E-4F6C-D104768A1214"); // create service

// create switch characteristic and allow remote device to read and write
BLECharacteristic switchCharacteristic("19B10001-E8F2-537E-4F6C-D104768A1214", BLERead | BLEWrite | BLENotify, 40);

bool connectedToDevice;

//char array of received data
int const receivedBytesArraySize = 30;
char receivedBytesArray [receivedBytesArraySize];

void BLEInit()
{
  // begin initialization
  if (!BLE.begin()) {
    Serial.println("starting BLE failed!");

    while (1);
  }

  if(dedugging)
  {
    Serial.println("Arduino setup now running");
  }
    

  //========== used for setting up bluetooth connection with mobil-app ============
  // set the UUID for the service this peripheral advertises
  BLE.setAdvertisedService(swimmerService);

  // add the characteristic to the service
  swimmerService.addCharacteristic(switchCharacteristic);

  // add service
  BLE.addService(swimmerService);

  // assign event handlers for connected, disconnected to peripheral
  BLE.setEventHandler(BLEConnected, blePeripheralConnectHandler);
  BLE.setEventHandler(BLEDisconnected, blePeripheralDisconnectHandler);

  // assign event handlers for characteristic
  switchCharacteristic.setEventHandler(BLEWritten, switchCharacteristicWritten);

  //egen test för notify
  switchCharacteristic.setEventHandler(BLENotify, switchCharacteristicNotify);
  
  // set an initial value for the characteristic
  switchCharacteristic.setValue("Arduino Swimmer 1 message");

  connectedToDevice = false;

  // set the local name peripheral advertises
  BLE.setLocalName("Arduino Swimmer 1");
}

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
      loopingMode = 0;
    }
    else if(str.startsWith("_1"))
    {
      if(dedugging)
      {
        Serial.print("mode_1 received, changing mode to training mode (paring done)");
      }
      digitalWrite(ledPin, LOW);
      loopingMode = 1;
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
  //set the timeStamp from app
  else if(str.startsWith("TS_"))
  {
    str = str.substring(9);
    timeStamp = str.toInt();
  }
  //button pressed on app to turn now
  else if(str.startsWith("T"))
  {
    turnButtonIsPressed = true;
  }
  else if(str.startsWith("SD_CLEAR"))
  {
    if(dedugging)
    {
      Serial.println("Clear SD Card");
    }
    clearSDCard();
  }
  else
  {
    if(dedugging)
    {
      Serial.print("Could not match sent data: ");
      Serial.println(str);
    }
  }
}

void clearReceivedBytesArray()
{
  for(int i = 0; i < receivedBytesArraySize; i++)
  {
    receivedBytesArray[i] = '\0';
  }
}

//===================== used in training and running mode =============


 /**
 * Callback function for every found scanned device (used for scanning in training and running mode)
 */
void bleCentralDiscoverHandler(BLEDevice peripheral) {
  // discovered a peripheral
  //Serial.println("Discovered a peripheral");
  //Serial.println("-----------------------");

  String addressName = peripheral.localName();
  String deviceScanningName = "Simple";

  //is scanned device a device we are looking for?
  for(int i = 0; i < sensorArraySize; i++)
  {
    if(addressName.startsWith(sensorArray[i]))
    {
      if(loopingMode == 1)
      {
        handlePeripheralDeviceTraining(peripheral);
      }
      else if(loopingMode == 2)
      {
        handlePeripheralDeviceRunning(peripheral);
      }
    }
  }
}



/**
 * Stops and starts scanneing after devices (scanning, training and running mode)
 */
void restartScan()
{
  BLE.stopScan();
  //BLE.scanForName("SimplePeripheral"); 
  BLE.scan(true); //first 
}
