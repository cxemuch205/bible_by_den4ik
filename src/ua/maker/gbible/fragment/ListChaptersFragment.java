package ua.maker.gbible.fragment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.activity.ReadForEveryDayActivity;
import ua.maker.gbible.activity.SettingActivity;
import ua.maker.gbible.adapter.ItemChapterAdapter;
import ua.maker.gbible.constant.App;
import ua.maker.gbible.structs.HistoryStruct;
import ua.maker.gbible.utils.DataBase;
import ua.maker.gbible.utils.Tools;
import ua.maker.gbible.utils.UserDB;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.google.analytics.tracking.android.EasyTracker;

@SuppressLint("ValidFragment")
public class ListChaptersFragment extends Fragment {
	
	private View view = null;
	private GridView gvShowChapters = null;
	private DataBase dataBase = null;
	private UserDB db = null;
	
	private int bookId = 0;
	private int lastBookId = -1;
	private String translate = "";
	
	private Button btnBook = null;
	private Button btnChapter = null;
	private Button btnPoem = null;
	
	private SharedPreferences sp = null;
	private SharedPreferences spDef = null;
	private List<Integer> listChapters;
	private LoadChaptersTask loadTask;
	private ItemChapterAdapter adapter;
	
	private ProgressDialog pd = null;
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
	
	private static ListChaptersFragment instance;
	
	private ListChaptersFragment(){};
	
