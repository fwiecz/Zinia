#ifndef LedManager_h
#define LedManager_h
#include <Arduino.h>

class LedManager
{
    private:
        int _numLeds;
        short** _from; // colors will be interpolated between _from and _to
        short** _to;   // based on the _timStep variable.
        float _timeStep;
    public:
        LedManager(int numLeds);
        short getRed(int pos);
        short getGreen(int pos);
        short getBlue(int pos);
        short lerp(short from, short to, float t);
        bool update(float step);
};

#endif