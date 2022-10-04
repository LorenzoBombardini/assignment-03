#ifndef __CONTROLLERTASK__
#define __CONTROLLERTASK__

#include "kernel/Task.h"
#include "model/GardenController.h"
#include "devices/Led.h"
#include "devices/LedExt.h"

class DeviceControllerTask : public Task
{

public:
  DeviceControllerTask(GardenController *gc);
  void tick();

private:
  void setState(int state);
  long elapsedTimeInState();

  enum
  {
    START,
    READ_SYSTEM_STATUS,
    CONTROL_DEVICES,
  } state;

  long stateTimestamp;
  GardenController *gardenController;

  Led* led1;
  Led* led2;
  LedExt* led3;
  LedExt* led4;
};

#endif