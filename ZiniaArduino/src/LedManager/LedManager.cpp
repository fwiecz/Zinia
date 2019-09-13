#include "LedManager.h"

LedManager::LedManager(int numLeds)
{
    _numLeds = numLeds;
    _from = new short*[numLeds];
    _to = new short*[numLeds];
    for(int i=0; i<numLeds; i++) {
        _from[i] = new short[3];
        _to[i] = new short[3];
    }
    _timeStep = 0.0;
}

short LedManager::lerp(short from, short to, float t) {
    return ((to - from) * t) + from;
}

short LedManager::getRed(int pos) {
    return lerp(_from[pos][0], _to[pos][0], _timeStep);
}

short LedManager::getGreen(int pos) {
    return lerp(_from[pos][1], _to[pos][1], _timeStep);
}

short LedManager::getBlue(int pos) {
    return lerp(_from[pos][2], _to[pos][2], _timeStep);
}

// Returns true if timeStep >= 1
bool LedManager::update(float step) {

    _timeStep += step;
    if(_timeStep >= 1) {
        _timeStep -= 1;
        return true;
    }
    return false;
}