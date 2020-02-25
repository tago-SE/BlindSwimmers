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
#include <SPI.h>
#include <SD.h>

/**
 * true = when debugging (will not start arduino unitil serial monitor is active when set to true)
 * false = when not debugging (starts arduino instant when power on)
 */
bool dedugging = true;

//number of sensor looking for by the arduino (sensor near edges)
int const sensorArraySize = 2;
String sensorArray [sensorArraySize];

const int ledPin = LED_BUILTIN; // pin to use for the LED

//Looping modes, used in loop function
/**
 * 0 == paring mode
 * 1 == training mode (paring done)
 * 2 == running mode (paring done)
 * 
 * Default as 0 in init function
 */
int loopingMode;

//timeStamp in long, sent from app when device is first connected
long timeStamp;

//used in training mode, app presses button to turn
bool turnButtonIsPressed;

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
  

  BLEInit();

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

  // set the discovered event handle (used for scanning in training and running mode)
  BLE.setEventHandler(BLEDiscovered, bleCentralDiscoverHandler);

  digitalWrite(ledPin, LOW);
  //==============================================================================

  //set starting mode (0 == paring mode)
  loopingMode = 0;

  //Init fingerprinting
  initFingerPrint();

  timeStamp = 0;

  turnButtonIsPressed = false;

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
  BLE.poll();
  if(loopingMode == 0)
  {
    //paring mode

    //if mode changes, do setup once when modechanges
    if(newMode != loopingMode)
    {
      connectingModeSetup();
      newMode = loopingMode;
    }
    connectingModeFunction();
    
  }
  else if(loopingMode == 1)
  {
    //training mode

    //if mode changes, do setup once when modechanges
    if(newMode != loopingMode)
    {
      trainingModeSetup();
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
      runningModeSetup();
      newMode = loopingMode;
    }
    runningModeFunction();
  }
}

/**
 * At start up, tries to par with a mobile
 */
void connectingModeFunction()
{
  connectingModeLoop();
}

/**
 * When training is selected
 */
void trainingModeFunction()
{
  trainingModeLoop();
}

/**
 * When running (not training)
 */
void runningModeFunction()
{
  runningModeLoop();
}

//============================== device near edge =====================================
/**
 * Check if device is near edge
 */
bool isNearEdge()
{
  /*if(!fingerprintReady)
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
  */

  if(getLastAverageRSSIValue() > -65)
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
