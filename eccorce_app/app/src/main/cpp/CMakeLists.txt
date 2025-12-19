cmake_minimum_required(VERSION 3.10)

project(nativelib)

add_library(nativelib SHARED native-lib.cpp)

find_library(log-lib log)

target_link_libraries(nativelib ${log-lib})
