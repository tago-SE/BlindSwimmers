
File myFile;

String TEST_DATA_FILE = "testdata.txt";
String TEST_DATA_HEADER = "id\trssi\trssi_avg\ttime\tturn\t"; 

/**
 * Initializes the SD card
 */
void initSD()
{
  if(dedugging)
  {
    Serial.print("Initializing SD card...");
  }
    
  if (!SD.begin(4))
  {
      Serial.println("initialization failed!");
      while (1);  // Failed
  }
  
  if(dedugging)
  {
    Serial.println("initialization done.");
  }
    
}

/**
 * Appends a line to a given filename.
 * Example filename: "test.txt".
 * Example line: "Hello there"
 */
void writeLineToFile(String filename, String line)
{
  if (!SD.begin(4))
  {
      Serial.println("initialization failed!");
      while (1);  // Failed
  }
    
  // open the file. note that only one file can be open at a time,
  // so you have to close this one before opening another.
  myFile = SD.open(filename, FILE_WRITE);
  if (myFile)
  {
    if(dedugging)
    {
      Serial.println(filename + " wrote: " + line);
    }
    myFile.println(line);
    myFile.close();
  }
  else
  {
    if(dedugging)
    {
      Serial.print("error opening: ");
      Serial.println(filename);
    }
  }
}

void writeToFileWrapper(String line) 
{
   writeLineToFile(TEST_DATA_FILE, line);
}

/**
 * Removes the file
 */
void clearFile(String filename)
{
    if (!SD.begin(4))
    {
        Serial.println("initialization failed!");
        while (1);  // Failed
    }
    if(dedugging)
    {
      Serial.println(filename + " cleared");
    }
    if (SD.exists(filename))
    {
        SD.remove(filename);
    }
}

void clearFileWrapper()
{
  clearFile(TEST_DATA_FILE);
  writeLineToFile(TEST_DATA_FILE, TEST_DATA_HEADER); // rewrite header
}

void clearSDCard()
{
  Serial.println("Clering SD Card");
}
