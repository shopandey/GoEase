
#define FEEDBACK_LED_IS_ACTIVE_LOW // The LED on my board is active LOW
#define IR_RECEIVE_PIN          14 // D5
#define IR_RECEIVE_PIN_STRING   "D5"
#define IR_SEND_PIN             12 // D6 - D4/pin 2 is internal LED
#define IR_SEND_PIN_STRING      "D6"
//#define tone(a,b,c) void()      // tone() inhibits receive timer
//#define noTone(a) void()
#define TONE_PIN                42 // Dummy for examples using it
//#define IR_TIMING_TEST_PIN      13 // D7
#define APPLICATION_PIN         0 // D3
#define LED_IDICATOR_PIN        13 // D7

#include <Arduino.h>

#include <ESP8266WiFi.h>
#include <WiFiClient.h> 
#include <ESP8266WebServer.h>
#include <ESP8266HTTPClient.h>

#include <IRremote.h>

#define DELAY_AFTER_SEND 500
#define DELAY_AFTER_LOOP 5000

/* Set these to your desired credentials. */
//const char *ssid = "Research_LAB";  //ENTER YOUR WIFI SETTINGS
//const char *password = "rlab@uem_jaipur!303807&chomu";
//const char *ssid = "BENGAL_3F_2";  //ENTER YOUR WIFI SETTINGS
//const char *password = "UEM_bengal@303807!123@1234";
const char *ssid = "IoT";  //ENTER YOUR WIFI SETTINGS
const char *password = "12345678";
const int statusPin= D4;

//=======================================================================
//                    Power on setup
//=======================================================================

void setup() {
  delay(1000);
  
  IrSender.begin(IR_SEND_PIN, DISABLE_LED_FEEDBACK); // Specify send pin and enable feedback LED at default feedback LED pin

  delay(1000);
  
  analogWriteRange(255);
  analogWriteFreq(5000);
  
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
  String postData = "id=SR5YH3&key=1234&input_data=iot_remote";
  
  http.begin("http://18.221.122.79/device_remote.php");              //Specify request destination
  http.addHeader("Content-Type", "application/x-www-form-urlencoded");    //Specify content-type header

  int httpCode = http.POST(postData);   //Send the request
  String payload = http.getString();    //Get the response payload

  int pos1 = payload.indexOf("[");
  int pos2 = payload.lastIndexOf("]");
  
  // Parse the string looking for ,
  String data = payload.substring(pos1 + 1, pos2);
  if (data.length()>4)
  {
      //Serial.println("[" + data + "]");
      int idx1 = data.indexOf(',');
      int idx2 = data.indexOf(',', idx1 + 1);
      int idx3 = data.indexOf(',', idx2 + 1);
      int idx4 = data.indexOf(',', idx3 + 1);
      int idx5 = data.indexOf(',', idx4 + 1);
      
      String IsData = (data.substring(0, idx1)); //().toInt()
      String type = (data.substring(idx1 + 1, idx2));
      int address = hexToDec(data.substring(idx2 + 1, idx3));
      int command = hexToDec(data.substring(idx3 + 1, idx4));
      int repeats = hexToDec(data.substring(idx4 + 1, idx5));

      if (IsData=="true"){
          Serial.println();
          Serial.print(F("address=0x"));
          Serial.print(address, HEX);
          Serial.print(F(" command=0x"));
          Serial.print(command, HEX);
          Serial.print(F(" repeats="));
          Serial.println(repeats);
          Serial.println();
          sendIR(type, address, command, repeats);
          digitalWrite(statusPin,0);
      }

  }

  http.end();  //Close connection
  
  delay(300);  //Post Data at every 5 seconds
  digitalWrite(statusPin,1);
  delay(200);
}

void sendIR(String type, int sAddress, int sCommand, int sRepeats) {

  if (type=="sony"){
    Serial.println(F("Send Sony/SIRCS with 7 command and 8 address bits"));
    Serial.flush();
    IrSender.sendSony(sAddress & 0xFF, sCommand, sRepeats, SIRCS_15_PROTOCOL);
  }
  else if (type=="samsung"){
    Serial.println(F("Send Samsung with 8 command and 16 address bits"));
    Serial.flush();
    IrSender.sendSamsung(sAddress & 0xFFF, sCommand, sRepeats);
  }
  else ;
  delay(DELAY_AFTER_SEND);
  
}

unsigned int hexToDec(String hexString) {
  
  unsigned int decValue = 0;
  int nextInt;
  
  for (int i = 0; i < hexString.length(); i++) {
    
    nextInt = int(hexString.charAt(i));
    if (nextInt >= 48 && nextInt <= 57) nextInt = map(nextInt, 48, 57, 0, 9);
    if (nextInt >= 65 && nextInt <= 70) nextInt = map(nextInt, 65, 70, 10, 15);
    if (nextInt >= 97 && nextInt <= 102) nextInt = map(nextInt, 97, 102, 10, 15);
    nextInt = constrain(nextInt, 0, 15);
    
    decValue = (decValue * 16) + nextInt;
  }
  
  return decValue;
}

String decToHex(byte decValue, byte desiredStringLength) {
  
  String hexString = String(decValue, HEX);
  while (hexString.length() < desiredStringLength) hexString = "0" + hexString;
  
  return hexString;
}
//=======================================================================
