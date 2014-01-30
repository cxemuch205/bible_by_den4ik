package ua.maker.gbible.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.adapter.ItemDialogReadAdapter;
import ua.maker.gbible.adapter.ItemLinksWithHeadersAdapter;
import ua.maker.gbible.structs.ItemReadDay;
import ua.maker.gbible.structs.PoemStruct;
import ua.maker.gbible.utils.DataBase;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ReadForEveryDayActivity extends SherlockActivity{
	
	private static final String TAG = "ReadForEveryDayActivity";
	private static final int ACTION_SET_DEF_ITEMS_READ = 101;
	
	private ListView lvListLinks = null;
	private ItemLinksWithHeadersAdapter adapter = null;
	
	private List<ItemReadDay> listLinks = null;
	private TextView tvSectionName = null;
	private DataBase db = null;
	private AlertDialog.Builder builderDialogSetDef = null, builderItemDialog;
	private AlertDialog dialogItemRead = null;
	private View dialogView = null;
	private ListView lvDialogItems = null;
	private ItemDialogReadAdapter adapterDialog = null;
	private List<PoemStruct> listItemsReadDialog = null;
	private int posClick = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_for_every_day_layout);
		setSupportProgressBarIndeterminate(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.background_action_bar));
		lvListLinks = (ListView)findViewById(R.id.lv_links_poems);
		tvSectionName = (TextView)findViewById(R.id.tv_sect_name);
		dialogView = getLayoutInflater().inflate(R.layout.dialog_item_read_layout, null);
		lvDialogItems = (ListView)dialogView.findViewById(R.id.lv_list_links_read);
		db = new DataBase(ReadForEveryDayActivity.this);
		try {
			db.createDataBase();
		} catch (IOException e) {}
		db.openDataBase();
		
		listLinks = new ArrayList<ItemReadDay>();
		listItemsReadDialog = new ArrayList<PoemStruct>();
		
		listLinks.addAll(db.getListReadForEveryDay());
		
		adapter = new ItemLinksWithHeadersAdapter(ReadForEveryDayActivity.this, listLinks, db);
		adapterDialog = new ItemDialogReadAdapter(ReadForEveryDayActivity.this, listItemsReadDialog);
		
		lvListLinks.setAdapter(adapter);
		lvDialogItems.setAdapter(adapterDialog);
		
		lvListLinks.setOnItemClickListener(itemClickListener);
		lvListLinks.setOnScrollListener(scrollListViewListener);
		lvDialogItems.setOnItemClickListener(itemReadClickInDialogListener);
		
		builderDialogSetDef = new AlertDialog.Builder(ReadForEveryDayActivity.this);
		builderDialogSetDef.setTitle(R.string.title_dialog_set_def_val_status_read);
		builderDialogSetDef.setMessage(R.string.msg_dialog_set_def_val_status_read);
		builderDialogSetDef.setPositiveButton(R.string.dialog_ok, clickOkClearDialogListener);
		builderDialogSetDef.setNegativeButton(R.string.dialog_cancel, clickCancelClearDialogListener);
		
		builderItemDialog = new AlertDialog.Builder(ReadForEveryDayActivity.this);
		builderItemDialog.setTitle(R.string.title_select_link);
		builderItemDialog.setNegativeButton(R.string.dialog_cancel, clickCancelItemListDialogListener);
		builderItemDialog.setView(dialogView);
		dialogItemRead = builderItemDialog.create();
	}
	
	private OnItemClickListener itemReadClickInDialogListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			Toast.makeText(getApplicationContext(), "Poem: " + listItemsReadDialog.get(position).getBookName()+" "+listItemsReadDialog.get(position).getChapter(), Toast.LENGTH_SHORT).show();
		}
	};
	
	private OnClickListener clickOkClearDialogListener = new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			setSupportProgressBarIndeterminateVisibility(true);
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					db.setDefaultStatusItemRead(listLinks.size());
					setSupportProgressBarIndeterminateVisibility(false);
				}
			});
			thread.start();
			for(int i = 0; i < listLinks.size(); i++){
				listLinks.get(i).setStatus(false);
			}
			adapter.notifyDataSetChanged();
			dialog.dismiss();
		}
	};
	
	private OnClickListener clickCancelClearDialogListener = new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	};
	
	private OnClickListener clickCancelItemListDialogListener = new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	};
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			ItemReadDay item = listLinks.get(position);
			posClick = position;
			
			listItemsReadDialog.clear();
			listItemsReadDialog.addAll(item.getListPoemOld());
			listItemsReadDialog.addAll(item.getListPoemNew());
			/*for(int j = 0; j < item.getListPoemOld().size(); j++){
				if(item.getListPoemOld().get(j).getPoemTo() == 0){
					listItemsReadDialog.add(item.getListPoemOld().get(j).getBookName()+" "+item.getListPoemOld().get(j).getChapter());
				}
				else{
					listItemsReadDialog.add(item.getListPoemOld().get(j).getBookName()+" "+item.getContentChapterOldTFull());
					break;
				}				
			}
			for(int j = 0; j < item.getListPoemNew().size(); j++){
				if(item.getListPoemNew().get(j).getPoemTo() == 0){
					listItemsReadDialog.add(item.getListPoemNew().get(j).getBookName()+" "+item.getListPoemNew().get(j).getChapter());
				}
				else{
					listItemsReadDialog.add(item.getListPoemNew().get(j).getBookName()+" "+item.getContentChapterNewTFull());
					break;
				}				
			}*/
			adapterDialog.notifyDataSetChanged();
			dialogItemRead.show();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, ACTION_SET_DEF_ITEMS_READ, Menu.NONE, getString(R.string.set_def_value_status_read)).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return super.onCreateOptionsMenu(menu);
	};
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case ACTION_SET_DEF_ITEMS_READ:
			builderDialogSetDef.create().show();
			return true;
		default:
			return super.onOptionsItemSelected(item);	
		}
	}
}
