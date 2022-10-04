#include "SerialCommunicationTask.h"
#include "Arduino.h"
#include "kernel/Logger.h"
#include "config.h"
#include "kernel/MsgService.h"

SerialCommunicationTask::SerialCommunicationTask(GardenController *gc) : gardenController(gc)
{
    setState(START);
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
        gardenController->setIrrigationStatus(splitString(data, 0).toInt());
        gardenController->setLed1(splitString(data, 1).toInt());
        gardenController->setLed2(splitString(data, 2).toInt());
        gardenController->setLed3(splitString(data, 3).toInt());
        gardenController->setLed4(splitString(data, 4).toInt());
        gardenController->setSystemStatus(splitString(data, 5).toInt());

        setState(SET_SERVICE_DATA); // TODO
        break;
    }

    case SET_SERVICE_DATA:
    {

        MsgService.sendMsg(String(gardenController->getSystemStatus())); // TODO
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
