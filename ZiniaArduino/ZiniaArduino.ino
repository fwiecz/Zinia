#include <Adafruit_NeoPixel.h>
#include <WiFiClient.h>
#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include "src/LedManager/LedManager.h"

// How many LEDs are connected?
#define NUM_LEDS 3

// The data output pin for the LEDs 
#define LED_DATA 5 // 5 is D1 on WeMos D1 mini

// The button used for WPS connection. Use a pin with internal pullup resistor!
#define WPS_BUTTON_PIN 0 // 0 is D3 on WeMos D1 mini

// The LED used to display technical information
#define INFO_LED 2 // Built-in LED on WeMos D1 mini

// When the LEDs are set to a single color, this speed is used instead of [colorSpeed].
const float singleColorSpeed = 0.001;

// The SSID (name) and password for your Wifi
const char* ssid = "";
const char* password = "";

// The NeoPixel object is used to communicate with the LEDs.
Adafruit_NeoPixel pixel = Adafruit_NeoPixel(NUM_LEDS, LED_DATA, NEO_GRB + NEO_KHZ800);

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

// How fast colors should change
float colorSpeed = 0.01f;

// Positions: numLights: 12-15, isOn: 25
char statusMsg[] = "{\"numLeds\":\"    \",\"isOn\": }";

void setup() {
  pinMode(LED_DATA, OUTPUT);
  pinMode(INFO_LED, OUTPUT);
  pinMode(WPS_BUTTON_PIN, INPUT_PULLUP);

  // Whenever the WPS button is pressed, the WPS subroutine starts
  attachInterrupt(digitalPinToInterrupt(WPS_BUTTON_PIN), wpsSetUp, FALLING);

  // Communicate to the computer
  Serial.begin(115200);

  // LOW means ON! While the chip tries to connect, the led will be on.
  digitalWrite(INFO_LED, LOW);
  setAllPixelsTo(0, 0, 0);

  // If no credentials given
  if(strcmp(ssid, empty) == 0 || strcmp(password, empty) == 0) {
    // TODO read credentials from EEPROM
  }
  else { // Connect to the given credentials
    WiFi.begin(ssid, password);
    while(WiFi.status() != WL_CONNECTED) {
      delay(500);
    }
  }

  initializeServer();
}

// The Status Message containing software/hardware information.
void sendStatus() {
  statusMsg[12] = ascii((NUM_LEDS / 1000) % 10);
  statusMsg[13] = ascii((NUM_LEDS / 100) % 10);
  statusMsg[14] = ascii((NUM_LEDS / 10) % 10);
  statusMsg[15] = ascii(NUM_LEDS % 10);
  statusMsg[25] = ascii(isOn);
  server.send(200, applicationJson, statusMsg);
}

// Initializes REST calls
void initializeServer() {
  server.on("/", sendStatus);
  server.on("/setSingleColor", setSingleColor);
  server.on("/setOn", setOn);
  server.on("/setOff", setOff);
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
  server.send(200, applicationJson, emptyJson);
}

// Interpolates to the given color. [colorSpeed] has no effect here.
void setSingleColor() {
  if(isOn) {
    short r = server.arg(F("r")).toInt();
    short g = server.arg(F("g")).toInt();
    short b = server.arg(F("b")).toInt();
    manager.setSingleColor(r, g, b);
  }
  server.send(200, applicationJson, emptyJson);
}

void wpsSetUp() {
  // TODO actual wps setup
}

// Sets all pixels to the given color.
void setAllPixelsTo(int r, int g, int b) {
  for(int i=0; i<NUM_LEDS; i++) {
    pixel.setPixelColor(0, r, g, b);
  }
  pixel.show();
}

// Returns the given number as ascii digit (0-9).
int ascii(int num) {
  return num + 48;
}

void updatePixels() {
  for(int i=0; i<NUM_LEDS; i++) {
    pixel.setPixelColor(i, manager.getRed(i), manager.getGreen(i), manager.getBlue(i));
  }
  pixel.show();
}

void loop() {
  if(WiFi.status() != WL_CONNECTED) { // If no connection, turn the info led on
    digitalWrite(INFO_LED, LOW);
    delay(500);
  }
  else { // If connection established, handle requests
    digitalWrite(INFO_LED, HIGH);
    server.handleClient();
  }

  manager.update(colorSpeed);
  updatePixels();
}
