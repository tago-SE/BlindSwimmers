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

#include <ArduinoBLE.h>
#include <string.h>

String arduinoName = "Arduino Swimmer 1";

int loopValue = 0;

int sensorArraySize = 2;
String sensorArray [2];


void setup() {
  Serial.begin(9600);
  while (!Serial);

  // begin initialization
  if (!BLE.begin()) {
    Serial.println("starting BLE failed!");

    while (1);
  }

  Serial.println("BLE Central scan callback");

  // set the discovered event handle
  BLE.setEventHandler(BLEDiscovered, bleCentralDiscoverHandler);

  //=========================== used for connecting with app =================
  // set the local name peripheral advertises
  BLE.setLocalName(arduinoName);

  // start advertising
  BLE.advertise();

  sensorArray[0] = "SimplePeripheral";
  sensorArray[1] = "SimpleBLEBroadcaster";
  //==========================================================================

  // start scanning for peripherals with duplicates
  //BLE.scanForName("SimplePeripheral"); 
  BLE.scan(true); //first 
}

void loop() {
  // poll the central for events
  BLE.poll();
  if(loopValue == 100000) //just a refresh scan value (not corresponding to a specific time, just a value..)
  {
    Serial.println("loop..");
    loopValue = 0;
    restartScan();
  }
  else
  {
    loopValue++;
  }
}
/**
 * Testing 35:69:1f:44:a3:74
 * Sencor F0:F8:F2:DA:31:FD
 */
void bleCentralDiscoverHandler(BLEDevice peripheral) {
  // discovered a peripheral
  //Serial.println("Discovered a peripheral");
  //Serial.println("-----------------------");

  String addressName = peripheral.localName();
  String deviceScanningName = "Simple";
  //strcpy(addressName, peripheral.address());

  for(int i = 0; i < sensorArraySize; i++)
  {
    if(addressName.startsWith(sensorArray[i]))
    {
      handlePeripheralDevice(peripheral);
    }
  }
}

void handlePeripheralDevice(BLEDevice peripheral)
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
  
    Serial.println();
    loopValue = 0;
    restartScan();
}

void restartScan()
{
  BLE.stopScan();
  //BLE.scanForName("SimplePeripheral"); 
  BLE.scan(true); //first 
}
