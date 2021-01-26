#include <WiFi.h>
 
const char* ssid = "FRITZ!Box 7590 PB";
const char* password =  "32589156918351329119";
 
const uint16_t port = 13579;
const char * host = "192.168.178.45";
 
void setup()
{

  pinMode(26, INPUT);
  
  Serial.begin(115200);
 
  wifiConnect();

  
  delay(1000);
}


void wifiConnect(){
  Serial.print("Connecting to WiFi...");
    WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.println("...");
  }
 
  Serial.print("WiFi connected with IP: ");
  Serial.println(WiFi.localIP());
}


WiFiClient client;


String device_password = "SYHVsangj8kyQRTSfABA";
String device_name = "Bewegungssensor2";
int data_handeling_mode = 0;

void connection_start(){
  Serial.println("Trying to connect to ControllServer...");
  if(!client.connect(host, port)){
    Serial.println("Error while connecting! Server doesn't respond! Retrying...");
    delay(2000);
    connection_start();
    return;
  }

  Serial.println("Successfully connect to ControllServer...");
  //Login Syntax: login:{password}:{device_name}:{data_handeling}
  client.println("login:"+device_password+":"+device_name+":"+data_handeling_mode);
  sendState();
  Serial.println("Successfully sent logindata to ControllServer...");
  
}
void connection_stop(){
  client.stop();
}

void sendState(){
  Serial.println("Sending SensorState to ControllServer...");
  bool currentValue = digitalRead(26);
  if(currentValue==HIGH)
    client.println("set:1");
  else
    client.println("set:0");
  Serial.println("SensorState successfully sent to ControllServer...");
}

bool valueBefore = false;

int pingticker = 0;

void loop()
{
   
  if(client.connected()){

    bool currentValue = digitalRead(26);
    
    if(valueBefore!=currentValue)
      sendState();

    valueBefore = currentValue;
  }else{
     if(WiFi.status() != WL_CONNECTED){
      connection_start();
     }else{
      wifiConnect();
     }
  }
  delay(100);

  //10 Ticks in der Sekunde (alle 30 Sec = alle 300 Ticks)
    if(client.connected()){
      pingticker++;
      if(pingticker>=300){
        pingticker=0;
        client.println("ping");
      }
    }
  
  
}
