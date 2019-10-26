

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

// When the LEDs are set to a single color.
const float singleColorSpeed = 0.001;

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
const char* argBody = "plain";
const char* argSpeed = "speed";

// Whether the server should take action to further reuqests.
// Requests will still be answered with status 200 (OK).
int isOn = 1;

// The current Mode of the device
int mode = MODE_READY;

// Positions: numLights: 12-15, isOn: 25, isRGBW: 36
char statusMsg[] = "{\"numLeds\":\"    \",\"isOn\": ,\"isRGBW\": }";

// Positions: red: 6-9, green: 17-20, blue: 28-31, white: 39-42
char colorMsg[] = "{\"r\":\"    \",\"g\":\"    \",\"b\":\"    \",\"w\":\"    \"}";

// Positions: brightness: 7-10
char brightnessMsg[] = "{\"br\":\"    \"}";

uint16_t lastSingleColor[4];
int lastBrightness = MAX_BRIGHTNESS;

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
  ESP.wdtEnable(5000);

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
#ifdef IS_RGBW
  statusMsg[36] = ascii(1);
#else
  statusMsg[36] = ascii(0);
#endif
  server.send(200, applicationJson, statusMsg);
}

// Send the color if the device is in MODE_SINGLE_COLOR mode
void sendColor() {
  writeNumberTo(colorMsg, 6, 4, lastSingleColor[0] / COLOR_DEPTH_MULTIPLY); // red
  writeNumberTo(colorMsg, 17, 4, lastSingleColor[1] / COLOR_DEPTH_MULTIPLY); // green
  writeNumberTo(colorMsg, 28, 4, lastSingleColor[2] / COLOR_DEPTH_MULTIPLY); // blue
  writeNumberTo(colorMsg, 39, 4, lastSingleColor[3] / COLOR_DEPTH_MULTIPLY); // white
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
  server.on("/setWhite", setWhite);
  server.on("/setOn", setOn);
  server.on("/setOff", setOff);
  server.on("/getColor", sendColor);
  server.on("/getBrightness", sendBrighness);
  server.on("/setBrightness", setBrightness);
  server.on("/setColorSequence", setColorSequence);
  server.onNotFound( [](){
    server.send(404, textPlain, F("Page not found"));
  });
  server.begin();
}

// Sets isOn to 0 and turns the leds off (all channels to 0)
void setOff() {
  isOn = 0;
  manager.setSingleColor(0, 0, 0, 0);
  sendEmptyResponse();
}

// Sets isOn to 1, but 
void setOn() {
  isOn = 1;
  if(mode == MODE_SINGLE_COLOR) {
    manager.setSingleColor(lastSingleColor[0], lastSingleColor[1], lastSingleColor[2], lastSingleColor[3]);
  }
  sendEmptyResponse();
}

// Interpolates to the given color.
void setSingleColor() {
  if(isOn) {
    mode = MODE_SINGLE_COLOR;
    lastSingleColor[0] = server.arg(F("r")).toInt() * COLOR_DEPTH_MULTIPLY;
    lastSingleColor[1] = server.arg(F("g")).toInt() * COLOR_DEPTH_MULTIPLY;
    lastSingleColor[2] = server.arg(F("b")).toInt() * COLOR_DEPTH_MULTIPLY;
#ifdef IS_RGBW
    if(server.hasArg(F("w"))) {
      lastSingleColor[3] = server.arg(F("w")).toInt() * COLOR_DEPTH_MULTIPLY;
    }
#endif
    manager.setSingleColor(lastSingleColor[0], lastSingleColor[1], lastSingleColor[2], lastSingleColor[3]);
    sendColor();
    return;
  }
  sendEmptyResponse();
}

void setWhite() {
#ifdef IS_RGBW
  if(isOn) {
    lastSingleColor[3] = server.arg(F("w")).toInt() * COLOR_DEPTH_MULTIPLY;
    manager.setSingleColor(lastSingleColor[0], lastSingleColor[1], lastSingleColor[2], lastSingleColor[3]);
    sendColor();
    return;
  }
#endif
  sendEmptyResponse();
}

// Interpolates to the given brightness.
void setBrightness() {
  if(isOn) {
    lastBrightness = server.arg(F("br")).toInt();
    manager.setBrightness(lastBrightness);
    sendBrighness();
    return;
  }
  sendEmptyResponse();
}

void setColorSequence() {
  if(isOn && server.hasArg(argBody)) {
    String body = server.arg(argBody);
    if(server.hasArg(argSpeed)) {
      int speedRaw = server.arg(argSpeed).toInt();
      manager.setSpeed(manager.convertSpeed(speedRaw));
    }
    bool success = manager.setColorSequence(&body);
    success ? sendEmptyResponse() : server.send(500, emptyJson);
    return;
  }
  sendEmptyResponse();
}

void sendEmptyResponse() {
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
  yield();
  pixel.show();
}

void loop() {
  if(WiFi.status() != WL_CONNECTED) { // If no connection, turn the info led on
    digitalWrite(INFO_LED, LOW);
  }
  else { // If connection established, handle requests
    digitalWrite(INFO_LED, HIGH);
    server.handleClient();
    yield();
  }

  manager.update();
  updatePixels();

  checkWpsButton();
}
