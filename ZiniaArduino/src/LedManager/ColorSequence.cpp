#include "LedManager.h"

bool LedManager::setColorSequence(String *body) {
    DeserializationError error = deserializeJson(json, body->c_str());
    _numSequenceColors = json["num"];
    if(!error && _numSequenceColors > 0) {
        _mode = MODE_COLOR_SEQUENCE;
        _currentSequenceColor = 0;
        _keepSequenceColorTime = json["keep"];
        currentStateToToBuffer();
        _sequenceColorShouldChange = true;
        nextSequenceColor();
        _timeStep = 0;
        return true;
    }
    return false;
}

void LedManager::nextSequenceColor() {
    if(_sequenceColorShouldChange) {
        _lastSequenceChangeMillis = millis();
        toBufferToFromBuffer();
        uint16_t r = json["data"][_currentSequenceColor][0];
        uint16_t g = json["data"][_currentSequenceColor][1];
        uint16_t b = json["data"][_currentSequenceColor][2];
        setColorToToBuffer(r, g, b);
        _currentSequenceColor ++;
        _currentSequenceColor %= _numSequenceColors;
        _sequenceColorShouldChange = false;
    }
    else {
        toBufferToFromBuffer();
    }
}

void LedManager::checkColorSequenceTime() {
    if(millis() - _lastSequenceChangeMillis >= _keepSequenceColorTime) {
        _sequenceColorShouldChange = true;
        _timeStep = 0;
        nextSequenceColor();
    }
}