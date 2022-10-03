/*
 * HTTPClient lib -- simple HTTP GET
 */
#include <WiFi.h>
#include <HTTPClient.h>
#define LED 4
#define TEMP 6
#define ADC_VREF_mV    3300.0 // in millivolt
#define ADC_RESOLUTION 4096.0

const char* ssid = "Babini";
const char* password = "";

const char *serverPath = "http://192.168.0.50:8000";

void connectToWifi(const char* ssid, const char* password){
  WiFi.begin(ssid, password);
  Serial.println("Connecting");
  while(WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connected to WiFi network with IP Address: ");
  Serial.println(WiFi.localIP());
}

void setup() {
  Serial.begin(115200); 
  pinMode(LED,OUTPUT);
  connectToWifi(ssid, password);
}

String sendData(String address, float valueTemp, int valueLight){  
  
   HTTPClient http;    
   http.begin(address + "/sensor");      
   http.addHeader("Content-Type", "application/json");    
    
   String msg = 
    String("{ \"temp\": ") + String(valueTemp) + 
    ", \"light\": \"" + String(valueLight) +"\" }";
   
   int retCode = http.POST(msg);
   if (retCode == 201){
       Serial.println("ok");   
     } else {
       Serial.println(String("error: ") + retCode);
     }
       
   String ledVal = http.getString();
   http.end();  
   
   return ledVal;
}


void loop() {
  if (WiFi.status()== WL_CONNECTED){      

    // read the ADC value from the temperature sensor
    int adcVal = analogRead(TEMP);
    // convert the ADC value to voltage in millivolt
    float milliVolt = adcVal * (ADC_VREF_mV / ADC_RESOLUTION);
    // convert the voltage to the temperature in °C
    float valueTemp = milliVolt / 10;

    int valueLight = random(0,5);
    Serial.println(String("temp: ") + valueTemp);
    String code = sendData(serverPath, valueTemp, valueLight);
    Serial.println(code);
    if (code == "1"){
          digitalWrite(LED,HIGH);
    }else{
          digitalWrite(LED,LOW);
    }
    
    delay(5000);

  } else {
    Serial.println("WiFi Disconnected... Reconnect.");
    connectToWifi(ssid, password);
  }
}
