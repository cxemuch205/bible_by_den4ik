package ua.maker.gbible.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.adapter.ItemListComparePoemsAdapter;
import ua.maker.gbible.constant.App;
import ua.maker.gbible.utils.DataBase;
import ua.maker.gbible.utils.Tools;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class ComparePoemActivity extends SherlockActivity {
	
	private TextView tvShowLink = null;
	private ListView lvCompare = null;
	private DataBase db = null;
	private List<HashMap<String, String>> listPoem = new ArrayList<HashMap<String,String>>();
	
	private int bookId = 1, chapter = 1, poem = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compare_poems);
		tvShowLink = (TextView)findViewById(R.id.tv_show_link);
		db = new DataBase(ComparePoemActivity.this);
		try {
			db.createDataBase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.openDataBase();
		bookId = getIntent().getIntExtra(App.BOOK_ID, 1);
		chapter = getIntent().getIntExtra(App.CHAPTER, 1);
		poem = getIntent().getIntExtra(App.POEM, 1);
		
		tvShowLink.setText(""
					+ Tools.getBookNameByBookId(bookId, ComparePoemActivity.this)
					+ " " + chapter + ":" + poem);
		
		listPoem = db.getPoemCompare(bookId, chapter, poem);
		
		lvCompare = (ListView)findViewById(R.id.lv_compare_poems);
		ItemListComparePoemsAdapter adapter = new ItemListComparePoemsAdapter(
				ComparePoemActivity.this, 
				R.layout.item_compare_poem, 
				listPoem);
		lvCompare.setAdapter(adapter);
	}
}
