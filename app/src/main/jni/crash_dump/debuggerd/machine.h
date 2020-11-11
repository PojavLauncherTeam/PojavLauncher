/*
 * Copyright (C) 2011 The Android Open Source Project
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

#ifndef _DEBUGGERD_MACHINE_H
#define _DEBUGGERD_MACHINE_H

#include <sys/types.h>

#include <backtrace/Backtrace.h>

#include "utility.h"

void dump_memory_and_code(log_t* log, Backtrace* backtrace);
void dump_registers(log_t* log, pid_t tid);

#endif // _DEBUGGERD_MACHINE_H
