#include "DeviceControllerTask.h"
#include "Arduino.h"
#include "kernel/Logger.h"
#include "config.h"

DeviceControllerTask::DeviceControllerTask(GardenController *gc) : gardenController(gc)
{
    setState(START);
}

void DeviceControllerTask::tick()
{
    switch (state)
    {
    case START:
    {
        led1 = new Led(LED1_PIN);
        led2 = new Led(LED2_PIN);
        led3 = new LedExt(LED3_PIN);
        led4 = new LedExt(LED4_PIN);
        setState(READ_SYSTEM_STATUS);
        break;
    }
    case READ_SYSTEM_STATUS:
    {
        if (gardenController->getSystemStatus() == 2)
        {
            setState(CONTROL_DEVICES);
        }
        break;
    }
    case CONTROL_DEVICES:
    {
        gardenController->getLed1() ? led1->switchOn() : led1->switchOff();
        gardenController->getLed2() ? led2->switchOn() : led2->switchOff();
        if (gardenController->getLed3() > 0)
        {
            led3->setIntensity(map(gardenController->getLed3(), 0, 4, 0, 255));
            led4->setIntensity(map(gardenController->getLed4(), 0, 4, 0, 255));
            led3->switchOn();
            led4->switchOn();
        }else{
            led3->switchOff();
            led4->switchOff();
        }
        setState(READ_SYSTEM_STATUS); // TODO
        break;
    }
    }
}

void DeviceControllerTask::setState(int s)
{
    state = s;
    stateTimestamp = millis();
}

long DeviceControllerTask::elapsedTimeInState()
{
    return millis() - stateTimestamp;
}
