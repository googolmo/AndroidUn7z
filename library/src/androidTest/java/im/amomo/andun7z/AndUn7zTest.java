package im.amomo.andun7z;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by momo on 20/11/2016.
 */
@RunWith(AndroidJUnit4.class)
public class AndUn7zTest {

    @Test
    public void extract7z() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        File input = new File(context.getExternalCacheDir(), "test1");
        input.mkdirs();
        copyAssets(context, "test.7z", input.getAbsolutePath());
        input = new File(input, "test.7z");
        assertEquals(input.exists(), true);

        File cache = new File(context.getExternalCacheDir(), "un7z2");
        cache.mkdirs();

        int result = AndUn7z.extract7z(input.getAbsolutePath(), cache.getAbsolutePath());
        assertEquals(result, 0);
        assertEquals(cache.exists(), true);
        assertEquals(cache.list().length, 1);
        assertEquals(cache.list()[0], "temp.webp");
    }

    @Test
    public void extract7zBig() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        File input = new File(context.getExternalCacheDir(), "test1");
        input.mkdirs();
        assertEquals(input.exists(), true);
        copyAssets(context, "test2.7z", input.getAbsolutePath());
        input = new File(input, "test2.7z");
        assertEquals(input.exists(), true);

        File cache = new File(context.getExternalCacheDir(), "un7z3");
        cache.mkdirs();

        int result = AndUn7z.extract7z(input.getAbsolutePath(), cache.getAbsolutePath());
        assertEquals(result, 0);
        assertEquals(cache.exists(), true);
        assertEquals(cache.list().length, 1);
        assertEquals(cache.list()[0], "main_en");
    }

    @Test
    public void un7zFromAssets() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        File cache = new File(context.getExternalCacheDir(), "test3");
        cache.mkdirs();

        int result = AndUn7z.extract7z(context.getAssets(), "test.7z", cache.getAbsolutePath());
        assertEquals(result, 0);
        assertEquals(cache.exists(), true);
        assertEquals(cache.list().length, 1);
        assertEquals(cache.list()[0], "temp.webp");
    }

    @Test
    public void un7zFromAssetsNotExists() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        File cache = new File(context.getExternalCacheDir(), "test3");
        cache.mkdirs();

        int result = AndUn7z.extract7z(context.getAssets(), "test10.7z", cache.getAbsolutePath());
        assertEquals(result, 100);
    }

    @Test
    public void un7zFromAssetsBig() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        File cache = new File(context.getExternalCacheDir(), "test3");
        cache.mkdirs();

        int result = AndUn7z.extract7z(context.getAssets(), "test2.7z", cache.getAbsolutePath());
        assertEquals(result, 0);
        assertEquals(cache.exists(), true);
        assertEquals(cache.list().length, 1);
        assertEquals(cache.list()[0], "main_en");
    }


    @After
    public void tearDown() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        File cache = context.getExternalCacheDir();
        deleteDir(cache);
    }

    private static void copyAssets(Context context, String file, String path) {
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(file);
            File outFile = new File(path, file);
            out = new FileOutputStream(outFile);
            copyFile(in, out);
        } catch (IOException e) {
            Log.e("tag", "Failed to copy asset file: " + file, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    private static void deleteDir(File file) {
        if (file == null) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                deleteDir(f);
            }
        }
        file.delete();
    }
}
