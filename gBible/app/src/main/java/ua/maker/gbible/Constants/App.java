package ua.maker.gbible.Constants;

/**
 * Created by daniil on 11/6/14.
 */
public class App {

    public interface Pref {
        public static final String NAME = "gbible_preference";
        public static final String BOOK_ID = "book_id";
        public static final String BOOK_NAME = "book_name";
        public static final String CHAPTER_ID = "chapter_id";
        public static final String POEM = "poem";
        public static final String HOME_BIBLE_LEVEL = "home_bible_level";
        public static final String TOP_BOOK_ID = "top_book_id";
        public static final String COUNT_CHAPTERS = "count_chapters";
        public static final String POEM_TEXT_SIZE = "poem_text_size";
        public static final String DATA_BASE_VER = "data_base_bible_ver";
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

    public interface DropBox {
        public static final String API_KEY = "f45xeahjql4ntdk";
        public static final String API_SECRET = "1lsj4rpcz26kebr";
    }

    public interface PlanDataType{
        public static final int TEXT = 0;
        public static final int TEXT_BOLD = 1;
        public static final int LINK = 2;
        public static final int LINK_WITH_TEXT = 3;
        public static final int IMG = 4;
        public static final int TEXT_WITH_IMG = 5;
    }

    public interface KeysConfig{
        public static final String BOOKS = "books_list";
    }
}
