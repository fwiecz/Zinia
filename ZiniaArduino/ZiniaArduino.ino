// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License along
// with this program; if not, write to the Free Software Foundation, Inc.,
// 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

#include <Adafruit_NeoPixel.h>
#include <WiFiClient.h>
#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include "src/LedManager/LedManager.h"

// How many LEDs are connected?
#define NUM_LEDS 45

// The data output pin for the LEDs 
#define LED_DATA 5 // 5 is D1 on WeMos D1 mini

// The button used for WPS connection. Use a pin with internal pullup resistor!
#define WPS_BUTTON_PIN 0 // 0 is D3 on WeMos D1 mini

// The LED used to display technical information
#define INFO_LED 2 // Built-in LED on WeMos D1 mini

// The SSID (name) and password for your Wifi
const char* ssid = "";
const char* password = "";

// When the LEDs are set to a single color, this speed is used instead of [colorSpeed].
const float singleColorSpeed = 0.001;

// The Brightness value will not be send as float so a maximum value for int conversion has to be set.
const int maxBrightnessAsInt = 255;

// The NeoPixel object is used to communicate with the LEDs.
Adafruit_NeoPixel pixel = Adafruit_NeoPixel(NUM_LEDS, LED_DATA, NEO_GRB + NEO_KHZ800);

// Available modes:
#define MODE_READY 0
#define MODE_SINGLE_COLOR 1

// The server object is responsible for handling incoming http requests.
ESP8266WebServer server(80);

// The LedManager takes care of color buffers and interpolation and so on.
LedManager manager = LedManager(NUM_LEDS, singleColorSpeed);

const char* empty = "";
const char* textPlain = "text/plain";
const char* applicationJson = "application/json";
const char* emptyJson = "{}";

// Whether the server should take action to further reuqests.
// Requests will still be answered with status 200 (OK).
int isOn = 1;

// The current Mode of the device
int mode = MODE_READY;

// How fast colors should change
float colorSpeed = 0.01f;

// Positions: numLights: 12-15, isOn: 25
char statusMsg[] = "{\"numLeds\":\"    \",\"isOn\": }";

// Positions: red: 6-9, green: 17-20, blue: 28-31
char colorMsg[] = "{\"r\":\"    \",\"g\":\"    \",\"b\":\"    \"}";

// Positions: brightness: 7-10
char brightnessMsg[] = "{\"br\":\"    \"}";

short lastSingleColor[4];
int lastBrightness = maxBrightnessAsInt;

#define NUM_WPS_DEBOUNCE 5
bool inWpsMode = false;
byte wpsButtonDebounce[NUM_WPS_DEBOUNCE];
byte wpsButtonCounter = 0;

void setup() {
  pinMode(LED_DATA, OUTPUT);
  pinMode(INFO_LED, OUTPUT);
  pinMode(WPS_BUTTON_PIN, INPUT_PULLUP);

  // Communicate to the computer
  Serial.begin(115200);

  // LOW means ON! While the chip tries to connect, the led will be on.
  digitalWrite(INFO_LED, LOW);
  updatePixels();

  WiFi.mode(WIFI_STA);
  WiFi.setAutoReconnect(true);

  // If no credentials given
  if(strcmp(ssid, empty) == 0 || strcmp(password, empty) == 0) {
    WiFi.begin();
  }
  else { // Connect to the given credentials
    WiFi.begin(ssid, password);
  }

  initializeServer();
}

// The Status Message containing software/hardware information.
void sendStatus() {
  writeNumberTo(statusMsg, 12, 4, NUM_LEDS);
  statusMsg[25] = ascii(isOn);
  server.send(200, applicationJson, statusMsg);
}

// Send the color if the device is in MODE_SINGLE_COLOR mode
void sendColor() {
  if(mode == MODE_SINGLE_COLOR) {
    writeNumberTo(colorMsg, 6, 4, lastSingleColor[0]); // red
    writeNumberTo(colorMsg, 17, 4, lastSingleColor[1]); // green
    writeNumberTo(colorMsg, 28, 4, lastSingleColor[2]); // blue
  } else {
    writeNullTo(colorMsg, 6);
    writeNullTo(colorMsg, 17);
    writeNullTo(colorMsg, 28);
  }
  server.send(200, applicationJson, colorMsg);
}

// Sends the target brightness as int. 
void sendBrighness() {
  writeNumberTo(brightnessMsg, 7, 4, lastBrightness);
  server.send(200, applicationJson, brightnessMsg);
}