	public static ListChaptersFragment getInstance(){
		if(instance == null){
			instance = new ListChaptersFragment();
		}
		return instance;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		setRetainInstance(true);
		if(db == null){
			db = new UserDB(activity);
			dataBase = new DataBase(activity);
			try {
				dataBase.createDataBase();
			} catch (IOException e) {}
			dataBase.openDataBase();

			sp = activity.getSharedPreferences(App.PREF_SEND_DATA, 0);
			spDef = PreferenceManager.getDefaultSharedPreferences(activity);
			
			listChapters = new ArrayList<Integer>();
			adapter = new ItemChapterAdapter(activity, listChapters);
			pd = new ProgressDialog(activity);
			pd.setMessage(getString(R.string.progress_dialog_message));
			pd.setCanceledOnTouchOutside(false);
		}		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_list_chapters, null);
		gvShowChapters = (GridView)view.findViewById(R.id.gv_show_chapters);
		gvShowChapters.setAdapter(adapter);
		btnBook = (Button)view.findViewById(R.id.btn_book);
		btnChapter = (Button)view.findViewById(R.id.btn_chapter);
		btnPoem = (Button)view.findViewById(R.id.btn_poem);
		if(!((ActionBarActivity)getActivity()).getSupportActionBar().isShowing())
			((ActionBarActivity)getActivity()).getSupportActionBar().show();
		updateChapters();
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.title_activity_list_chapters));
		
		translate = spDef.getString(getString(R.string.pref_default_translaters), "0");
		
		btnChapter.setBackgroundResource(R.drawable.btn_active_select);		

		bookId = sp.getInt(App.BOOK_ID, 1);
		Log.d("Getting Book_id", "id = " + bookId);
		String bookName = ""+Tools.getBookNameByBookId(bookId, getActivity());
		btnBook.setTextSize(16);
		btnBook.setText(bookName);
		btnBook.setOnClickListener(clickSelectBookListener);
		
		btnPoem.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!sp.contains(App.CHAPTER) && sp.contains(App.BOOK_ID))
					Tools.showToast(getActivity(), getString(R.string.no_select_chapter));
			}
		});
		if(listChapters.size() == 0 | lastBookId != bookId)
			updateChapters();
		else
			adapter.notifyDataSetChanged();
		
		gvShowChapters.setOnItemClickListener(listenerItemGriatView);
	}
	
	private void updateChapters(){
		if(bookId != lastBookId | listChapters.size() == 0 | lastBookId == -1){
			if(loadTask == null){
				loadTask = new LoadChaptersTask();
				loadTask.execute();
			} else {
				loadTask.cancel(false);
				loadTask = new LoadChaptersTask();
				loadTask.execute();
			}
		}
	}
	
	private class LoadChaptersTask extends AsyncTask<Void, Void, List<Integer>>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(pd != null) pd.show();
		}
		
		@Override
		protected List<Integer> doInBackground(Void... params) {
			return dataBase.getChapters(DataBase.TABLE_NAME_RST, bookId);
		}
		
		@Override
		protected void onPostExecute(List<Integer> result) {
			super.onPostExecute(result);
			lastBookId = bookId;
			listChapters.clear();
			listChapters.addAll(result);
			adapter.notifyDataSetChanged();
			if(pd.isShowing()) pd.dismiss();
		}
		
	}
	
	private OnClickListener clickSelectBookListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(getFragmentManager().findFragmentByTag(App.TAG_FRAGMENT_BOOKS) != null){
				FragmentTransaction ft = getFragmentManager().
						 beginTransaction();
				ft.remove(getFragmentManager().findFragmentByTag(App.TAG_FRAGMENT_BOOKS));
				ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
				ft.addToBackStack(null);
				ft.commit();
			}
			
			FragmentTransaction ft = getFragmentManager().
					 beginTransaction();
			ft.replace(R.id.flRoot, SelectBookFragment.getInstance(), App.TAG_FRAGMENT_BOOKS);
			ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
			ft.addToBackStack(null);
			ft.commit();
		}
	};
	
	private OnItemClickListener listenerItemGriatView = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			SharedPreferences sp = getActivity().getSharedPreferences(App.PREF_SEND_DATA, 0);
			Editor editor = sp.edit();
			editor.putInt(App.BOOK_ID, bookId);
			editor.putInt(App.CHAPTER, position+1);
			editor.putInt(App.POEM_SET_FOCUS, 0);
			editor.commit();
			
			//Write to history
			String dateCreate = dateFormat.format(new Date());
			HistoryStruct itemHistory = new HistoryStruct();
			itemHistory.setBookId(bookId);
			itemHistory.setChapter((int)(id+1));
			itemHistory.setPoem(1);
			itemHistory.setTranslate(translate);
			itemHistory.setDateCreated(dateCreate);
			
			db.insertHistory(itemHistory);
			//#######################
			
			FragmentTransaction ft = getFragmentManager().
					 beginTransaction();
			ft.replace(R.id.flRoot, ListPoemsFragment.getInstence(), App.TAG_FRAGMENT_POEMS);
			ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
			ft.addToBackStack(null);
			ft.commit();
		}
	};
	
	@Override
	public void onResume() {
		super.onResume();
		translate = spDef.getString(getString(R.string.pref_default_translaters), "0");
		boolean isReadActivity = false;
		if(sp.contains(App.IS_ITEM_READ)){
			isReadActivity = sp.getBoolean(App.IS_ITEM_READ, false);
		}
		if(isReadActivity){
			FragmentTransaction ft = getFragmentManager().
					 beginTransaction();
			ft.replace(R.id.flRoot, ListPoemsFragment.getInstence(), App.TAG_FRAGMENT_POEMS);
			ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
			ft.addToBackStack(null);
			ft.commit();
		}
	};
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_main_in_list_poem, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	   	switch(item.getItemId()){
	   	case R.id.action_exit:
	   		getActivity().finish();
	   		return true;
	   	case R.id.action_setting_app:
	   		Intent startSetting = new Intent(getActivity(), SettingActivity.class);
			startActivity(startSetting);
	   		return true;
	   	case R.id.action_read_for_every_day:
			Intent startRead = new Intent(getActivity(), ReadForEveryDayActivity.class);
			getActivity().startActivity(startRead);
			return true;
	   	}
	   	return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(getActivity());
	}
	
	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(getActivity());
	}
}
