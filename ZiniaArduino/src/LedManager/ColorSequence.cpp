#include "LedManager.h"

#define SEQUENCE_COLOR_SHOULD_CHANGE 1
#define SEQUENCE_COLOR_SHOULD_NOT_CHANGE 0
#define SEQUENCE_COLOR_JUST_CHANGED 2

bool LedManager::setColorSequence(String *body) {
    DeserializationError error = deserializeJson(json, body->c_str());
    _numSequenceColors = json["num"];
    if(!error && _numSequenceColors > 0) {
        _mode = MODE_COLOR_SEQUENCE;
        _currentSequenceColor = 0;
        _keepSequenceColorTime = json["keep"];
        currentStateToToBuffer();
        _sequenceColorShouldChange = SEQUENCE_COLOR_SHOULD_CHANGE;
        nextSequenceColor();
        _timeStep = 0;
        return true;
    }
    return false;
}

void LedManager::nextSequenceColor() {
    if(_sequenceColorShouldChange == SEQUENCE_COLOR_SHOULD_CHANGE) {
        toBufferToFromBuffer();
        uint16_t r = ((uint16_t)json["data"][_currentSequenceColor][0]) * COLOR_DEPTH_MULTIPLY;
        uint16_t g = ((uint16_t)json["data"][_currentSequenceColor][1]) * COLOR_DEPTH_MULTIPLY;
        uint16_t b = ((uint16_t)json["data"][_currentSequenceColor][2]) * COLOR_DEPTH_MULTIPLY;
#ifdef IS_RGBW 
        uint16_t w = ((uint16_t)json["data"][_currentSequenceColor][3]) * COLOR_DEPTH_MULTIPLY;
        setColorToToBuffer(r, g, b, w);
#else
        setColorToToBuffer(r, g, b, 0);
#endif
        _currentSequenceColor ++;
        _currentSequenceColor %= _numSequenceColors;
        _sequenceColorShouldChange = SEQUENCE_COLOR_JUST_CHANGED;
    }
    else if(_sequenceColorShouldChange == SEQUENCE_COLOR_JUST_CHANGED) {
        _lastSequenceChangeMillis = millis();
        _sequenceColorShouldChange = SEQUENCE_COLOR_SHOULD_NOT_CHANGE;
        toBufferToFromBuffer();
    }
}

void LedManager::checkColorSequenceTime() {
    if(millis() - _lastSequenceChangeMillis >= _keepSequenceColorTime && 
    _sequenceColorShouldChange == SEQUENCE_COLOR_SHOULD_NOT_CHANGE) {
        _sequenceColorShouldChange = SEQUENCE_COLOR_SHOULD_CHANGE;
        nextSequenceColor();
        _timeStep = 0;
    }
}