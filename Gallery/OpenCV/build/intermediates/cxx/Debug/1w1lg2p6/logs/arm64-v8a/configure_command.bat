@echo off
"C:\\Users\\ASUS\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HC:\\Users\\ASUS\\Documents\\mobile app dev\\Software-development-for-mobile-devices\\Gallery\\OpenCV\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=21" ^
  "-DANDROID_PLATFORM=android-21" ^
  "-DANDROID_ABI=arm64-v8a" ^
  "-DCMAKE_ANDROID_ARCH_ABI=arm64-v8a" ^
  "-DANDROID_NDK=C:\\Users\\ASUS\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\ASUS\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\ASUS\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\ASUS\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=C:\\Users\\ASUS\\Documents\\mobile app dev\\Software-development-for-mobile-devices\\Gallery\\OpenCV\\build\\intermediates\\cxx\\Debug\\1w1lg2p6\\obj\\arm64-v8a" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=C:\\Users\\ASUS\\Documents\\mobile app dev\\Software-development-for-mobile-devices\\Gallery\\OpenCV\\build\\intermediates\\cxx\\Debug\\1w1lg2p6\\obj\\arm64-v8a" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BC:\\Users\\ASUS\\Documents\\mobile app dev\\Software-development-for-mobile-devices\\Gallery\\OpenCV\\.cxx\\Debug\\1w1lg2p6\\arm64-v8a" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
