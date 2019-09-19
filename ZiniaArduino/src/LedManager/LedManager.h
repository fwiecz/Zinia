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
        bool compute(float step);
    public:
        LedManager(int numLeds, float singleColorSpeed);
        short getRed(int pos);
        short getGreen(int pos);
        short getBlue(int pos);
        short lerp(short from, short to, float t);
        void update(float step);
        void setSingleColor(short r, short g, short b);
        void currentStateToFromBuffer();
};

#endif