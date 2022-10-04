#include "config.h"
#include "GardenController.h"

GardenController::GardenController()
{
}

bool GardenController::getLed1()
{
    return led1;
}
bool GardenController::getLed2()
{
    return led2;
}
int GardenController::getLed3()
{
    return led3;
}
int GardenController::getLed4()
{
    return led4;
}
int GardenController::getAnalogLed(int ledNum)
{
    switch (ledNum)
    {
    case 3:
        return led3;
        break;

    case 4:
        return led4;
        break;
    default:
        return -1;
        break;
    }
}
bool GardenController::getDigitalLed(int ledNum)
{
    switch (ledNum)
    {
    case 1:
        return led1;
        break;

    case 2:
        return led2;
        break;
    default:
        return -1;
        break;
    }
}

int GardenController::getIrrigationStatus()
{
    return irrigator;
}
void GardenController::setIrrigationStatus(int val)
{
    if (val >= 0 && val <= 5)
    {
        irrigator = val;
    }
}
bool GardenController::getBthStatus()
{
    return bthStatus;
}

void GardenController::setLed1(bool val)
{
    led1 = val;
}
void GardenController::setLed2(bool val)
{
    led2 = val;
}
void GardenController::setLed3(int val)
{
    if (val >= 0 && val <= 5)
    {
        led3 = val;
    }
}
void GardenController::setLed4(int val)
{
    if (val >= 0 && val <= 5)
    {
        led4 = val;
    }
}

void GardenController::init()
{
    setLed1(false);
    setLed2(false);
    setLed3(0);
    setLed4(0);
}

void GardenController::setBthStatus(bool val)
{
    bthStatus = val;
}

void GardenController::setSystemStatus(int status){
    state = status;
}

int GardenController::getSystemStatus(){
    return state;
}