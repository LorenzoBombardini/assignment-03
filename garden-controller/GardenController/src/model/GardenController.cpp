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
}
int GardenController::getLed3()
{
}
int GardenController::getLed4()
{
}
int GardenController::getAnalogLed(int ledNum)
{
}
bool GardenController::getDigitalLed(int ledNum)
{
}

int GardenController::getIrrigationStatus()
{
}
bool GardenController::getBthStatus()
{
}

void GardenController::setLed1(bool val)
{
}
void GardenController::setLed2(bool val)
{
}
void GardenController::setLed3(int val)
{
}
void GardenController::setLed4(int val)
{
}

void GardenController::init()
{
    led1 = false;
    setLed2(false);
    setLed3(0);
    setLed4(0);
}
