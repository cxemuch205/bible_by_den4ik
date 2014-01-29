package ua.maker.gbible.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ua.maker.gbible.BuildConfig;
import ua.maker.gbible.R;
import ua.maker.gbible.adapter.LinksWithHeadersAdapter;
import ua.maker.gbible.structs.ItemReadDay;
import ua.maker.gbible.structs.PoemStruct;
import ua.maker.gbible.utils.DataBase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class ReadForEveryDayActivity extends SherlockActivity{
	
	private static final String TAG = "ReadForEveryDayActivity";
	
	private ListView lvListLinks = null;
	private LinksWithHeadersAdapter adapter = null;
	
	private List<ItemReadDay> listLinks = null;
	private TextView tvSectionName = null;
	private DataBase db = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_for_every_day_layout);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.background_action_bar));
		lvListLinks = (ListView)findViewById(R.id.lv_links_poems);
		tvSectionName = (TextView)findViewById(R.id.tv_sect_name);
		db = new DataBase(ReadForEveryDayActivity.this);
		try {
			db.createDataBase();
		} catch (IOException e) {}
		db.openDataBase();
		
		listLinks = new ArrayList<ItemReadDay>();
		
		listLinks.addAll(db.getListReadForEveryDay());
		
		adapter = new LinksWithHeadersAdapter(ReadForEveryDayActivity.this, listLinks, db);
		lvListLinks.setAdapter(adapter);
		
		lvListLinks.setOnItemClickListener(itemClickListener);
		lvListLinks.setOnScrollListener(scrollListViewListener);
	}
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			Log.d(TAG, "Click on item: " + position);
			Toast.makeText(getApplicationContext(), "Click item: " + position, Toast.LENGTH_SHORT).show();
		}
	};
	
	private OnScrollListener scrollListViewListener = new OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			tvSectionName.setText(String.valueOf(listLinks.get(firstVisibleItem).getMonth()));
			/*int k = 0;
			if(firstVisibleItem >= 0 && firstVisibleItem < 31) k = 0;
			if(firstVisibleItem >= 31 && firstVisibleItem < 59) k = 1;
			if(firstVisibleItem >= 59 && firstVisibleItem < 90) k = 2;
			if(firstVisibleItem >= 0 && firstVisibleItem < 31) k = 3;
			if(firstVisibleItem >= 0 && firstVisibleItem < 31) k = 4;
			if(firstVisibleItem >= 0 && firstVisibleItem < 31) k = 5;
			if(firstVisibleItem >= 0 && firstVisibleItem < 31) k = 6;
			if(firstVisibleItem >= 0 && firstVisibleItem < 31) k = 7;
			if(firstVisibleItem >= 0 && firstVisibleItem < 31) k = 8;
			if(firstVisibleItem >= 0 && firstVisibleItem < 31) k = 9;
			if(firstVisibleItem >= 0 && firstVisibleItem < 31) k = 10;
			if(firstVisibleItem >= 0 && firstVisibleItem < 31) k = 11;
			
			switch (k) {
			case 0: //January
				tvSectionName.setText(R.string.month_jan);
				break;//return 31;
			case 1: //February
				tvSectionName.setText(R.string.month_feb);				
				break;//return 28;
			case 2: //March
				tvSectionName.setText(R.string.month_mar);
				break;//return 31;
			case 3: //April
				tvSectionName.setText(R.string.month_apr);
				break;//return 30;
			case 4: //May
				tvSectionName.setText(R.string.month_may);
				break;//return 31;
			case 5: //June
				tvSectionName.setText(R.string.month_jun);
				break;//return 30;
			case 6: //Jule
				tvSectionName.setText(R.string.month_jul);
				break;//return 31;
			case 7: //August
				tvSectionName.setText(R.string.month_aug);
				break;//return 31;
			case 8: //September
				tvSectionName.setText(R.string.month_sep);
				break;//return 30;
			case 9: //October
				tvSectionName.setText(R.string.month_okt);
				break;//return 31;
			case 10: //November
				tvSectionName.setText(R.string.month_nov);
				break;//return 30;
			case 11: //December
				tvSectionName.setText(R.string.month_dec);
				break;//return 31;
			default:
				break;//return 0;
			}*/
		}
	};
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);	
		}
	}
}
