package com.hu.andun7z;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.util.Log;

public class AndUn7z {
    private static final String TAG = "AndUn7z";

    /**
     * Extract from assets
     * @param context
     * @param assetPath
     * @param outPath
     * @return
     * @throws Exception
     */
    public static boolean extractAssets(Context context, String assetPath, String outPath)
    {
        File outDir = new File(outPath);
        if(!outDir.exists() || !outDir.isDirectory())
        {
            outDir.mkdirs();
        }

        String tempPath = outPath + File.separator + assetPath + ".temp";
        try {
            copyFromAssets(context, assetPath, tempPath);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (libIsUsable) {
            boolean ret = (AndUn7z.un7zip(tempPath, outPath) == 0);
            new File(tempPath).delete();
            return ret;
        } else {
            return false;
        }
    }

    /**
     * Copy asset to temp
     * @param context
     * @param assetPath
     * @param tempPath
     * @throws Exception
     */
    private static void copyFromAssets(Context context, String assetPath, String tempPath)
            throws Exception
    {
        InputStream inputStream = context.getAssets().open(assetPath);
        FileOutputStream fileOutputStream = new FileOutputStream(tempPath);
        int length = -1;
        byte[] buffer = new byte[0x1400];
        while ((length = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, length);
        }
        fileOutputStream.flush();
        fileOutputStream.close();
        inputStream.close();
    }

    //JNI interface
    private static native int un7zip(String filePath, String outPath);

    private static boolean libIsUsable = true;
    static {
        try {
            System.loadLibrary("un7z");
        } catch (UnsatisfiedLinkError error) {
            libIsUsable = false;
        }
    }
}
