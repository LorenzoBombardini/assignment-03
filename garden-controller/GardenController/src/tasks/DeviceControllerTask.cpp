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
        if (gardenController->getLed1())
        {
            led1->switchOn();
        }
        else
        {
            led1->switchOff();
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
