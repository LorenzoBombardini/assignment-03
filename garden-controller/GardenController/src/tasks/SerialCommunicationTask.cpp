#include "SerialCommunicationTask.h"
#include "Arduino.h"
#include "kernel/Logger.h"
#include "config.h"
#include "kernel/MsgService.h"

SerialCommunicationTask::SerialCommunicationTask(GardenController *gc) : gardenController(gc)
{
    setState(START);
}

void SerialCommunicationTask::tick()
{
    switch (state)
    {
    case START:
    {
        gardenController->init();
        MsgService.init();
        setState(GET_SERVICE_DATA);
        break;
    }
    case GET_SERVICE_DATA:
    {
        if (MsgService.isMsgAvailable())
        {
            Msg *msg = MsgService.receiveMsg();
            if (msg != NULL)
            {
                data = msg->getContent();
                delete msg;
                setState(UPDATE_SYSTEM_STATUS);
            }
        }
        break;
    }
    case UPDATE_SYSTEM_STATUS:
    {
        gardenController->setSystemStatus(splitString(data, 5).toInt());
        if (gardenController->getSystemStatus() == 2) // Sono in AUTO aggiorno i valori
        {
            gardenController->setIrrigationStatus(splitString(data, 0).toInt());
            gardenController->setLed1(splitString(data, 1).toInt());
            gardenController->setLed2(splitString(data, 2).toInt());
            gardenController->setLed3(splitString(data, 3).toInt());
            gardenController->setLed4(splitString(data, 4).toInt());
        }

        setState(SET_SERVICE_DATA);
        break;
    }

    case SET_SERVICE_DATA:
    {

        MsgService.sendMsg(String(gardenController->getBthStatus())); 
        setState(GET_SERVICE_DATA);
        break;
    }
    }
}

void SerialCommunicationTask::setState(int s)
{
    state = s;
    stateTimestamp = millis();
}

long SerialCommunicationTask::elapsedTimeInState()
{
    return millis() - stateTimestamp;
}
