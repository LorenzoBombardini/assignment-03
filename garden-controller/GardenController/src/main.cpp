/*
 * SMART COFFEE MACHINE - Assignment #02 - ESIOT a.y. 2021-2022
 *
 * Esempio di soluzione.
 *
 */
#include <Arduino.h>
#include "config.h"
#include "kernel/Scheduler.h"
// #include "Logger.h"
#include "UserConsole.h"
#include "model/CoffeeMachine.h"
#include "tasks/UserInteractionManTask.h"
#include "tasks/MakingProductTask.h"
#include "tasks/RemoveProductTask.h"
#include "tasks/SelfTestTask.h"
#include "model/GardenController.h"
#include "tasks/SerialCommunicationTask.h"
#include "tasks/DeviceControllerTask.h"

Scheduler sched;
UserConsole *pUserConsole;
CoffeeMachine *pCoffeeMachine;

void setup()
{
  // Logger.log(".:: Smart Coffee Machine ::.");

  sched.init(50);
  GardenController *pGardenController = new GardenController();
  Task *pSerialTask = new SerialCommunicationTask(pGardenController);
  pSerialTask->init(500);

  Task *pControllerTask = new DeviceControllerTask(pGardenController);
  pControllerTask->init(10);

  sched.addTask(pSerialTask);
  sched.addTask(pControllerTask);

  /*
    pCoffeeMachine = new CoffeeMachine();
    pUserConsole = new UserConsole();

    Task* pUserIntManTask = new UserInteractionManTask(pUserConsole, pCoffeeMachine);
    pUserIntManTask->init(50);

    MakingProductTask* pMakingProdTask = new MakingProductTask(pCoffeeMachine);
    pMakingProdTask->init(100);

    Task* pRemoveProdTask = new RemoveProductTask(pCoffeeMachine);
    pRemoveProdTask->init(200);

    Task* pSelfTestTask = new SelfTestTask(pCoffeeMachine, pMakingProdTask->getMotor());
    pSelfTestTask->init(100);

    sched.addTask(pUserIntManTask);
    sched.addTask(pMakingProdTask);
    sched.addTask(pRemoveProdTask);
    sched.addTask(pSelfTestTask);*/

  // Logger.log("Calibrating...");
  // pUserConsole->notifyCalibrating();
  // pUserConsole->calibrate();
  // Logger.log("done");
  // pUserConsole->notifyReady();
}

void loop()
{
  sched.schedule();
}