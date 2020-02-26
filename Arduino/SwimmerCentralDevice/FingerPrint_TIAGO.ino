// Maximum number of devices that the library manages
int const MAX_DEVICES = 20;

// This is the maximum number of saved RSSI that the average is based on
int const MAX_RSSI = 10;

// This is the value (or error code) returned if there is no RSSI value stored
int const NO_RSSI_VALUE = 0;


// Temp variable
int total;

struct Device
{
  String id;                  // Device ID
  int rssiArray[MAX_RSSI];    // Bucket
  int size;                   // Current bucket size, maximum is MAX_RSSI
  int index;                  // Current index position of the latest received RSSI

  void clear()
  {
    for (int j = 0; j < size; j++)
    {
      rssiArray[j] = 0;
    }
    size = 0;
    index = 0;
    id = "";
  }

  void addRSSI(int rssi) {
    if (size < MAX_RSSI) {
      rssiArray[index++] = rssi;
      size = index;
    }
    else {
      if (index >= size) {
        index = 0;
      }
      rssiArray[index++] = rssi;
    }
  }

  int getAverageRSSI() {
    if (size == 0)
      return NO_RSSI_VALUE;
    total = 0;
    for (int i = 0; i < size; i++) {
      total += rssiArray[i];
    }
    return total/size;
  }

} Device;

struct Device devices[MAX_DEVICES];
int devices_size = 0;
int avgRSSI = 0;

/*
*   Saves the incoming RSSI into the bucket associated with the provided id.
*   Returns the average RSSI from the previously received RSSI if any.
*/
int getAverageRSSI(int rssi, String id)
{
  for (int i = 0; i < devices_size; i++)
  {
    if (devices[i].id == id)
    {
      devices[i].addRSSI(rssi);
      avgRSSI = devices[i].getAverageRSSI();
      return avgRSSI;
    }
  }
  if (devices_size >= MAX_DEVICES) {
    return NO_RSSI_VALUE;
  }
  // Add new device
  devices[devices_size].clear();
  devices[devices_size].id = id;
  devices[devices_size].addRSSI(rssi);
  devices_size++;
  avgRSSI = devices[devices_size - 1].getAverageRSSI();
  return avgRSSI;
}

/**
 * Returns avgRSSI from last getAverageRSSI
 */
int getLastAverageRSSIValue()
{
   return avgRSSI;
}

/**
 * Init fingerprint
 */
void initFingerPrint()
{
  clearRSSIHistory();
}

/*
*   Clears the RSSI
*/
void clearRSSIHistory()
{
  for (int i = 0; i < MAX_DEVICES; i++)
  {
    devices[i].clear();
  }
  devices_size = 0;
}
