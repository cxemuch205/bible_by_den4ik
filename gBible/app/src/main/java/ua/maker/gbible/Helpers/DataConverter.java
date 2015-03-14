package ua.maker.gbible.Helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Daniil on 14-Mar-15.
 */
public class DataConverter {

    private static Gson gson;

    public static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder().setPrettyPrinting().create();
        }
        return gson;
    }
}
