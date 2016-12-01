#include <jni.h>
#include <android/log.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

#include "include/7zTypes.h"

#ifdef __cplusplus
extern "C" {
#endif

#define LOG_TAG "un7z"
#undef LOG

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,LOG_TAG,__VA_ARGS__)

int extractAssets(const AAsset* asset, const char* dstPath);
int extract7z(const char* srcFile, const char* dstPath);

/*
 * Class:     com_hu_andun7z_AndUn7z
 * Method:    un7zip
 * Signature: (Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_im_amomo_andun7z_AndUn7z_un7zip
(JNIEnv *env, jclass thiz, jstring filePath, jstring outPath)
{
    const char* cFilePath = env->GetStringUTFChars(filePath, NULL);
    const char* cOutPath = env->GetStringUTFChars(outPath, NULL);
    LOGD("start extract filePath[%s], outPath[%s]", cFilePath, cOutPath);
    jint ret = extract7z(cFilePath, cOutPath);
    LOGD("end extract");
    env->ReleaseStringUTFChars(filePath, cFilePath);
    env->ReleaseStringUTFChars(outPath, cOutPath);
    return ret;
}

JNIEXPORT jint JNICALL Java_im_amomo_andun7z_AndUn7z_un7zipFromAsset
(JNIEnv *env, jclass thiz, jobject assetManager, jstring filePath, jstring outPath)
{
    const char* cFilePath = env->GetStringUTFChars(filePath, NULL);
    const char* cOutPath = env->GetStringUTFChars(outPath, NULL);
    AAssetManager * cAAssetManager = AAssetManager_fromJava(env, assetManager);
    LOGD("start extract filePath[%s], outPath[%s]", cFilePath, cOutPath);
    AAsset* asset = AAssetManager_open(cAAssetManager, cFilePath, AASSET_MODE_STREAMING);
    jint ret;
    if (asset != NULL) {
        ret = extractAssets(asset, cOutPath);
        AAsset_close(asset);
    } else {
        ret = 100;
    }
    LOGD("end extract");
    env->ReleaseStringUTFChars(filePath, cFilePath);
    env->ReleaseStringUTFChars(outPath, cOutPath);
    return ret;
}

#ifdef __cplusplus
}
#endif
