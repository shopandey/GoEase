#include <ESP8266WiFi.h>
#include <WiFiClient.h> 
#include <ESP8266WebServer.h>
#include <ESP8266HTTPClient.h>

/* Set these to your desired credentials. */
const char *ssid = "IoT";  //ENTER YOUR WIFI SETTINGS
const char *password = "12345678";
//const char *ssid = "BENGAL_3F_2";  //ENTER YOUR WIFI SETTINGS
//const char *password = "UEM_bengal@303807!123@1234";
const int statusPin= D4;
const int relay    = D7;
const int red_led  = D2;
const int sensor   = D6;


//=======================================================================
//                    Power on setup
//=======================================================================

void setup() {
  delay(1000);
  
  pinMode(relay,   OUTPUT);
  pinMode(red_led, OUTPUT);
  pinMode(sensor,  INPUT);
  
  delay(1000);
  
  pinMode(statusPin,OUTPUT);
  delay(1000);
  digitalWrite(statusPin,1);
  
  Serial.begin(115200);
  WiFi.mode(WIFI_OFF);        //Prevents reconnection issue (taking too long to connect)
  delay(1000);
  WiFi.mode(WIFI_STA);        //This line hides the viewing of ESP as wifi hotspot
  
  WiFi.begin(ssid, password);     //Connect to your WiFi router
  Serial.println("");

  Serial.print("Connecting");
  // Wait for connection
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  //If connection successful show IP address in serial monitor
  Serial.println("");
  Serial.print("Connected to ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());  //IP address assigned to your ESP
}

//=======================================================================
//                    Main Program Loop
//=======================================================================
void loop() {
  
  HTTPClient http;    //Declare object of class HTTPClient

   String getData, Link;
  
  //POST Data
  String postData = "";

  if (digitalRead(sensor)==HIGH){
      postData = "id=DL7VD4&key=1234&input_data=open";
  }
  else{
      postData = "id=DL7VD4&key=1234&input_data=close";
  }
  
  http.begin("http://18.221.122.79/postdata.php");              //Specify request destination
  http.addHeader("Content-Type", "application/x-www-form-urlencoded");    //Specify content-type header

  int httpCode = http.POST(postData);   //Send the request
  String payload = http.getString();    //Get the response payload

  int pos1 = payload.indexOf("[");
  int pos2 = payload.lastIndexOf("]");
  
  // Parse the string looking for ,
  String data = payload.substring(pos1 + 1, pos2);
  if (data.length()>0)
  {
      Serial.println("[" + data + "]");

      int a = data.toInt();
      
      if (a==1){
          digitalWrite(relay,HIGH);
          
          for (int i=0;i<6;i++){
              digitalWrite(red_led,HIGH);
              delay(500);
              digitalWrite(red_led,LOW);
              delay(500);
          }
          
          digitalWrite(relay,LOW);
      }
      
      digitalWrite(statusPin,0);
  }

  http.end();  //Close connection
  
  delay(300);  //Post Data at every 5 seconds
  digitalWrite(statusPin,1);
  delay(200);
}

//=======================================================================
