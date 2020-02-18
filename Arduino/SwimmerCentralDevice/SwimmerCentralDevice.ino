/*
  Scan Callback

  This example scans for BLE peripherals and prints out their advertising details:
  address, local name, adverised service UUIDs. Unlike the Scan example, it uses
  the callback style APIs and disables filtering so the peripheral discovery is
  reported for every single advertisement it makes.

  The circuit:
  - Arduino MKR WiFi 1010, Arduino Uno WiFi Rev2 board, Arduino Nano 33 IoT,
    Arduino Nano 33 BLE, or Arduino Nano 33 BLE Sense board.

  This example code is in the public domain.
*/

//includes
#include <ArduinoBLE.h>
#include <string.h>

/**
 * true = when debugging (will not start arduino unitil serial monitor is active when set to true)
 * false = when not debugging (starts arduino instant when power on)
 */
bool dedugging = false;

//A loop value for how often the arduino will restart rescanning
int const restartLoopValueMAX = 100000; //max value of loop (how often it will loop)
int restartLoopValue = restartLoopValueMAX; //the acctual loop

//A loop value for how often the arduino will check if ner edge
int const nearEdgeLoopValueMAX = 10000; //max value of loop (how often it will loop)
int nearEdgeLoopValue = nearEdgeLoopValueMAX; //the acctual loop

//number of sensor looking for by the arduino (sensor near edges)
int const sensorArraySize = 2;
String sensorArray [sensorArraySize];

const int ledPin = LED_BUILTIN; // pin to use for the LED

//fingerprint global variables
int const fingerprintArraySize = 10;
int fingerprintArray [fingerprintArraySize];
int fingerprintIndex = 0;
bool fingerprintReady;

//Looping modes, used in loop function
/**
 * 0 == paring mode
 * 1 == training mode (paring done)
 * 2 == running mode (paring done)
 * 
 * Default as 0 in init function
 */
int loopingMode;

//======== global variables for Bluetooth-connection with mobile-phone ============
//String ArduinoName = "Arduino Swimmer 1"; <-- does't work with BLE.setLocalName(ArduinoName); ..?

BLEService swimmerService("19B10001-E8F2-537E-4F6C-D104768A1214"); // create service

// create switch characteristic and allow remote device to read and write
BLECharacteristic switchCharacteristic("19B10001-E8F2-537E-4F6C-D104768A1214", BLERead | BLEWrite | BLENotify, 40);

/**
 * Init
 */
void setup() {
  Serial.begin(9600);
  if(dedugging)
  {
    //wait till serial monitor is on
    while (!Serial);
  }
  

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

  // set the local name peripheral advertises
  BLE.setLocalName("Arduino Swimmer 1");

  sensorArray[0] = "SimplePeripheral";
  sensorArray[1] = "SimpleBLEBroadcaster";

  if(dedugging)
  {
    //prints number of sensors and there name
    Serial.print("  Number edge of sensors: ");
    Serial.println(sensorArraySize);
    for(int i = 0; i < sensorArraySize; i++)
    {
      Serial.print("  Sensor [");
      Serial.print((i + 1));
      Serial.print("]: ");
      Serial.println(sensorArray[i]);
    }
  }

  digitalWrite(ledPin, LOW);
  //==============================================================================

  // set the discovered event handle (used for scanning in training and running mode)
  BLE.setEventHandler(BLEDiscovered, bleCentralDiscoverHandler);

  //set starting mode (0 == paring mode)
  loopingMode = 0;

  //Init fingerprinting
  zeroFingerprintArray();
  fingerprintReady = false;

  // start scanning for peripherals with duplicates
  //BLE.scanForName("SimplePeripheral"); 
  //BLE.scan(true); //first 
  if(dedugging)
  {
    Serial.println("Arduino setup done");
  }
  
}

//========================= Forever-loop =====================================
int newMode = -1;
/**
 * Forever-loop
 */
