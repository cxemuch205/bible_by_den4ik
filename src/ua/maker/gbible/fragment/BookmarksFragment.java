package ua.maker.gbible.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.SinglePanelActivity;
import ua.maker.gbible.activity.ComparePoemActivity;
import ua.maker.gbible.activity.SettingActivity;
import ua.maker.gbible.adapter.ItemListBookmarksAdapter;
import ua.maker.gbible.constant.App;
import ua.maker.gbible.structs.BookMarksStruct;
import ua.maker.gbible.utils.DataBase;
import ua.maker.gbible.utils.Tools;
import ua.maker.gbible.utils.UserDB;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class BookmarksFragment extends SherlockFragment {
	
	private static final String TAG = "BookmarksFragment";
	
	private static final int BTN_COMPARE_POEM = 100;
	private static final int BTN_ADD_TO_PLAN = 101;
	private static final int BTN_DELETE = 102;
	
	private View view = null;
	private ListView lvBookmarks = null;
	private TextView tvInfo = null;
	private ItemListBookmarksAdapter adapter = null;
	
	private List<BookMarksStruct> listBookmarks = null;
	
	private UserDB db = null;
	private int selectItem = 0;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView()");
		view = inflater.inflate(R.layout.activity_bookmarks, null);
		lvBookmarks = (ListView)view.findViewById(R.id.lv_list_bookmarks);
		tvInfo = (TextView)view.findViewById(R.id.tv_info_bookmarks);
		listBookmarks = new ArrayList<BookMarksStruct>();
		if(!getSherlockActivity().getActionBar().isShowing())
			getSherlockActivity().getActionBar().show();
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		getSherlockActivity().getActionBar().setTitle(getString(R.string.title_activity_bookmarks));
		registerForContextMenu(lvBookmarks);
		getSherlockActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		
		db = new UserDB(getSherlockActivity());
		
		listBookmarks = db.getBookMarks();
		if(listBookmarks == null || listBookmarks.size()<1){
			tvInfo.setVisibility(TextView.VISIBLE);
			tvInfo.setText(getString(R.string.no_founded_bookmarks));
		}
		else
		{
			tvInfo.setVisibility(TextView.GONE);
		}
		
		updateListView();
		
		lvBookmarks.setOnItemClickListener(itemClickListener);
		lvBookmarks.setOnItemLongClickListener(itemLongClickListener);
	}
	
	private void updateListView(){
		adapter = new ItemListBookmarksAdapter(getSherlockActivity(), listBookmarks);
		lvBookmarks.setAdapter(adapter);
	}
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			SharedPreferences sp = getSherlockActivity().getSharedPreferences(App.PREF_SEND_DATA, 0);
			Editor editor = sp.edit();
			editor.putInt(App.BOOK_ID, listBookmarks.get((int)id).getBookId());
			editor.putInt(App.CHAPTER, listBookmarks.get((int)id).getChapter());
			editor.putInt(App.POEM_SET_FOCUS, listBookmarks.get((int)id).getPoem()-1);
			editor.commit();
			
			FragmentTransaction ft = getFragmentManager().
					 beginTransaction();
			ft.replace(R.id.flRoot, new ListPoemsFragment(), App.TAG_FRAGMENT_POEMS);
			ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
			ft.addToBackStack(null);
			ft.commit();
		}
	};
	
	private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View v,
				int position, long id) {
			selectItem = position;
			return false;
		}
	};
	
	public void onResume() {
		super.onResume();
		listBookmarks = db.getBookMarks();
		updateListView();
	};
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_main, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(Menu.NONE, BTN_COMPARE_POEM, Menu.NONE, getString(R.string.compare_str));
		menu.add(Menu.NONE, BTN_ADD_TO_PLAN, Menu.NONE, getString(R.string.add_to_plan_this_link));
		menu.add(Menu.NONE, BTN_DELETE, Menu.NONE, getString(R.string.context_delete));
	}
	    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	   	switch(item.getItemId()){
	   	case R.id.action_exit:
	   		getSherlockActivity().finish();
	   		return true;
	   	case R.id.action_setting_app:
	   		Intent startSetting = new Intent(getSherlockActivity(), SettingActivity.class);
			startActivity(startSetting);
	   		return true;
	   	}
	   	return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		
		switch (item.getItemId()) {
		case BTN_ADD_TO_PLAN:
			Log.d(TAG, "click item - add to plan");
			
			return true;
		case BTN_DELETE:
			db.deleteBookmark(listBookmarks.get(selectItem).getId());
			listBookmarks.remove(selectItem);
			updateListView();
			Tools.showToast(getSherlockActivity(), getString(R.string.deleted_bookmark));
			return true;
		case BTN_COMPARE_POEM:
			Intent intentCompare = new Intent(getSherlockActivity(), ComparePoemActivity.class);
			intentCompare.putExtra(App.BOOK_ID, listBookmarks.get(selectItem).getBookId());
			intentCompare.putExtra(App.CHAPTER, listBookmarks.get(selectItem).getChapter());
			intentCompare.putExtra(App.POEM, listBookmarks.get(selectItem).getPoem());
			startActivity(intentCompare);
			Log.d(TAG, "Click on list: pos = " + selectItem);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

}
