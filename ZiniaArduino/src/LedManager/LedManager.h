#ifndef LedManager_h
#define LedManager_h
#include <Arduino.h>

#define MODE_SINGLE_COLOR 1

class LedManager
{
    private:
        int _numLeds;
        short** _from; // colors will be interpolated between _from and _to
        short** _to;   // based on the _timStep variable.
        float _timeStep;
        int _mode;
        float _singleColorSpeed;
        long _timestamp;
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
    public:
        LedManager(int numLeds, float singleColorSpeed);
        short getRed(int pos);
        short getGreen(int pos);
        short getBlue(int pos);
        short lerp(short from, short to, float t);
        float lerp(float from, float to, float t);
        void update(float step);
        void setSingleColor(short r, short g, short b);
        void currentStateToFromBuffer();
        void toBufferToFromBuffer();
        void setBrightness(float br);
};

#endif