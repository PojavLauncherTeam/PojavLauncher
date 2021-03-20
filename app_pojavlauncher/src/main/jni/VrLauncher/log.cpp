//
// Created by znix on 20/03/21.
//

#include "log.h"

void log(android_LogPriority level, const char *tag, std::string message, ...) {
    char buffer[256];
    va_list args;
    va_start (args, message);
    vsnprintf(buffer, sizeof(buffer), message.c_str(), args);
    va_end (args);

    __android_log_write(level, tag, buffer);
}
