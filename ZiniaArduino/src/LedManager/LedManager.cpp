#include "LedManager.h"

LedManager::LedManager(int numLeds, float singleColorSpeed)
{
    _numLeds = numLeds;
    _from = new short*[numLeds];
    _to = new short*[numLeds];
    _singleColorSpeed = singleColorSpeed;
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

void LedManager::currentStateToFromBuffer() {
    for(int i=0; i<_numLeds; i++) {
        _from[i][0] = getRed(i);
        _from[i][1] = getGreen(i);
        _from[i][2] = getBlue(i);
    }
}

void LedManager::setSingleColor(short r, short g, short b) {
    _mode = MODE_SINGLE_COLOR;
    currentStateToFromBuffer();
    for(int i=0; i<_numLeds; i++) {
        _to[i][0] = r;
        _to[i][1] = g;
        _to[i][2] = b;
    }
    _timeStep = 0;
}

// Returns true if timeStep >= 1
bool LedManager::compute(float step) {
    if(_timestamp > 0) {
        long deltatime = micros() - _timestamp;
        _timeAdjust = 1000.0 / (float)deltatime;
    } else {
        _timeAdjust = 1;
    }
    _timestamp = micros();

    _timeStep += (step / _timeAdjust);
    if(_timeStep >= 1) {
        _timeStep -= 1;
        _timeStep = min(_timeStep, 2);
        return true;
    }
    return false;
}

void LedManager::update(float step) {

    // colors should always change in same speed
    step = _mode == MODE_SINGLE_COLOR ? _singleColorSpeed : step;

    bool newRowRequired = compute(step);

    switch (_mode) {
        case MODE_SINGLE_COLOR : {
            if(newRowRequired) { // The target color was reached
                _timeStep = 1;
                currentStateToFromBuffer();
                _timeStep = 0;
            }
            break;
        }
    }
}