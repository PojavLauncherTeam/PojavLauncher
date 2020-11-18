/*
 * Copyright (C) 2016 The Android Open Source Project
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

#ifndef _DEBUGGERD_SIGNAL_SENDER_H
#define _DEBUGGERD_SIGNAL_SENDER_H

#include <sys/types.h>

bool start_signal_sender();
bool stop_signal_sender();

// Sends a signal to a target process or thread.
// If tid is greater than zero, this performs tgkill(pid, tid, signal).
// Otherwise, it performs kill(pid, signal).
bool send_signal(pid_t pid, pid_t tid, int signal);

#endif
