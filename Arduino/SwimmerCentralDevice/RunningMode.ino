
//A loop value for how often the arduino will check if ner edge
int const nearEdgeLoopValueMAXRunning = 10000; //max value of loop (how often it will loop)
int nearEdgeLoopValueRunning = nearEdgeLoopValueMAXRunning; //the acctual loop

//A loop value for how often the arduino will restart rescanning
int const restartLoopValueMAXRunning = 100000; //max value of loop (how often it will loop)
int restartLoopValueRunning = restartLoopValueMAXRunning; //the acctual loop

void runningModeSetup()
{
  if(dedugging)
  {
    Serial.println("Running mode");
  }
  //start scanning for sensores
  BLE.scan(true); //first 
}

void runningModeLoop()
{
  // poll the central for events
  //BLE.poll();

  //looping for restart scanning
  if(restartLoopValueRunning == 0)
  {
    if(dedugging)
    {
      Serial.println("restart scanning loop..");
    }
    restartLoopValueRunning = restartLoopValueMAXRunning;
    restartScan();
  }
  else
  {
    restartLoopValueRunning--;
  }

  //looping for near edge
  if(nearEdgeLoopValueRunning == 0)
  {
    //Serial.println("near edge loop..");
    nearEdgeLoopValueRunning = nearEdgeLoopValueMAXRunning;
    
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
    nearEdgeLoopValueRunning--;
  }
}

/**
 * Function that handles a matching sensor (used for scanning in training and running mode)
 */
void handlePeripheralDeviceRunning(BLEDevice peripheral)
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
  int tmp = getAverageRSSI(peripheral.rssi(), peripheral.localName());

  if(dedugging)
  {
    Serial.print("Average RSSI here is: ");
    Serial.println(tmp);
    Serial.println();
  }
  
  //double distance = getDistance(peripheral.rssi(), 9);
  //print distance
  //Serial.print("Distance is: ");
  //Serial.println(distance);
  //getTXPower(peripheral.rssi());
  
  
  restartLoopValueRunning = 0;
  restartScan();
}
