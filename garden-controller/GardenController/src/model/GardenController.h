#ifndef __GC__
#define __GC__

#include <Arduino.h>

class GardenController
{
private:
    bool led1;
    bool led2;
    int led3;
    int led4;

    int irrigator;
    bool bthStatus;

    long servoWaitingTime;

    enum
    {
        ALARM,
        MANUAL,
        AUTO,
    } state;

public:
    GardenController();

    bool getLed1();
    bool getLed2();
    int getLed3();
    int getLed4();
    int getAnalogLed(int ledNum);
    bool getDigitalLed(int ledNum);

    int getIrrigationStatus();
    bool getBthStatus();

    void setLed1(bool val);
    void setLed2(bool val);
    void setLed3(int val);
    void setLed4(int val);
    void setIrrigationStatus(int val);

    void setBthStatus(bool val);
    int getSystemStatus();
    void setSystemStatus(int state);


    void init();
    ~GardenController();
};

#endif