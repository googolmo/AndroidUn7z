#
# Copyright (C) The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

cmake_minimum_required(VERSION 3.4.1)

add_library(un7z
            SHARED
            src/main/cpp/andun7z.cpp
            src/main/cpp/7zMain.c
            src/main/cpp/include/7zAlloc.c
            src/main/cpp/include/7zBuf.c
            src/main/cpp/include/7zCrc.c
            src/main/cpp/include/7zCrcOpt.c
            src/main/cpp/include/7zFile.c
            src/main/cpp/include/7zDec.c
            src/main/cpp/include/7zArcIn.c
            src/main/cpp/include/7zStream.c
            src/main/cpp/include/Bcj2.c
            src/main/cpp/include/Bra.c
            src/main/cpp/include/Bra86.c
            src/main/cpp/include/CpuArch.c
            src/main/cpp/include/Delta.c
            src/main/cpp/include/Lzma2Dec.c
            src/main/cpp/include/LzmaDec.c
            src/main/cpp/include/Ppmd7.c
            src/main/cpp/include/Ppmd7Dec.c
            )

include_directories(src/main/cpp/include/)

find_library(log-lib
             log)
find_library(android-lib
             android)


target_link_libraries(un7z
                      ${android-lib}
                      ${log-lib})