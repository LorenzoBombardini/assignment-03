/*
 * HTTPClient lib -- simple HTTP GET
 */
#include <WiFi.h>
#include <HTTPClient.h>

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
  connectToWifi(ssid, password);
}

String sendData(String address, int valueTemp, int valueLight){  
  
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

    int valueTemp = random(15,20);
    int valueLight = random(15,20);
    String code = sendData(serverPath, valueTemp, valueLight);
    Serial.println(code);
    if (code == "1"){
          //led on
    }else{
          //led off
    }
    
    delay(5000);

  } else {
    Serial.println("WiFi Disconnected... Reconnect.");
    connectToWifi(ssid, password);
  }
}
