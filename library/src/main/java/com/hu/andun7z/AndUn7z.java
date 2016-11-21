package com.hu.andun7z;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

public final class AndUn7z {
    private static final String TAG = "AndUn7z";

    private static boolean sLibIsUsable = true;

    /**
     * Extract file
     * @param filePath file path
     * @param outPath output file path
     * @return if True is successful
     */
    @WorkerThread
    public static boolean extract7z(@NonNull String filePath, @Nullable String outPath) {
        if (!sLibIsUsable) {
            return false;
        }
        if (TextUtils.isEmpty(filePath)) {
            throw new IllegalArgumentException("FilePath can NOT be null");
        }
        File outDir;
        if (TextUtils.isEmpty(outPath)) {
            outDir = new File(filePath).getParentFile();
        } else {
            outDir = new File(outPath);
        }
        File parent = outDir;
        if (!parent.exists() || !parent.isDirectory()) {
            parent.mkdirs();
        }
        return AndUn7z.un7zip(filePath, outPath) == 0;
    }

    /**
     * Extract file from Assets
     * @param context context
     * @param assetPath asset file path
     * @param outPath output file path
     * @return if True is successful.
     */
    public static boolean extractAssets(Context context, String assetPath, String outPath) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = context.getCacheDir();
        }
        cacheDir = new File(cacheDir, TAG);
//        if (!cacheDir.exists() || !cacheDir.isDirectory()) {
//            cacheDir.mkdirs();
//        }

        return extractAssets(context, assetPath, outPath, cacheDir.getAbsolutePath());
    }

    /**
     * Extract file from assets
     *
     * @param context context
     * @param assetPath asset file path
     * @param outPath output file path
     * @param cachePath cache dir path
     * @return if True is successful
     */
    public static boolean extractAssets(Context context, String assetPath, String outPath, String cachePath) {
        if (context == null) {
            throw new IllegalArgumentException("context can not be null!");
        }
        if (TextUtils.isEmpty(assetPath) || TextUtils.isEmpty(outPath) || TextUtils.isEmpty(cachePath)) {
            throw new IllegalArgumentException("Arguments can not be null!");
        }
        File outDir = new File(outPath);
        if (!outDir.exists() || !outDir.isDirectory()) {
            outDir.mkdirs();
        }

        File tempFile = new File(cachePath, assetPath + ".tmp");
        File parentFile = tempFile.getParentFile();
        if (!parentFile.exists() && !parentFile.isDirectory()) {
            parentFile.mkdirs();
        }
        try {
            copyFromAssets(context, assetPath, tempFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (sLibIsUsable) {
            boolean ret = (AndUn7z.un7zip(tempFile.getAbsolutePath(), outPath) == 0);
            tempFile.delete();
            return ret;
        } else {
            return false;
        }
    }

    private static void copyFromAssets(Context context, String assetPath, String tempPath) {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            inputStream = context.getAssets().open(assetPath);
            fileOutputStream = new FileOutputStream(tempPath);
            int length;
            byte[] buffer = new byte[0x1400];
            while ((length = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, length);
            }
            fileOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //JNI interface
    private static native int un7zip(String filePath, String outPath);

    static {
        try {
            System.loadLibrary("un7z");
        } catch (UnsatisfiedLinkError error) {
            sLibIsUsable = false;
        }
    }
}
