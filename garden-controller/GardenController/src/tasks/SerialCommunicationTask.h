#ifndef __COMMUNICATIONTASK__
#define __COMMUNICATIONTASK__

#include "kernel/Task.h"
#include "model/GardenController.h"

class SerialCommunicationTask : public Task
{

public:
  SerialCommunicationTask(GardenController *gc);
  void tick();

private:
  void setState(int state);
  long elapsedTimeInState();

  enum
  {
    START,
    GET_SERVICE_DATA,
    UPDATE_SYSTEM_STATUS,
    SET_SERVICE_DATA,
  } state;

  long stateTimestamp;
  String data;

  GardenController *gardenController;
};

#endif