package ua.maker.gbible_v2.Models;

import java.io.Serializable;

/**
 * Created by daniil on 11/7/14.
 */
public class Poem implements Serializable {

    public int bookId;
    public int chapterId;//from 0
    public int chapter;  //from 1
    public int poem;
    public int poemTo;
    public String content;
    public String translateName;
    public String bookName;

    public Poem() {
    }

    public Poem(int bookId, int chapterId, int poem, String content) {
        this.bookId = bookId;
        this.chapterId = chapterId;
        this.poem = poem;
        this.content = content;
    }
}
