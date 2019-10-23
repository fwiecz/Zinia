#ifndef LedManager_h
#define LedManager_h
#include <Arduino.h>
#include <ArduinoJson.h>

// Uncomment if you are using 4 channels (RGBW)
// #define IS_RGBW

// Multiplier for the color depth. The color depth received from the android app is 8 bit (256-steps).
// If you use for example 12-bit (4096 steps) set the multiplier to 16 (256 * 16 = 4096).
#define COLOR_DEPTH_MULTIPLY 1

#define MODE_SINGLE_COLOR 1
#define MODE_COLOR_SEQUENCE 2

#define JSON_SIZE 2000

class LedManager
{
    private:
        int _numLeds;
        uint16_t** _from; // colors will be interpolated between _from and _to
        uint16_t** _to;   // based on the _timStep variable.
        float _timeStep;
        int _mode;
        float _speed;
        float _singleColorSpeed;
        unsigned long _timestamp;
        float _timeAdjust;
        float _fromBrightness;
        float _toBrightness;
        float _brightness;
        float _brTimeStep;
        bool compute(float step);
        void computeBrightness();
        uint16_t getRedRaw(int pos);
        uint16_t getGreenRaw(int pos);
        uint16_t getBlueRaw(int pos);
        uint16_t getWhiteRaw(int pos);
        void nextSequenceColor();
        void currentStateToFromBuffer();
        void currentStateToToBuffer();
        void toBufferToFromBuffer();
        uint16_t lerp(uint16_t from, uint16_t to, float t);
        float lerp(float from, float to, float t);
        int _numSequenceColors;
        int _currentSequenceColor;
        int _keepSequenceColorTime;
        int _sequenceColorShouldChange;
        unsigned long _lastSequenceChangeMillis;
        StaticJsonDocument<JSON_SIZE> json;
        void setColorToToBuffer(uint16_t r, uint16_t g, uint16_t b, uint16_t w);
        void checkColorSequenceTime();
    public:
        LedManager(int numLeds, float singleColorSpeed);
        uint16_t getRed(int pos);
        uint16_t getGreen(int pos);
        uint16_t getBlue(int pos);
        uint16_t getWhite(int pos);
        void update();
        void setSingleColor(uint16_t r, uint16_t g, uint16_t b, uint16_t w);
        void setBrightness(float br);
        bool setColorSequence(String *body);
        void setSpeed(float speed);
        float convertSpeed(int raw);
};

#endif