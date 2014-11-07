package ua.maker.gbible.Models;

import java.io.Serializable;

/**
 * Created by daniil on 11/7/14.
 */
public class Poem implements Serializable {

    public int bookId;
    public int chapterId;
    public int poemId;
    public String content;

    public Poem() {
    }

    public Poem(int bookId, int chapterId, int poemId, String content) {
        this.bookId = bookId;
        this.chapterId = chapterId;
        this.poemId = poemId;
        this.content = content;
    }
}
