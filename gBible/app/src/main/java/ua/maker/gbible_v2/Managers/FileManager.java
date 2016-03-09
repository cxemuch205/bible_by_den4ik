package ua.maker.gbible_v2.Managers;

import android.content.Context;

import com.google.inject.Inject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by daniilpavenko on 3/9/16.
 */
public class FileManager {

    private Context context;

    @Inject
    public FileManager(Context context) {
        this.context = context;
    }

    public String getPathFromAssets(String nameFile) {
        File f = new File(context.getCacheDir() + File.separator + nameFile);
        if (!f.exists()) try {

            InputStream is = context.getAssets().open(nameFile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();


            FileOutputStream fos = new FileOutputStream(f);
            fos.write(buffer);
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return f.getPath();
    }
}
