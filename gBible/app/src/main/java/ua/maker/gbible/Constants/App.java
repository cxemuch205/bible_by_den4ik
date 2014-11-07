package ua.maker.gbible.Constants;

/**
 * Created by daniil on 11/6/14.
 */
public class App {

    public interface Pref {
        public static final String NAME = "gbible_preference";
        public static final String BOOK_ID = "book_id";
        public static final String CHAPTER_ID = "chapter_id";
        public static final String POEM = "poem";
        public static final String HOME_BIBLE_LEVEL = "home_bible_level";
        public static final String TOP_BOOK_ID = "top_book_id";
    }

    public interface BookHomeLevels {
        public static final int BOOK = 0;
        public static final int CHAPTER = 1;
        public static final int POEM = 2;
    }

    public interface DeviceType{
        public static final int PHONE = 0;
        public static final int TABLET_7 = 1;
        public static final int TABLET_10 = 2;
    }
}
