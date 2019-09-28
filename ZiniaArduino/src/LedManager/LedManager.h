#ifndef LedManager_h
#define LedManager_h
#include <Arduino.h>
#include <ArduinoJson.h>

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
        void setColorToToBuffer(uint16_t r, uint16_t g, uint16_t b);
        void checkColorSequenceTime();
    public:
        LedManager(int numLeds, float singleColorSpeed);
        uint16_t getRed(int pos);
        uint16_t getGreen(int pos);
        uint16_t getBlue(int pos);
        void update();
        void setSingleColor(uint16_t r, uint16_t g, uint16_t b);
        void setBrightness(float br);
        bool setColorSequence(String *body);
        void setSpeed(float speed);
};

#endif