//============================== fingerprinting ==========================================
/*
 * Wrapper to algorithm
 */
void fingerprint(int rssiValue)
{
  addRssiToArray(rssiValue);
}

/**
 * Adds a new RSSI value to arraylist
 * ¨
 * If arraylist allready filled, start to fill it from start again
 */
void addRssiToArray(int rssiValue)
{
  fingerprintArray[fingerprintIndex] = rssiValue;
  fingerprintIndex++;

  if(fingerprintIndex == fingerprintArraySize)
  {
    //array now filled
    fingerprintIndex = 0;
    //fingerprintReady = true;
  }
}

/**
 * Gets the average rssi from array
 */
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

/**
 * Init array with 0s
 */
void zeroFingerprintArray()
{
  for(int i = 0; i < fingerprintArraySize; i++)
  {
    fingerprintArray[i] = 0;
  }
}
