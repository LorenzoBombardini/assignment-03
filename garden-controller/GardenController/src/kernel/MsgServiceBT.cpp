#include "Arduino.h"
#include "MsgServiceBT.h"
#include "config.h"
#include "Logger.h"
MsgServiceBT BTService(BT_RX, BT_TX);

MsgServiceBT::MsgServiceBT(int rxPin, int txPin)
{
    channel = new SoftwareSerial(rxPin, txPin);
}

void MsgServiceBT::init()
{
    content.reserve(256);
    channel->begin(BT_BAUD_RATE);
    availableMsg = NULL;
}

bool MsgServiceBT::sendMsg(Msg msg)
{
    channel->println(msg.getContent());
}

bool MsgServiceBT::isMsgAvailable()
{
    while (channel->available())
    {
        char ch = (char)channel->read();
        if (ch == '\n')
        {
            availableMsg = new Msg(content);
            content = "";
            return true;
        }
        else
        {
            content += ch;
        }
    }
    return false;
}

void MsgServiceBT::serialEventBT()
{
    BTService.isMsgAvailable();
}

Msg *MsgServiceBT::receiveMsg()
{
    if (availableMsg != NULL)
    {
        Msg *msg = availableMsg;
        availableMsg = NULL;
        return msg;
    }
    else
    {
        return NULL;
    }
}