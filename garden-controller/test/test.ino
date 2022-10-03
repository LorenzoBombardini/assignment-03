#include <Servo.h>

Servo myservo;
#define LED1D 7
#define LED2D 6
#define LED3A 5
#define LED4A 3
#define SERVO_PIN 9
int i = 0;
String data;
int pos = 0;
void setup() {
  Serial.begin(115200);
  Serial.setTimeout(1);
  // put your setup code here, to run once:
  pinMode(LED1D, OUTPUT);
  pinMode(LED2D, OUTPUT);
  pinMode(LED3A, OUTPUT);
  pinMode(LED4A, OUTPUT);
  myservo.attach(SERVO_PIN, 1800, 2280);
  myservo.writeMicroseconds(2040);
}

void loop() {

  /*for (i=1800;i<2300;i++){
    myservo.writeMicroseconds(i);
        Serial.println(i);
        delay(20);
    }*/
  data = Serial.readString();
  for (i = 0; i < 2; i++) {
    if (i == 0 && data[i] == 1) {
      digitalWrite(LED1D, HIGH);
    } else {
      digitalWrite(LED1D, LOW);
    }
  }

  /*
    for (pos = 0; pos <= 180; pos += 1) { // goes from 0 degrees to 180 degrees
    // in steps of 1 degree
    myservo.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);                       // waits 15ms for the servo to reach the position
    }
    for (pos = 180; pos >= 0; pos -= 1) { // goes from 180 degrees to 0 degrees
    myservo.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);                       // waits 15ms for the servo to reach the position
    }*/

}
