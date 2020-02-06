/*
  Callback LED

  This example creates a BLE peripheral with service that contains a
  characteristic to control an LED. The callback features of the
  library are used.

  The circuit:
  - Arduino MKR WiFi 1010, Arduino Uno WiFi Rev2 board, Arduino Nano 33 IoT,
    Arduino Nano 33 BLE, or Arduino Nano 33 BLE Sense board.

  You can use a generic BLE central app, like LightBlue (iOS and Android) or
  nRF Connect (Android), to interact with the services and characteristics
  created in this sketch.

  This example code is in the public domain.
*/

#include <ArduinoBLE.h>

BLEService ledService("19B10000-E8F2-537E-4F6C-D104768A1214"); // create service

// create switch characteristic and allow remote device to read and write
BLEByteCharacteristic switchCharacteristic("19B10001-E8F2-537E-4F6C-D104768A1214", BLERead | BLEWrite);

const int ledPin = LED_BUILTIN; // pin to use for the LED
const int BUFFER_SIZE_IN_BYTES = 25;

//for incomming bytes by remote devices
int commandByte = -1;
int dataByte = -1;
int newCommand = 1; //1 = a command to read, 2 = data for a command to read

void setup() {
  Serial.begin(9600);
  while (!Serial);
  
  pinMode(ledPin, OUTPUT); // use the LED pin as an output

  // begin initialization
  if (!BLE.begin()) {
    Serial.println("starting BLE failed!");

    while (1);
  }

  // set the local name peripheral advertises
  BLE.setLocalName("Swimmer Central Device");
  // set the UUID for the service this peripheral advertises
  BLE.setAdvertisedService(ledService);

  // add the characteristic to the service
  ledService.addCharacteristic(switchCharacteristic);

  // add service
  BLE.addService(ledService);

  // assign event handlers for connected, disconnected to peripheral
  BLE.setEventHandler(BLEConnected, blePeripheralConnectHandler);
  BLE.setEventHandler(BLEDisconnected, blePeripheralDisconnectHandler);

  // assign event handlers for characteristic
  switchCharacteristic.setEventHandler(BLEWritten, switchCharacteristicWritten);
  // set an initial value for the characteristic
  switchCharacteristic.setValue(0);

  // start advertising
  BLE.advertise();

  Serial.println(("Bluetooth device active, waiting for connections..."));
}

void loop() {
  // poll for BLE events
  BLE.poll();
}

void blePeripheralConnectHandler(BLEDevice central) {
  // central connected event handler
  Serial.print("Connected event, central: ");
  Serial.println(central.address());

  Serial.print("RSSI = ");
  Serial.println(BLE.rssi());
  //digitalWrite(ledPin, HIGH);
}

void blePeripheralDisconnectHandler(BLEDevice central) {
  // central disconnected event handler
  Serial.print("Disconnected event, central: ");
  Serial.println(central.address());
  //digitalWrite(ledPin, LOW);
}

/**
 * Reads ONE incoming byte from remote device and puts it in command or data integers
 * 
 * First reads the command (a.e 1 = first command, 2 = second command and so on)
 * Next byte sent is the data for the command
 */
void switchCharacteristicWritten(BLEDevice central, BLECharacteristic characteristic) {

/*
  //testing
  int BUFFER_SIZE_IN_BYTES = 25;
  
  byte sentByte [BUFFER_SIZE_IN_BYTES];

  //resets array with 0
  for(int i = 0; i < BUFFER_SIZE_IN_BYTES; i++)
  {
    sentByte[i] = 0;
  }

  Serial.print("value length = ");
  Serial.println(switchCharacteristic.valueLength());

  switchCharacteristic.read();
  Serial.print("readValue = ");
  Serial.println(switchCharacteristic.readValue(sentByte[23]));

  switchCharacteristic.read();
  Serial.print("readValue = ");
  Serial.println(switchCharacteristic.readValue(sentByte[24]));
  
  int byteRead = switchCharacteristic.readValue(sentByte, 3);

  

  //print text sent by phone
  Serial.print("Characteristic event, received: ");
  for(int i = 0; i < BUFFER_SIZE_IN_BYTES; i++)
  {
    Serial.print(sentByte[i]);
  }
  
  Serial.print(", byteLenght read: ");
  Serial.print(byteRead);
  Serial.println("");
  
  // central wrote new value to characteristic, update LED
  Serial.print("Characteristic event, written: ");

  Serial.print(switchCharacteristic.value());
  */

  byte tmp = 0;
  switchCharacteristic.readValue(tmp);
  Serial.print("tmp: ");
  Serial.println(tmp);

  if(newCommand == 1)
  {
    commandByte = tmp;
    Serial.print("sets commandByte to: ");
    Serial.println(commandByte);
    newCommand = 0;
  }
  else
  {
    dataByte = tmp;
    Serial.print("sets dataByte to: ");
    Serial.println(dataByte);
    executeCommand(commandByte, dataByte);
    
    newCommand = 1;
  }
  

  if (switchCharacteristic.value()) {
    Serial.println("LED on");
    digitalWrite(ledPin, HIGH);
  } else {
    Serial.println("LED off");
    digitalWrite(ledPin, LOW);
  }
}

/**
 * Execute command
 * 
 * command = 1 ==> do stuff 1
 * command = 2 ==> do stuff 2
 * and so on
 */
void executeCommand(int command, int data)
{
  Serial.print("in executeCommand function, command: ");
  Serial.print(command);
  Serial.print(" and data");
  Serial.println(data);
  if(command == -1 || data == -1)
  {
    //something went wrong
  }
  else if(command == 1)
  {
    Serial.print("Executing command 1 with data ");
    Serial.println(data);
  }
  else if(command == 2)
  {
    Serial.print("Executing command 2 with data ");
    Serial.println(data);
  }

  commandByte = -1;
  dataByte = -1;
}
