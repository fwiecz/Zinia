#include <Adafruit_NeoPixel.h>
#include <WiFiClient.h>
#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>

// How many LEDs are connected?
#define NUM_LEDS 16

// The data output pin for the LEDs 
#define LED_DATA 5 // 5 is D1 on WeMos D1 mini

// The button used for WPS connection. Use a pin with internal pullup resistor!
#define WPS_BUTTON_PIN 0 // 0 is D3 on WeMos D1 mini

// The LED used to display technical information
#define INFO_LED 2 // Built-in LED on WeMos D1 mini

// The SSID (name) and password for your Wifi
const char* ssid = "";
const char* password = "";

// The NeoPixel object is used to communicate with the LEDs.
Adafruit_NeoPixel pixel = Adafruit_NeoPixel(NUM_LEDS, LED_DATA, NEO_GRB + NEO_KHZ800);

// The server object is responsible for handling incoming http requests.
ESP8266WebServer server(80);

const char* empty = "";
const char* textPlain = "text/plain";
const char* applicationJson = "application/json";

// Whether the leds should be on or not
int isOn = 1;

// Positions: numLights: 12-15, isOn: 25
char* statusMsg = "{\"numLeds\":\"    \",\"isOn\": }";

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
  server.onNotFound( [](){
    server.send(404, textPlain, F("Page not found"));
  });
  server.begin();
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

void loop() {
  if(WiFi.status() != WL_CONNECTED) { // If no connection, turn the info led on
    digitalWrite(INFO_LED, LOW);
    delay(500);
  }
  else { // If connection established, handle requests
    digitalWrite(INFO_LED, HIGH);
    server.handleClient();
  }
}
