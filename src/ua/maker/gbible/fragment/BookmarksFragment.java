package ua.maker.gbible.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.adapter.ItemListBookmarksAdapter;
import ua.maker.gbible.constant.App;
import ua.maker.gbible.structs.BookMarksStruct;
import ua.maker.gbible.utils.DataBase;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
	
	private View view = null;
	private ListView lvBookmarks = null;
	private TextView tvInfo = null;
	private ItemListBookmarksAdapter adapter = null;
	
	private List<BookMarksStruct> listBookmarks = null;
	
	private DataBase db = null;
	
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
		db = new DataBase(getSherlockActivity());
		
		try {
			db.createDataBase();
		} catch (IOException e) {}
		db.openDataBase();
		Log.d(TAG, "DB opened");
		
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
	public boolean onOptionsItemSelected(MenuItem item) {
	   	switch(item.getItemId()){
	   	case R.id.action_exit:
	   		getSherlockActivity().finish();
	   		return true;
	   	}
	   	return super.onOptionsItemSelected(item);
	}

}
