#include <Arduino.h>
#include "config.h"
#include "kernel/Scheduler.h"
#include "model/GardenController.h"
#include "tasks/SerialCommunicationTask.h"
#include "tasks/DeviceControllerTask.h"
#include "tasks/BTSerialCommunicationTask.h"
Scheduler sched;

void setup()
{
  BTService.init();
  sched.init(50);
  GardenController *pGardenController = new GardenController();
  Task *pSerialTask = new SerialCommunicationTask(pGardenController);
  pSerialTask->init(500);

  Task *pBTSerialTask = new BTSerialCommunicationTask(pGardenController);
  pBTSerialTask->init(100);

  Task *pControllerTask = new DeviceControllerTask(pGardenController);
  pControllerTask->init(10);

  sched.addTask(pSerialTask);
  sched.addTask(pControllerTask);
  sched.addTask(pBTSerialTask);
}

void loop()
{
  sched.schedule();
  BTService.serialEventBT();
}

String splitString(String str, int index)
{
  int found = 0;
  int strIdx[] = {0, -1};
  int maxIdx = str.length() - 1;

  for (int i = 0; i <= maxIdx && found <= index; i++)
  {
    if (str.charAt(i) == ',' || i == maxIdx)
    {
      found++;
      strIdx[0] = strIdx[1] + 1;
      strIdx[1] = (i == maxIdx) ? i + 1 : i;
    }
  }
  return found > index ? str.substring(strIdx[0], strIdx[1]) : "";
}