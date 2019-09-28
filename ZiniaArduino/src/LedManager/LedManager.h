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
        short** _from; // colors will be interpolated between _from and _to
        short** _to;   // based on the _timStep variable.
        float _timeStep;
        int _mode;
        float _singleColorSpeed;
        unsigned long _timestamp;
        float _timeAdjust;
        float _fromBrightness;
        float _toBrightness;
        float _brightness;
        float _brTimeStep;
        bool compute(float step);
        void computeBrightness();
        short getRedRaw(int pos);
        short getGreenRaw(int pos);
        short getBlueRaw(int pos);
        void nextSequenceColor();
        void currentStateToFromBuffer();
        void currentStateToToBuffer();
        void toBufferToFromBuffer();
        short lerp(short from, short to, float t);
        float lerp(float from, float to, float t);
        int _numSequenceColors;
        int _currentSequenceColor;
        int _keepSequenceColorTime;
        bool _sequenceColorShouldChange;
        unsigned long _lastSequenceChangeMillis;
        StaticJsonDocument<JSON_SIZE> json;
        void setColorToToBuffer(uint16_t r, uint16_t g, uint16_t b);
        void checkColorSequenceTime();
    public:
        LedManager(int numLeds, float singleColorSpeed);
        short getRed(int pos);
        short getGreen(int pos);
        short getBlue(int pos);
        void update(float step);
        void setSingleColor(short r, short g, short b);
        void setBrightness(float br);
        bool setColorSequence(String *body);
};

#endif