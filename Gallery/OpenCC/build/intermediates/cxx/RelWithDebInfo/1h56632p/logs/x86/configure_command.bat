@echo off
"C:\\Users\\ASUS\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HC:\\Users\\ASUS\\Documents\\mobile app dev\\Software-development-for-mobile-devices\\Gallery\\OpenCC\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=21" ^
  "-DANDROID_PLATFORM=android-21" ^
  "-DANDROID_ABI=x86" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86" ^
  "-DANDROID_NDK=C:\\Users\\ASUS\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\ASUS\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\ASUS\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\ASUS\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=C:\\Users\\ASUS\\Documents\\mobile app dev\\Software-development-for-mobile-devices\\Gallery\\OpenCC\\build\\intermediates\\cxx\\RelWithDebInfo\\1h56632p\\obj\\x86" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=C:\\Users\\ASUS\\Documents\\mobile app dev\\Software-development-for-mobile-devices\\Gallery\\OpenCC\\build\\intermediates\\cxx\\RelWithDebInfo\\1h56632p\\obj\\x86" ^
  "-DCMAKE_BUILD_TYPE=RelWithDebInfo" ^
  "-BC:\\Users\\ASUS\\Documents\\mobile app dev\\Software-development-for-mobile-devices\\Gallery\\OpenCC\\.cxx\\RelWithDebInfo\\1h56632p\\x86" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
