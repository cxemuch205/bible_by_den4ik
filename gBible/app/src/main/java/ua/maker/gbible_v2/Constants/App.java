package ua.maker.gbible_v2.Constants;

/**
 * Created by daniil on 11/6/14.
 */
public class App {

    public static final int COLOR_SELECT = 0x998D6E63;

    public interface Pref {
        String NAME = "gbible_preference";
        String BOOK_ID = "book_id";
        String BOOK_NAME = "book_name";
        String CHAPTER_ID = "chapter_id";
        String POEM = "poem";
        String HOME_BIBLE_LEVEL = "home_bible_level";
        String TOP_BOOK_ID = "top_book_id";
        String COUNT_CHAPTERS = "count_chapters";
        String POEM_TEXT_SIZE = "poem_text_size";
        String DATA_BASE_VER = "data_base_bible_ver";
        String SYNC_WITH_DBX = "sync_with_dbx";
    }

    public interface BookHomeLevels {
        int BOOK = 0;
        int CHAPTER = 1;
        int POEM = 2;
    }

    public interface DeviceType{
        int PHONE = 0;
        int TABLET_7 = 1;
        int TABLET_10 = 2;
    }

    public interface DropBox {
        String API_KEY = "zjzwnaytw9d55tc";//"f45xeahjql4ntdk";
        String API_SECRET = "yde5d3o2aosg8ul";//"1lsj4rpcz26kebr";
    }

    public interface PlanDataType{
        int TEXT = 0;
        int TEXT_BOLD = 1;
        int LINK = 2;
        int LINK_WITH_TEXT = 3;
        int IMG = 4;
        int TEXT_WITH_IMG = 5;
    }

    public interface KeysConfig{
        String BOOKS = "books_list";
    }
}