/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <string.h>

#include <string>
#include <unordered_map>

#include "sys/system_properties.h"

std::unordered_map<std::string, std::string> g_properties;

extern "C" int property_set(const char* name, const char* value) {
  if (g_properties.count(name) != 0) {
    g_properties.erase(name);
  }
  g_properties[name] = value;
  return 0;
}

extern "C" int property_get(const char* key, char* value, const char* default_value) {
  if (g_properties.count(key) == 0) {
    if (default_value == nullptr) {
      return 0;
    }
    strncpy(value, default_value, PROP_VALUE_MAX-1);
  } else {
    strncpy(value, g_properties[key].c_str(), PROP_VALUE_MAX-1);
  }
  value[PROP_VALUE_MAX-1] = '\0';
  return strlen(value);
}
