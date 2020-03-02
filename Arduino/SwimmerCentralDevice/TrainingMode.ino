//A loop value for how often the arduino will check if ner edge
int const nearEdgeLoopValueMAXTraining = 10000; //max value of loop (how often it will loop)
int nearEdgeLoopValueTraining = nearEdgeLoopValueMAXTraining; //the acctual loop

//A loop value for how often the arduino will restart rescanning
int const restartLoopValueMAXTraining = 100000; //max value of loop (how often it will loop)
int restartLoopValueTraining = restartLoopValueMAXTraining; //the acctual loop

void trainingModeSetup()
{
  if(dedugging)
  {
    Serial.println("Training mode");
  }
  initSD();
}

void trainingModeLoop()
{
  // poll the central for events
  //BLE.poll();
  digitalWrite(ledPin, LOW);

  //looping for restart scanning
  if(restartLoopValueTraining == 0)
  {
    if(dedugging)
    {
      Serial.println("restart scanning loop..");
    }
    restartLoopValueTraining = restartLoopValueMAXTraining;
    restartScan();
  }
  else
  {
    restartLoopValueTraining--;
  }

  //looping for near edge
  if(nearEdgeLoopValueTraining == 0)
  {
    //Serial.println("near edge loop..");
    nearEdgeLoopValueTraining = nearEdgeLoopValueMAXTraining;
    
    //check if time to turn-around
    if(isNearEdge())
    {
      digitalWrite(ledPin, HIGH);
    }
    else
    {
      digitalWrite(ledPin, LOW);
    }
  }
  else
  {
    nearEdgeLoopValueTraining--;
  }
}

void handlePeripheralDeviceTraining(BLEDevice peripheral)
{
  if(!saveDataPauseResume)
  {
    
  }
  else
  {
    if(dedugging)
    {
      Serial.println("-----------------------");
      Serial.print("Peripheral device \"");
      Serial.print(peripheral.localName());
      Serial.println("\" discovered");
  
      // print address
      Serial.print("Address: ");
      Serial.println(peripheral.address());
  
      // print the local name, if present
      if (peripheral.hasLocalName()) {
        Serial.print("Local Name: ");
        Serial.println(peripheral.localName());
      }
    
      // print the advertised service UUIDs, if present
      if (peripheral.hasAdvertisedServiceUuid()) {
        Serial.print("Service UUIDs: ");
        for (int i = 0; i < peripheral.advertisedServiceUuidCount(); i++) {
          Serial.print(peripheral.advertisedServiceUuid(i));
          Serial.print(" ");
        }
        Serial.println();
      }
    
      // print the RSSI
      Serial.print("RSSI: ");
      Serial.println(peripheral.rssi());
    }
    
    //fingerprint(peripheral.rssi());
  
    String sensorId = peripheral.localName();
    int rssiValue = peripheral.rssi();
    int averageRSSIValue = getAverageRSSI(peripheral.rssi(), peripheral.localName());
    long timeValue =/* timeStamp + */(millis() - startMillis);
  
    
    String strToSD = sensorId + "\t" + rssiValue + "\t" + averageRSSIValue + "\t" + timeValue + "\t" + turnButtonIsPressed + "\t" + timeStamp;
    writeToFileWrapper(strToSD);
    turnButtonIsPressed = false;
  
    //if turn button is pressed on app, set to false
    /*if(turnButtonPressed)
    {
      turnButtonPressed = false;
    }*/
  
    if(dedugging)
    {
      Serial.print("Average RSSI here is: ");
      Serial.println(averageRSSIValue);
      Serial.println();
    }
  }
  
  
  restartLoopValueTraining = 0;
  restartScan();
}