void loop()
{
  if(loopingMode == 0)
  {
    //paring mode

    //if mode changes, do setup once when modechanges
    if(newMode != loopingMode)
    {
      if(dedugging)
      {
        Serial.println("Paring mode");
        Serial.println("Start advertising, waiting for connections...");

        // start advertising
        BLE.advertise();
      }
      
      newMode = loopingMode;
    }
    paringModeFunction();
    
  }
  else if(loopingMode == 1)
  {
    //training mode

    //if mode changes, do setup once when modechanges
    if(newMode != loopingMode)
    {
      if(dedugging)
      {
        Serial.println("Training mode");
      }
      newMode = loopingMode;
    }
    trainingModeFunction();
  }
  else if(loopingMode == 2)
  {
    //running mode

    //if mode changes, do setup once when modechanges
    if(newMode != loopingMode)
    {
      if(dedugging)
      {
        Serial.println("Running mode");
      }
      //start scanning for sensores
      BLE.scan(true); //first 
      
      newMode = loopingMode;
    }
    runningModeFunction();
  }
}

/**
 * At start up, tries to par with a mobile
 */
void paringModeFunction()
{
  BLE.poll();
  parinModeLoop();
}

/**
 * When training is selected
 */
void trainingModeFunction()
{
  
}

/**
 * When running (not training)
 */
void runningModeFunction()
{
  // poll the central for events
  BLE.poll();

  //looping for restart scanning
  if(restartLoopValue == 0)
  {
    if(dedugging)
    {
      Serial.println("restart scanning loop..");
    }
    restartLoopValue = restartLoopValueMAX;
    restartScan();
  }
  else
  {
    restartLoopValue--;
  }

  //looping for near edge
  if(nearEdgeLoopValue == 0)
  {
    //Serial.println("near edge loop..");
    nearEdgeLoopValue = nearEdgeLoopValueMAX;
    
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
    nearEdgeLoopValue--;
  }
}

//============================ bluetooth connection =================================
/**
 * Testing 35:69:1f:44:a3:74
 * Sencor F0:F8:F2:DA:31:FD
 */

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
      handlePeripheralDevice(peripheral);
    }
  }
}

/**
 * Function that handles a matching sensor (used for scanning in training and running mode)
 */
void handlePeripheralDevice(BLEDevice peripheral)
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
  
  
  

  fingerprint(peripheral.rssi());

  if(dedugging)
  {
    Serial.print("Average RSSI here is: ");
    Serial.println(getAverageFingerprint());
    Serial.println();
  }
  

  

  //double distance = getDistance(peripheral.rssi(), 9);
  //print distance
  //Serial.print("Distance is: ");
  //Serial.println(distance);
  //getTXPower(peripheral.rssi());
  
  
  restartLoopValue = 0;
  restartScan();
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

//============================== device near edge =====================================
/**
 * Check if device is near edge
 */
bool isNearEdge()
{
  if(!fingerprintReady)
  {
    //average not filled yet and not ready yet
    return false;
  }

  int average = getAverageFingerprint();
  //Serial.print("Average RSSI here is: ");
  //Serial.println(average);
  
  if(average > -65)
  {
    //Serial.println("NOW");
    return true;
  }
  else
  {
    return false;
  }
}













//============= old stuff ===========================================


void getTXPower(int rssiValue)
{
  /*
    d = distance
    A = txPower
    n = signal propagation constant
    RSSI = dBm
   */
  //RSSI = -10 n log d + A
  //A = (10 ^ (-RSSI / (10 * 10))) - d

  double txPower = (10 ^ (-rssiValue / (10 * 10))) - 1;
  Serial.print("TXPower = ");
  Serial.println(txPower);
}

double getDistance(int rssi, int txPower)
{
    /*
     * RSSI = TxPower - 10 * n * lg(d)
     * n = 2 (in free space)
     * 
     * d = 10 ^ ((TxPower - RSSI) / (10 * n))
     */

    //return math.pow(10d, ((double) txPower - rssi) / (10 * 2));
    return ((10 ^ ((txPower - rssi) / (10 * 2))) / 10);
}