// Initializes REST calls
void initializeServer() {
  server.on("/", sendStatus);
  server.on("/setSingleColor", setSingleColor);
  server.on("/setOn", setOn);
  server.on("/setOff", setOff);
  server.on("/getColor", sendColor);
  server.on("/getBrightness", sendBrighness);
  server.on("/setBrightness", setBrightness);
  server.onNotFound( [](){
    server.send(404, textPlain, F("Page not found"));
  });
  server.begin();
}

// Sets isOn to 0 and turns the leds off (all channels to 0)
void setOff() {
  isOn = 0;
  manager.setSingleColor(0, 0, 0);
  server.send(200, applicationJson, emptyJson);
}

// Sets isOn to 1, but 
void setOn() {
  isOn = 1;
  if(mode == MODE_SINGLE_COLOR) {
    manager.setSingleColor(lastSingleColor[0], lastSingleColor[1], lastSingleColor[2]);
  }
  server.send(200, applicationJson, emptyJson);
}

// Interpolates to the given color. [colorSpeed] has no effect here.
void setSingleColor() {
  if(isOn) {
    mode = MODE_SINGLE_COLOR;
    lastSingleColor[0] = server.arg(F("r")).toInt();
    lastSingleColor[1] = server.arg(F("g")).toInt();
    lastSingleColor[2] = server.arg(F("b")).toInt();
    manager.setSingleColor(lastSingleColor[0], lastSingleColor[1], lastSingleColor[2]);
    sendColor();
    return;
  }
  server.send(200, applicationJson, emptyJson);
}

// Interpolates to the given brightness.
void setBrightness() {
  if(isOn) {
    lastBrightness = server.arg(F("br")).toInt();
    float br = (float)lastBrightness / (float)maxBrightnessAsInt;
    manager.setBrightness(br);
    sendBrighness();
    return;
  }
  server.send(200, applicationJson, emptyJson);
}

// Starts the WPS setup
void wpsSetUp() {
  if(!inWpsMode) {
    inWpsMode = true;
    for(int i=0; i<10; i++) {
      digitalWrite(INFO_LED, i % 2);
      delay(100);
    }
    WiFi.beginWPSConfig();
    digitalWrite(INFO_LED, LOW);
    delay(100);
    inWpsMode = false;
  }
}

// Checks whether the WPS button is pressed for some time
void checkWpsButton() {
  if(wpsButtonCounter > 100) {
    wpsButtonCounter = 0;
    for(int i=0; i<NUM_WPS_DEBOUNCE-1; i++) {
      wpsButtonDebounce[i] = wpsButtonDebounce[i+1];
    }
    wpsButtonDebounce[NUM_WPS_DEBOUNCE - 1] = (digitalRead(WPS_BUTTON_PIN) == LOW);
    int count = 0;
    for(int i=0; i<NUM_WPS_DEBOUNCE; i++) {
      count += wpsButtonDebounce[i];
    }
    if(count == NUM_WPS_DEBOUNCE) {
      wpsSetUp();
    }
  }
  wpsButtonCounter ++;
}

// Returns the given number as ascii digit (0-9).
int ascii(int num) {
  return num + 48;
}

// Writes 'null' to the specified char array at the given position;
void writeNullTo(char* array, int pos) {
  array[pos] = 'n'; array[pos+1] = 'u'; array[pos+2] = 'l'; array[pos+3] = 'l';
}

// Writes the given number to the specified char array at the given position. 
void writeNumberTo(char* array, int pos, int digits, int number) {
  for(int i=0; i<digits; i++) {
    array[pos+i] = ascii((number / tenPow(digits-i-1)) % 10);
  }
}

// Returns 10^n
int tenPow(int n) {
  if(n == 0)return 1;
  if(n == 1)return 10;
  if(n == 2)return 100;
  if(n == 3)return 1000;
  return tenPow(n - 1) * 10; 
}

// The main update function for led pixels.
void updatePixels() {
  for(int i=0; i<NUM_LEDS; i++) {
    pixel.setPixelColor(i, manager.getRed(i), manager.getGreen(i), manager.getBlue(i));
  }
  pixel.show();
}

void loop() {
  if(WiFi.status() != WL_CONNECTED) { // If no connection, turn the info led on
    digitalWrite(INFO_LED, LOW);
  }
  else { // If connection established, handle requests
    digitalWrite(INFO_LED, HIGH);
    server.handleClient();
  }

  manager.update(colorSpeed);
  updatePixels();

  checkWpsButton();
}
