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
    _fromBrightness = 1.0;
    _toBrightness = 1.0;
    _brightness = 1.0;
}

short LedManager::lerp(short from, short to, float t) {
    return ((to - from) * t) + from;
}

float LedManager::lerp(float from, float to, float t) {
    return ((to - from) * t) + from;
}

short LedManager::getRed(int pos) {
    return (short)(getRedRaw(pos) * _brightness);
}

short LedManager::getRedRaw(int pos) {
    return lerp(_from[pos][0], _to[pos][0], _timeStep);
}

short LedManager::getGreen(int pos) {
    return (short)(getGreenRaw(pos) * _brightness);
}

short LedManager::getGreenRaw(int pos) {
    return lerp(_from[pos][1], _to[pos][1], _timeStep);
}

short LedManager::getBlue(int pos) {
    return (short)(getBlueRaw(pos) * _brightness);
}

short LedManager::getBlueRaw(int pos) {
    return lerp(_from[pos][2], _to[pos][2], _timeStep);
}


void LedManager::currentStateToFromBuffer() {
    for(int i=0; i<_numLeds; i++) {
        _from[i][0] = getRedRaw(i);
        _from[i][1] = getGreenRaw(i);
        _from[i][2] = getBlueRaw(i);
    }
}

void LedManager::currentStateToToBuffer() {
    for(int i=0; i<_numLeds; i++) {
        _to[i][0] = getRedRaw(i);
        _to[i][1] = getGreenRaw(i);
        _to[i][2] = getBlueRaw(i);
    }
}

void LedManager::toBufferToFromBuffer() {
    for(int i=0; i<_numLeds; i++) {
        _from[i][0] = _to[i][0];
        _from[i][1] = _to[i][1];
        _from[i][2] = _to[i][2];
    }
}

void LedManager::setColorToToBuffer(uint16_t r, uint16_t g, uint16_t b) {
    for(int i=0; i<_numLeds; i++) {
        _to[i][0] = r;
        _to[i][1] = g;
        _to[i][2] = b;
    }
}

void LedManager::setSingleColor(short r, short g, short b) {
    _mode = MODE_SINGLE_COLOR;
    currentStateToFromBuffer();
    setColorToToBuffer(r, g, b);
    _timeStep = 0;
}

void LedManager::computeBrightness() {
    if(_fromBrightness != _toBrightness) {
        _brTimeStep += _singleColorSpeed / _timeAdjust;
        _brTimeStep = min(1, _brTimeStep);
        _brightness = lerp(_fromBrightness, _toBrightness, _brTimeStep);   
        if(_brTimeStep >= 1) {
            _fromBrightness = _toBrightness;
        }
    }
}

void LedManager::setBrightness(float br) {
    _fromBrightness = _brightness;
    _toBrightness = br;
    _brTimeStep = 0;
}

// Returns true if timeStep >= 1
bool LedManager::compute(float step) {
    if(_timestamp > 0) {
        unsigned long deltatime = micros() - _timestamp;
        _timeAdjust = 1000.0 / (float)deltatime;
    } else {
        _timeAdjust = 1;
    }
    _timestamp = micros();

    computeBrightness();
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
                toBufferToFromBuffer();
                _timeStep = 0;
            }
            break;
        }
        case MODE_COLOR_SEQUENCE : {
            if(newRowRequired) {
                nextSequenceColor();
            }
            checkColorSequenceTime();
            break;
        }
    }
}