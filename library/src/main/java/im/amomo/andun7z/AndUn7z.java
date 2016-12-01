package im.amomo.andun7z;

import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

public final class AndUn7z {
    private static final String TAG = "AndUn7z";

    private static boolean sLibIsUsable = true;

    public static final int ERROR_LIBS_UNLOAD = -1;
    public static final int ERROR_FILE_OPEN_FAILED = 100;
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
