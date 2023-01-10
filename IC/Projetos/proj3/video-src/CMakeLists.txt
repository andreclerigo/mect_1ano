cmake_minimum_required(VERSION 3.16)
project(opencv-example)

find_package(OpenCV REQUIRED)

SET (CMAKE_BUILD_TYPE "Release")
#SET (CMAKE_BUILD_TYPE "Debug")

SET (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra -std=c++17 -pthread")
SET (CMAKE_CXX_FLAGS_RELEASE "-O3")
SET (CMAKE_CXX_FLAGS_DEBUG "-g3 -fsanitize=address")

SET (BASE_DIR ${CMAKE_SOURCE_DIR} )
SET (CMAKE_RUNTIME_OUTPUT_DIRECTORY ${BASE_DIR}/../video-bin)

add_executable (intraframe_encoder intraframe_encoder.cpp)
target_link_libraries (intraframe_encoder ${OpenCV_LIBS})

add_executable (interframe_encoder interframe_encoder.cpp)
target_link_libraries (interframe_encoder ${OpenCV_LIBS})

add_executable (intraframe_decoder intraframe_decoder.cpp)
target_link_libraries (intraframe_decoder ${OpenCV_LIBS}) 

add_executable (interframe_decoder interframe_decoder.cpp)
target_link_libraries (interframe_decoder ${OpenCV_LIBS})  

add_executable (lossy_intra_encoder lossy_intra_encoder.cpp)
target_link_libraries (lossy_intra_encoder ${OpenCV_LIBS})

add_executable (lossy_intra_decoder lossy_intra_decoder.cpp)
target_link_libraries (lossy_intra_decoder ${OpenCV_LIBS}) 

add_executable (lossy_inter_encoder lossy_inter_encoder.cpp)
target_link_libraries (lossy_inter_encoder ${OpenCV_LIBS})

add_executable (lossy_inter_decoder lossy_inter_decoder.cpp)
target_link_libraries (lossy_inter_decoder ${OpenCV_LIBS}) 

add_executable (video_cmp video_cmp.cpp)
target_link_libraries (video_cmp ${OpenCV_LIBS})