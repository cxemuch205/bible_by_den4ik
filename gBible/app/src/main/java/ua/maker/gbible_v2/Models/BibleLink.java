package ua.maker.gbible_v2.Models;

import java.io.Serializable;

/**
 * Created by daniil on 11/7/14.
 */
public class BibleLink implements Serializable {

    public String name; //Book - name, chapter - number
    public int id; //Book - id (number in book), chapter - number
    public int bookId;
    public String info;

    public BibleLink() {
    }

    public BibleLink(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public BibleLink(String name, int id, int bookId) {
        this.name = name;
        this.id = id;
        this.bookId = bookId;
    }
}