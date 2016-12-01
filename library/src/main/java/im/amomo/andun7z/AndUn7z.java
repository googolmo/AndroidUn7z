package im.amomo.andun7z;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;

public final class AndUn7z {
    private static final String TAG = "AndUn7z";

    private static boolean sLibIsUsable = true;

    public static final int ERROR_LIBS_UNLOAD = -1;
    public static final int OK = 0;

    /**
     * Extract file
     * @param filePath file path
     * @param outPath output file path
     * @return if 0 is successful, -1 is library can't be loaded
     */
    @WorkerThread
    public static int extract7z(@NonNull String filePath, @Nullable String outPath) {
        if (!sLibIsUsable) {
            return ERROR_LIBS_UNLOAD;
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
        if (!outDir.exists() || !outDir.isDirectory()) {
            outDir.mkdirs();
        }
        return un7zip(filePath, outDir.getAbsolutePath());
    }

    /**
     * Extract 7zip file from Assets to Destination Folder
     * @param manager AssetManager;
     * @param filePath file path like 'test.z7'
     * @param outPath destination folder path
     * @return if 0 is successful, -1 is library can't be loaded
     */
    public static int extract7z(@NonNull AssetManager manager, @NonNull String filePath, @NonNull String outPath) {
        if (!sLibIsUsable) {
            return ERROR_LIBS_UNLOAD;
        }
        if (TextUtils.isEmpty(filePath)) {
            throw new IllegalArgumentException("FilePath can NOT be null");
        }
        if (manager == null) {
            throw new IllegalArgumentException("AssetManager can NOT be null");
        }
        File outDir;
        if (TextUtils.isEmpty(outPath)) {
            outDir = new File(filePath).getParentFile();
        } else {
            outDir = new File(outPath);
        }
        if (!outDir.exists() || !outDir.isDirectory()) {
            outDir.mkdirs();
        }
        return un7zipFromAsset(manager, filePath, outPath);
    }

    /**
     * Extract file from Assets
     * @param context context
     * @param assetPath asset file path
     * @param outPath output file path
     * @return if True is successful.
     */
    @Deprecated
    public static int extractAssets(Context context, String assetPath, String outPath) {
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
    @Deprecated
    public static int extractAssets(Context context, String assetPath, String outPath, String cachePath) {
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
            return 101;
        }
        if (sLibIsUsable) {
            int ret = AndUn7z.un7zip(tempFile.getAbsolutePath(), outPath);
            tempFile.delete();
            return ret;
        } else {
            return ERROR_LIBS_UNLOAD;
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
    private static native int un7zipFromAsset(AssetManager manager, String filePath, String outPath);

    static {
        try {
            System.loadLibrary("un7z");
        } catch (UnsatisfiedLinkError error) {
            sLibIsUsable = false;
            Log.d(TAG, "System.loadLibrary failed", error);
        }
    }
}
