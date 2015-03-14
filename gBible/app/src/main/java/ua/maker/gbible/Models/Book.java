package ua.maker.gbible.Models;

import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Daniil on 14-Mar-15.
 */
public class Book implements Serializable {

    public int id;
    public int chaptersCount;
    public String name;

    public static Type getTypeToken() {
        TypeToken<Book> typeToken = new TypeToken<Book>(){};
        return typeToken.getType();
    }
    public static Type getTypeTokenForArray() {
        TypeToken<ArrayList<Book>> typeToken = new TypeToken<ArrayList<Book>>(){};
        return typeToken.getType();
    }
}
