package ua.maker.gbible.activity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.adapter.ItemDialogReadAdapter;
import ua.maker.gbible.adapter.ItemLinksWithHeadersAdapter;
import ua.maker.gbible.constant.App;
import ua.maker.gbible.structs.ItemReadDay;
import ua.maker.gbible.structs.PoemStruct;
import ua.maker.gbible.utils.DataBase;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.analytics.tracking.android.EasyTracker;

public class ReadForEveryDayActivity extends SherlockActivity{
	
	private static final String TAG = "ReadForEveryDayActivity";
	private static final int ACTION_SET_DEF_ITEMS_READ = 101;
	
	private ListView lvListLinks = null;
	private ItemLinksWithHeadersAdapter adapter = null;
	
	private List<ItemReadDay> listLinks = null;
	private WeakReference<LoadDataTask> asyncTaskLoadWeakRef;
	private static SimpleDateFormat dataFormatDay = new SimpleDateFormat("D");
	private TextView tvSectionName = null;
	private DataBase db = null;
	private AlertDialog.Builder builderDialogSetDef = null, builderItemDialog;
	private AlertDialog dialogItemRead = null;
	private View dialogView = null;
	private ListView lvDialogItems = null;
	private ItemDialogReadAdapter adapterDialog = null;
	private List<PoemStruct> listItemsReadDialog = null;
	private MenuItem itemReset;
	private int posClick = 0;
	private int firstItemPosition = 0;
	private boolean loadLinks = false;
	private SharedPreferences pref = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
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
		pref = getSharedPreferences(App.PREF_SEND_DATA, 0);
		try {
			db.createDataBase();
		} catch (IOException e) {}
		db.openDataBase();
		
		listLinks = new ArrayList<ItemReadDay>();
		listItemsReadDialog = new ArrayList<PoemStruct>();
		
		startLoadData();
		
		adapter = new ItemLinksWithHeadersAdapter(ReadForEveryDayActivity.this, listLinks, db, pref, getCurrentDayNumber());
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
		setSelectedItemForRead(getCurrentDayNumber());
	}
	
	private int getCurrentDayNumber(){
		String dayS = dataFormatDay.format(new Date());
		return Integer.parseInt(dayS);
	}
	
	private void setSelectedItemForRead(int day){
		if(pref.contains(App.LAST_ITEM_SELECT)){
			posClick = pref.getInt(App.LAST_ITEM_SELECT, 0);
		}
		else
		{
			posClick = day-1;
		}
		setLastReadedItemToFocus();
	}
	
	private void startLoadData(){
		LoadDataTask task = new LoadDataTask(ReadForEveryDayActivity.this);
		this.asyncTaskLoadWeakRef = new WeakReference<ReadForEveryDayActivity.LoadDataTask>(task);
		task.execute();
	}
	
	private static class LoadDataTask extends AsyncTask<Void, Void, List<ItemReadDay>>{
		
		private WeakReference<ReadForEveryDayActivity> activityWeak;
		
		public LoadDataTask(ReadForEveryDayActivity activity){
			this.activityWeak = new WeakReference<ReadForEveryDayActivity>(activity);
			activityWeak.get().setSupportProgressBarIndeterminateVisibility(true);
		}
		
		@Override
		protected List<ItemReadDay> doInBackground(Void... params) {
			List<ItemReadDay> result;
			
			result = activityWeak.get().db.getListReadForEveryDay();
			
			return result;
		}
		
		@Override
		protected void onPostExecute(List<ItemReadDay> result) {
			super.onPostExecute(result);
			if(this.activityWeak.get() != null){
				activityWeak.get().listLinks.addAll(result);
				activityWeak.get().tvSectionName.setText(
						String.valueOf(result.get(activityWeak.get().firstItemPosition).getMonth()));
				activityWeak.get().adapter.notifyDataSetChanged();
				activityWeak.get().loadLinks = true;
				activityWeak.get().setSelectedItemForRead(
						activityWeak.get().getCurrentDayNumber());
				activityWeak.get().setSupportProgressBarIndeterminateVisibility(false);
				if(activityWeak.get().itemReset != null)
					activityWeak.get().itemReset.setVisible(true);
			}
		}		
	}
	
	private void setLastReadedItemToFocus(){
		lvListLinks.setSelection(posClick);
		lvListLinks.smoothScrollToPosition(posClick);
	}
	
	private OnItemClickListener itemReadClickInDialogListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			Editor editor = pref.edit();
			editor.putInt(App.BOOK_ID, listItemsReadDialog.get(position).getBookId());
			editor.putInt(App.CHAPTER, listItemsReadDialog.get(position).getChapter());
			editor.putInt(App.POEM_SET_FOCUS, listItemsReadDialog.get(position).getPoem()-1);
			editor.putBoolean(App.IS_ITEM_READ, true);
			editor.commit();
			finish();
		}
	};
	
	private OnClickListener clickOkClearDialogListener = new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					db.setDefaultStatusItemRead(listLinks.size());
				}
			});
			thread.start();
			for(int i = 0; i < listLinks.size(); i++){
				listLinks.get(i).setStatus(false);
			}
			adapter.notifyDataSetChanged();
			pref.edit().remove(App.LAST_ITEM_SELECT).commit();
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
			pref.edit().putInt(App.LAST_ITEM_SELECT, posClick).commit();
			
			listItemsReadDialog.clear();
			listItemsReadDialog.addAll(item.getListPoemOld());
			listItemsReadDialog.addAll(item.getListPoemNew());
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
			firstItemPosition = firstVisibleItem;
			if(loadLinks)
				tvSectionName.setText(String.valueOf(listLinks.get(firstVisibleItem).getMonth()));
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, ACTION_SET_DEF_ITEMS_READ, Menu.NONE, getString(R.string.set_def_value_status_read))
				.setIcon(android.R.drawable.ic_menu_close_clear_cancel)
				.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		itemReset = menu.findItem(ACTION_SET_DEF_ITEMS_READ);
		itemReset.setVisible(false);
		return super.onCreateOptionsMenu(menu);
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		if(lvListLinks.getFirstVisiblePosition() != posClick){
			setLastReadedItemToFocus();
		}
	}
	
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

    @Override
    protected void onStart() {
    	super.onStart();
    	EasyTracker.getInstance().activityStart(this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	EasyTracker.getInstance().activityStop(this);
    }
}
