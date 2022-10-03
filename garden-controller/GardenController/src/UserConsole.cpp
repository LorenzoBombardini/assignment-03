#include "UserConsole.h"
#include "Arduino.h"
// #include "Logger.h"
#include "devices/ButtonImpl.h"
#include "devices/Led.h"
#include "kernel/MsgService.h"
#include <avr/sleep.h>
#include "model/CoffeeMachine.h"
#include "config.h"

void wakeUp() {}

UserConsole::UserConsole()
{
  pPot = new Potentiometer(POT_PIN);
  pPir = new Pir(PIR_PIN);
  pButUp = new ButtonImpl(BT_UP_PIN);
  pButDown = new ButtonImpl(BT_DOWN_PIN);
  pButMake = new ButtonImpl(BT_MAKE_PIN);
  attachInterrupt(digitalPinToInterrupt(PIR_PIN), wakeUp, RISING);
}

void UserConsole::init()
{
}

int UserConsole::getSugarLevel()
{
  return pPot->getValue() * 100;
}

void UserConsole::sync()
{
  pButDown->sync();
  pButUp->sync();
  pButMake->sync();
  pPot->sync();
  pPir->sync();
}

void UserConsole::showWelcome()
{

  MsgService.sendMsg("cm:we");
}

void UserConsole::showReady()
{
  MsgService.sendMsg("cm:ok");
}

void UserConsole::showUpdatedSugarLevel(int value)
{
}

void UserConsole::clearMsg()
{
}

void UserConsole::showItem(ProductType *pItem)
{
}

bool UserConsole::isUpPressed()
{
  return pButUp->isClicked();
}

bool UserConsole::isDownPressed()
{
  return pButDown->isClicked();
}

bool UserConsole::isMakePressed()
{
  return pButMake->isClicked();
}

void UserConsole::showMaking(ProductType *pItem)
{
  MsgService.sendMsg("cm:ma");
}

void UserConsole::showProductReady(ProductType *pItem)
{
}

void UserConsole::showSelfTest()
{
  MsgService.sendMsg("cm:st");
}

void UserConsole::showNeedAssistanceNoMoreItems()
{
  MsgService.sendMsg("cm:as:ni");
}
void UserConsole::showNeedAssistanceFailedSelfTest()
{
  MsgService.sendMsg("cm:as:tf");
}

void UserConsole::sleep()
{
  MsgService.sendMsg("cm:zz");
  delay(100);
  set_sleep_mode(SLEEP_MODE_PWR_DOWN);
  sleep_enable();
  sleep_mode();

  MsgService.sendMsg("cm:ok");
}

bool UserConsole::detectedPresence()
{
  return pPir->isDetected();
}

void UserConsole::testButtons()
{
  if (pButUp->isPressed())
  {
  }
  if (pButDown->isPressed())
  {
  }
  if (pButMake->isPressed())
  {
  }
}

void UserConsole::notifyNumItems(int nItems)
{
  MsgService.sendMsg(String("cm:ni:") + nItems);
}

void UserConsole::notifyNumSelfTests(int nSelfTests)
{
  MsgService.sendMsg(String("cm:nt:") + nSelfTests);
}

bool UserConsole::checkCmd(bool &refilled, bool &recovered)
{
  if (MsgService.isMsgAvailable())
  {
    Msg *msg = MsgService.receiveMsg();
    if (msg != NULL)
    {
      if (msg->getContent() == "fi")
      {
        refilled = true;
        recovered = false;
      }
      else if (msg->getContent() == "re")
      {
        refilled = false;
        recovered = true;
      }
      delete msg;
      return true;
    }
    else
    {
      return false;
    }
  }
  else
  {
    return false;
  }
}
