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
#include <math.h>

int loopValue = 0;

int sensorArraySize = 2;
String sensorArray [2];

const int ledPin = LED_BUILTIN; // pin to use for the LED

int fingerprintArray [10];
int fingerprintArraySize = 10;
int fingerprintIndex = 0;

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
  BLE.setLocalName("Arduino Swimmer 1");

  // start advertising
  BLE.advertise();

  sensorArray[0] = "SimplePeripheral";
  sensorArray[1] = "SimpleBLEBroadcaster";
  //==========================================================================

  zeroFingerprintArray();

  // start scanning for peripherals with duplicates
  //BLE.scanForName("SimplePeripheral"); 
  BLE.scan(true); //first 
}

void loop() {
  // poll the central for events
  BLE.poll();
  if(loopValue == 100000)
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

    fingerprint(peripheral.rssi());

    //double distance = getDistance(peripheral.rssi(), 9);
    //print distance
    //Serial.print("Distance is: ");
    //Serial.println(distance);
    //getTXPower(peripheral.rssi());
    
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

void fingerprint(int rssiValue)
{
  if(fingerprintIndex < fingerprintArraySize)
  {
    fillArray(rssiValue);
  }
  else
  {
    pushFingerprintArray(rssiValue);
  }
}

void fillArray(int rssiValue)
{
  fingerprintArray[fingerprintIndex] = rssiValue;
  fingerprintIndex++;
}

void pushFingerprintArray(int rssiValue)
{
  
  for(int i = 0; i < fingerprintArraySize; i++)
  {
    if(i == (fingerprintArraySize-1))
    {
      fingerprintArray[i] = rssiValue;
    }
    else
    {
      fingerprintArray[i] = fingerprintArray[i + 1];
    }
  }

  int average = getAverageFingerprint();
  Serial.print("Average RSSI here is: ");
  Serial.println(average);

  if(average > -65)
  {
    Serial.println("NOW");
    Serial.println("NOW");
    Serial.println("NOW");
    Serial.println("NOW");

    digitalWrite(ledPin, HIGH);
  }
  else
  {
    digitalWrite(ledPin, LOW);
  }
}

int getAverageFingerprint()
{
  double average = 0;
  for(int i = 0; i < fingerprintArraySize; i++)
  {
    average = average + fingerprintArray[i];
  }

  average = average / fingerprintArraySize;
  return average;
}

void zeroFingerprintArray()
{
  for(int i = 0; i < fingerprintArraySize; i++)
  {
    fingerprintArray[i] = 0;
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
