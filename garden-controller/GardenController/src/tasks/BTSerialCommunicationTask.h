#ifndef __BTCOMMUNICATIONTASK__
#define __BTCOMMUNICATIONTASK__

#include "kernel/Task.h"
#include "model/GardenController.h"
#include "kernel/MsgServiceBT.h"

String splitString(String,int);

class BTSerialCommunicationTask : public Task
{

public:
  BTSerialCommunicationTask(GardenController *gc);
  void tick();

private:
  void setState(int state);
  long elapsedTimeInState();

  enum
  {
    START,
    GET_BT_DATA,
    UPDATE_SYSTEM_STATUS,
    WAIT_CONNECTION,
  } state;

  long stateTimestamp;
  String data;

  GardenController *gardenController;
};

#endif