#include "BTSerialCommunicationTask.h"
#include "Arduino.h"
#include "kernel/Logger.h"
#include "config.h"
#include "kernel/MsgServiceBT.h"
#include "kernel/Logger.h"

BTSerialCommunicationTask::BTSerialCommunicationTask(GardenController *gc) : gardenController(gc)
{
    setState(START);
}

void BTSerialCommunicationTask::tick()
{
    switch (state)
    {
    case START:
    {
        setState(GET_BT_DATA);
        break;
    }
    case GET_BT_DATA:
    {
        if (BTService.isMsgAvailable())
        {
            Msg *msg = BTService.receiveMsg();
            if (msg != NULL)
            {
                data = msg->getContent();
                BTService.sendMsg(data);
                delete msg;
                setState(WAIT_CONNECTION);
            }
        }
        break;
    }
    case WAIT_CONNECTION:
    {
        int bthConnected = splitString(data, 5).toInt();
        if (bthConnected == 1) // Dal mobile è arrivato uno stato manual
        {
            gardenController->setBthStatus(true);
            setState(UPDATE_SYSTEM_STATUS);
        }
        else
        {
            gardenController->setBthStatus(false);
            setState(GET_BT_DATA);
        }
        break;
    }
    case UPDATE_SYSTEM_STATUS:
    {
        if (gardenController->getSystemStatus() == 1) // Se anche il service è in MANUAL aggiorno i valori
        {
            gardenController->setIrrigationStatus(splitString(data, 0).toInt());
            gardenController->setLed1(splitString(data, 1).toInt());
            gardenController->setLed2(splitString(data, 2).toInt());
            gardenController->setLed3(splitString(data, 3).toInt());
            gardenController->setLed4(splitString(data, 4).toInt());
        }
        setState(GET_BT_DATA);
        break;
    }
    }
}

void BTSerialCommunicationTask::setState(int s)
{
    state = s;
    stateTimestamp = millis();
}

long BTSerialCommunicationTask::elapsedTimeInState()
{
    return millis() - stateTimestamp;
}
