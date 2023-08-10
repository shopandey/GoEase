#include <ESP8266WiFi.h>
#include <WiFiClient.h> 
#include <ESP8266WebServer.h>
#include <ESP8266HTTPClient.h>

/* Set these to your desired credentials. */
//const char *ssid = "Research_LAB";  //ENTER YOUR WIFI SETTINGS
//const char *password = "rlab@uem_jaipur!303807&chomu";
const char *ssid = "IoT";  //ENTER YOUR WIFI SETTINGS
const char *password = "12345678";
const int statusPin= D4;
const int redPin   = D5;
const int greenPin = D6;
const int bluePin  = D7;
const int pwmPin   = D8;

//=======================================================================
//                    Power on setup
//=======================================================================

void setup() {
  
    pinMode(redPin,   OUTPUT);
    pinMode(greenPin, OUTPUT);
    pinMode(bluePin,  OUTPUT);
    pinMode(pwmPin,   OUTPUT);
  
    delay(100);
    
    analogWriteRange(255);
    analogWriteFreq(5000);
    
    pinMode(statusPin,OUTPUT);
    delay(1000);
    digitalWrite(statusPin,1);
    
    Serial.begin(115200);
    WiFi.mode(WIFI_OFF);        //Prevents reconnection issue (taking too long to connect)
    delay(500);
    WiFi.mode(WIFI_STA);        //This line hides the viewing of ESP as wifi hotspot
    
    WiFi.begin(ssid, password);     //Connect to your WiFi router
    Serial.println("");
  
    Serial.print("Connecting");
    // Wait for connection
    while (WiFi.status() != WL_CONNECTED) {
        for (int i=0;i<255;i++){
            setColor(100,i,0,0); //r,g,b
            delay(1);
        }
        for (int i=0;i<255;i++){
            setColor(100,255-i,0,0); //r,g,b
            delay(1);
        }
        for (int i=0;i<255;i++){
            setColor(100,0,i,0); //r,g,b
            delay(1);
        }
        for (int i=0;i<255;i++){
            setColor(100,0,255-i,0); //r,g,b
            delay(1);
        }
        for (int i=0;i<255;i++){
            setColor(100,0,0,i); //r,g,b
            delay(1);
        }
        for (int i=0;i<255;i++){
            setColor(100,0,0,255-i); //r,g,b
            delay(1);
        }
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
  String postData = "id=SL6YUX&key=1234&input_data=iot_lamp";
  
  http.begin("http://18.221.122.79/postdata.php");              //Specify request destination
  http.addHeader("Content-Type", "application/x-www-form-urlencoded");    //Specify content-type header

  int httpCode = http.POST(postData);   //Send the request
  String payload = http.getString();    //Get the response payload

  int pos1 = payload.indexOf("[");
  int pos2 = payload.lastIndexOf("]");
  
  // Parse the string looking for ,
  String data = payload.substring(pos1 + 1, pos2);
  if (data.length()>4)
  {
      Serial.println("[" + data + "]");
      int idx1 = data.indexOf(',');
      int idx2 = data.indexOf(',', idx1 + 1);
      int idx3 = data.indexOf(',', idx2 + 1);
      int idx4 = data.indexOf(',', idx3 + 1);
      int pwm = (data.substring(0, idx1)).toInt();
      int r = (data.substring(idx1 + 1, idx2)).toInt();
      int g = (data.substring(idx2 + 1, idx3)).toInt();
      int b = (data.substring(idx3 + 1, idx4)).toInt();

      setColor(pwm,r,g,b);

      digitalWrite(statusPin,0);
  }

  http.end();  //Close connection
  
  delay(300);  //Post Data at every 5 seconds
  digitalWrite(D4,1);
  delay(200);
}

void setColor(int pwm, int red, int green, int blue) {
  int t_pwm =   map(pwm,    0, 100, 20, 255);
  analogWrite(pwmPin, t_pwm);
  analogWrite(redPin, red );
  analogWrite(greenPin, green);
  analogWrite(bluePin, blue);
}
//=======================================================================
