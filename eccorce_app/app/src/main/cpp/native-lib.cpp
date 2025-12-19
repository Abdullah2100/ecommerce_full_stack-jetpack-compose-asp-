#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring  JNICALL
Java_com_example_eccomerce_1app_util_Secrets_getBaseUrlFromNdk(JNIEnv *env, jobject /* this */) {
    std::string baseUrl = "http://72.60.232.89:5077/api";
    return env->NewStringUTF(baseUrl.c_str());
}
