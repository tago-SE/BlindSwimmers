/*
*   SD card read/write
*
*   This code is meant to write data to the SD card file.
*   The circuit:
*   SD card attached to SPI bus as follows:
*
*   - MOSI - pin 11
*   - MISO - pin 12
*   - CLK - pin 13
*   - CS - pin 4 (for MKRZero SD: SDCARD_SS_PIN)
*/


#include <SPI.h>
#include <SD.h>

String TEST_DATA_FILE = "testdata.txt";
String TEST_DATA_HEADER = "id\trssi\trssi_avg\ttime\tturn\t"; 


const int WRITE_BUFFER_SIZE = 5; // The number of messages until they are written to the file
String write_buffer[WRITE_BUFFER_SIZE];
int write_buffer_index = 0;

File myFile;

//
//   Initializes the SD card
//
void initSD()
{
    Serial.print("Initializing SD card...");
    if (!SD.begin(4))
    {
        Serial.println("initialization failed!");
        while (1);  // Failed
    }
    Serial.println("initialization done.");
    myFile = SD.open(TEST_DATA_FILE, FILE_WRITE);

    writeToFileWrapper("SD-Card Started");
}

//
//  Appends a line to a given filename.
//  Example filename: "test.txt".
//  Example line: "Hello there"
//
void writeLineToFile(String filename, String line)
{   
  // open the file. note that only one file can be open at a time,
  // so you have to close this one before opening another.
    myFile = SD.open(filename, FILE_WRITE);
    if (myFile)
    {
        Serial.println(filename + " wrote: " + line);
        myFile.println(line);
        myFile.close();
    }
    else
    {
        Serial.print("error opening: ");
        Serial.println(filename);
    }
}

void writeToFileWrapper(String line) 
{
    // If the buffer is not yet full we save it in the cache and write it at a later stage
    if (write_buffer_index < WRITE_BUFFER_SIZE) 
    {
      write_buffer[write_buffer_index] = line;
      write_buffer_index = write_buffer_index + 1;
    } 
    else 
    {
      Serial.println("writing batch");
      //myFile = SD.open(TEST_DATA_FILE, FILE_WRITE);
      if (myFile)
      {
          for (int i = 0; i < write_buffer_index; i++) 
          {
            Serial.println(TEST_DATA_FILE + " wrote: " + write_buffer[i]);
            myFile.println(write_buffer[i]);
          }
          write_buffer_index = 0;
          //myFile.close();
      }
      else
      {
          Serial.print("error opening: ");
          Serial.println(TEST_DATA_FILE);
      }
      
    }
}

void closeWrapper()
{
  myFile.close();
}

//
// Removes the file
// 
void clearFile(String filename)
{
    Serial.println(filename + " cleared");
    if (SD.exists(filename))
    {
        SD.remove(filename);
    }
}

void clearFileWrapper()
{
  write_buffer_index = 0;
  clearFile(TEST_DATA_FILE);
  writeLineToFile(TEST_DATA_FILE, TEST_DATA_HEADER); // rewrite header
}
