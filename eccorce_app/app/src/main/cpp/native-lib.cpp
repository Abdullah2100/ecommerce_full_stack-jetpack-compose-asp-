#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring  JNICALL
Java_com_example_eccomerce_1app_util_Secrets_getUrlFromNdk(JNIEnv *env, jobject /* this */) {
    std::string baseUrl = "http://72.60.232.89:5077/api";
    return env->NewStringUTF(baseUrl.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_eccomerce_1app_util_Secrets_getStripKey(JNIEnv *env, jobject /* this */) {
std::string baseUrl = "pk_test_22Sm8GfHSWpeb01zOzUpuMfN9DResnK59fYyWMbB9vAJPIk6Epo7owMlmrWGTU9wJA6UifuGjEjCl1UuAp0LmJ2Yd00eQxDQmeA";
return env->NewStringUTF(baseUrl.c_str());
}
