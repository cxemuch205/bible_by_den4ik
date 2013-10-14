package ua.maker.gbible.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ua.maker.gbible.R;
import ua.maker.gbible.activity.ComparePoemActivity;
import ua.maker.gbible.activity.SettingActivity;
import ua.maker.gbible.adapter.ItemDialogAdapter;
import ua.maker.gbible.adapter.ItemListPoemAdapter;
import ua.maker.gbible.constant.App;
import ua.maker.gbible.listeners.onDialogClickListener;
import ua.maker.gbible.utils.DataBase;
import ua.maker.gbible.utils.Tools;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ListPoemsFragment extends SherlockFragment{
	
	private static final String TAG = "ListPoemsFragment";
	
	private static final int CLEAR_CLICK_DELAYED = 300;
	private static final int SMOOTH_DURATION = 100;
	
	private View view = null;
	private ListView lvShowPoem = null;
	private WebView wvContent = null;
	private LinearLayout llMenuLink = null;
	private DataBase dataBase = null;
	private AlertDialog dialog = null;
	private List<String> listPoems = null;
	private Timer timer = null;
	
	private int bookId = 1;
	private int chapterNumber = 1;
	private int poemPos = 1;
	private int selectPoem = 1;
	private int maxChapter = 0;
	private int click = 0;
	
	private int posPoemForCompare = 0;
	
	private String nameTranslate = "";
	
	private Button btnBook = null;
	private Button btnChapter = null;
	private Button btnPoem = null;
	
	private SharedPreferences sp = null, defPref = null;
	private boolean dayNight = false; //false - day / true - night
	private boolean useVolBtn = false;
	private int speedScroolList = App.DEFAULT_SCROOL_SPEED;
	
	//link for bookmark
	private int bookIdBM = 1;
	private int poemBM = 1;
	private int chapterBM = 1;
	private String contentBM = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "Start onCreateView");		
		view = inflater.inflate(R.layout.activity_list_poems, null);
		lvShowPoem = (ListView)view.findViewById(R.id.lv_show_poems);
		llMenuLink = (LinearLayout)view.findViewById(R.id.menu_select_link);

		btnBook = (Button)view.findViewById(R.id.btn_book);
		btnChapter = (Button)view.findViewById(R.id.btn_chapter);
		btnPoem = (Button)view.findViewById(R.id.btn_poem);
		
		dataBase = new DataBase(getSherlockActivity());
		try {
			dataBase.createDataBase();
		} catch (IOException e) {e.printStackTrace();}
		dataBase.openDataBase();
		sp = getSherlockActivity().getSharedPreferences(App.PREF_SEND_DATA, 0);
		
		nameTranslate = getTranslateWitchPreferences();
		bookId = sp.getInt(App.BOOK_ID, 1);
		chapterNumber = sp.getInt(App.CHAPTER, 1);
		selectPoem = sp.getInt(App.POEM_SET_FOCUS, 1);
		Thread getMaxChapter = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Log.d(TAG, "Thread getMaxCountPoemInChapter");
				maxChapter = dataBase.getNumberOfChapterInBook(bookId, nameTranslate);
			}
		});
		getMaxChapter.run();
		
		Log.d(TAG, "END onCreateView");
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, "onActivityCreated()");
		setHasOptionsMenu(true);
		getSherlockActivity().getActionBar().setTitle(""
				+ Tools.getBookNameByBookId(bookId, getSherlockActivity()) + " " 
				+ chapterNumber);
		
		defPref = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
		dayNight = (defPref.getString(getString(R.string.pref_mode_read), "0").equals("0"))?false:true;
		if(dayNight)
		{
			lvShowPoem.setBackgroundColor(Color.BLACK);
			llMenuLink.setBackgroundColor(Color.BLACK);
		}
		else
		{
			lvShowPoem.setBackgroundColor(Color.WHITE);
			llMenuLink.setBackgroundColor(Color.WHITE);
		}
		
		speedScroolList = defPref.getInt(getString(R.string.pref_smooth_duration), 2);
		useVolBtn = defPref.getBoolean(getString(R.string.pref_use_vol_up_down_btn), false);
		btnPoem.setBackgroundResource(R.drawable.btn_active_select);
		btnBook.setText(Tools.getBookNameByBookId(bookId, getSherlockActivity()));
		btnChapter.setText(""+chapterNumber);
		timer = new Timer();
		
		btnBook.setOnClickListener(new OnClickListener() {
			
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
				ft.replace(R.id.flRoot, new SelectBookFragment(), App.TAG_FRAGMENT_BOOKS);
				ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
				ft.addToBackStack(null);
				ft.commit();
				
				Editor e = sp.edit();
				e.remove(App.CHAPTER);
				e.remove(App.BOOK_ID);
				e.commit();
			}
		});
		
		btnChapter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(sp.contains(App.BOOK_ID)){
					FragmentTransaction ft = getFragmentManager().
							 beginTransaction();
					ft.replace(R.id.flRoot, new ListChaptersFragment(), App.TAG_FRAGMENT_CHAPTERS);
					ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
					ft.addToBackStack(null);
					ft.commit();
				}
				else
					Tools.showToast(getSherlockActivity(), getString(R.string.no_select_book));
				
				Editor e = sp.edit();
				e.remove(App.CHAPTER);
				e.commit();
			}
		});
		
		Log.d(TAG, "Start onActivityCreated");
		listPoems = dataBase.getPoemsInChapter(bookId, chapterNumber, nameTranslate);
		
		ItemListPoemAdapter adapterListPoem = new ItemListPoemAdapter(getSherlockActivity(), R.layout.item_list_poems, listPoems);
		lvShowPoem.setAdapter(adapterListPoem);
		
		Log.d(TAG, "END onActivityCreated");
		
		lvShowPoem.setSmoothScrollbarEnabled(true);
		lvShowPoem.setOnScrollListener(scrollChangeListener);		
		lvShowPoem.setOnItemClickListener(itemClickListener);		
		lvShowPoem.setOnItemLongClickListener(itemLongClickListener);
		lvShowPoem.setOnTouchListener(touchListener);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
		String[] listActions = {
				""+getString(R.string.dialog_add_to_bookmarks),
				""+getString(R.string.dialog_copy_to_clicpboard),
				""+getString(R.string.dialog_share),
				""+getString(R.string.dialog_compare)};
		List<Integer> idPicture = new ArrayList<Integer>();
		idPicture.add(R.drawable.add_bookmark_small);
		idPicture.add(R.drawable.copy_ico_small);
		idPicture.add(R.drawable.ico_share_smal_v2);
		idPicture.add(R.drawable.ico_compare_poem);
		
		ItemDialogAdapter adapterDialog = new ItemDialogAdapter(getSherlockActivity(), listActions, idPicture);
		
		builder.setAdapter(adapterDialog, dialogItemClickListener);
		dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		
		lvShowPoem.setOnKeyListener(keyListener);
		getSherlockActivity().setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
		
	}
	
	SimpleOnGestureListener gestureListener = new SimpleOnGestureListener(){
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2,
				float velocityX, float velocityY) {
			
			float sensitvity = 120;
			   
			if((e1.getX() - e2.getX()) > sensitvity){
			    toNextChapter(); //Swipe to left
			}else if((e2.getX() - e1.getX()) > sensitvity){
			    toPreviousChapter(); //Swipe to right
			}
			   
			/*if((e1.getY() - e2.getY()) > sensitvity){
				//swipe += "Swipe Up\n";
			}else if((e2.getY() - e1.getY()) > sensitvity){
				//swipe += "Swipe Down\n";
			}else{
				//swipe += "\n";
			}*/
			
			return super.onFling(e1, e2, velocityX, velocityY);
		}
	};
	
	GestureDetector gestureDetector
    = new GestureDetector(gestureListener);
	
	private OnTouchListener touchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (gestureDetector.onTouchEvent(event))
				return true;
			else
				return false;
		}
	};
	
	private OnKeyListener keyListener = new OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if(useVolBtn){
				if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
					lvShowPoem.smoothScrollBy((speedScroolList*(-1)), SMOOTH_DURATION);
				}
				else
				if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
					lvShowPoem.smoothScrollBy(speedScroolList, SMOOTH_DURATION);
				}
			}			
			return false;
		}
	};
	
	private void selectPrefPoem(){
		Log.d(TAG, "START - select poem: " + poemPos);
		lvShowPoem.setSelection(selectPoem);
		lvShowPoem.smoothScrollToPosition(selectPoem);
	}
	
	private OnScrollListener scrollChangeListener = new OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			btnPoem.setText(""+(firstVisibleItem+1));
			poemPos = firstVisibleItem;
			
			//lvShowPoem.is
		}
	};
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			posPoemForCompare = (int)id;
			hideActionNav();
		}
	};
	
	private void hideActionNav(){
		click++;
		if(click%2 == 0){
			if(getSherlockActivity().getActionBar().isShowing()) 
				getSherlockActivity().getActionBar().hide();
			else
				getSherlockActivity().getActionBar().show();
			if(llMenuLink.getVisibility() == LinearLayout.VISIBLE) 
				llMenuLink.setVisibility(LinearLayout.GONE);
			else
				llMenuLink.setVisibility(LinearLayout.VISIBLE);
			click = 0;
		}
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				click = 0;
			}
		}, CLEAR_CLICK_DELAYED);
	}
	
	private DialogInterface.OnClickListener dialogItemClickListener = new onDialogClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0: //add to bookmarks
				dataBase.insertBookMark(getTranslateWitchPreferences(), bookIdBM, chapterBM, poemBM, contentBM);
				Tools.showToast(getSherlockActivity(), getString(R.string.dialog_added_to_bookmarks));
				dialog.cancel();
				break;
			case 1://Copy to clipboard
				AlertDialog.Builder builderSelect = new AlertDialog.Builder(getSherlockActivity());
				builderSelect.setTitle(getString(R.string.dialog_title_select_text));
				
				LayoutInflater inflater = getSherlockActivity().getLayoutInflater();
				View view = inflater.inflate(R.layout.dialog_select_poem, null);
				wvContent = (WebView)view.findViewById(R.id.wv_show_content_poem);
				wvContent.getSettings().setJavaScriptEnabled(true);
				wvContent.setBackgroundColor(0x00000000);
				
				StringBuilder builderString = new StringBuilder("<p><b>"+Tools.getBookNameByBookId(bookId, getSherlockActivity())
		        		+" "+chapterNumber+":"+poemBM+"</b></p>"
		        		+"<p>"+listPoems.get(poemBM-1)+"</p>");
				
				wvContent.loadDataWithBaseURL(null, builderString.toString(), "text/html", "utf-8", null);
				
				builderSelect.setNegativeButton(getString(R.string.dialog_cancel), clickCancelCopyListener);
				builderSelect.setPositiveButton(getString(R.string.dialog_dtn_copy_all), clickCopyAllListener);
				builderSelect.setView(view);
				
				AlertDialog dialogSelect = builderSelect.create();
				dialogSelect.show();
				
				break;
			case 2://Share
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/*");
				intent.putExtra(Intent.EXTRA_TEXT, Tools.getBookNameByBookId(bookId, getSherlockActivity())
	        		+" "+chapterNumber+":"+poemBM+"\n"
	        		+listPoems.get(poemBM-1));
				getSherlockActivity().startActivity(intent);
				
				break;
			case 3://Compare
				Intent intentCompare = new Intent(getSherlockActivity(), ComparePoemActivity.class);
				intentCompare.putExtra(App.BOOK_ID, bookId);
				intentCompare.putExtra(App.CHAPTER, chapterNumber);
				intentCompare.putExtra(App.POEM, (posPoemForCompare+1));
				startActivity(intentCompare);
				Log.d(TAG, "Click on list: pos = " + posPoemForCompare);
				
				break;
			default:
				break;
			}			
		}
	};
	
	private DialogInterface.OnClickListener clickCopyAllListener = new onDialogClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			copyToClipBoard(Tools.getBookNameByBookId(bookId, getSherlockActivity())
	        		+" "+chapterNumber+":"+poemBM+"\n"
	        		+listPoems.get(poemBM-1));
			dialog.cancel();			
		}
	};
	
	private DialogInterface.OnClickListener clickCancelCopyListener = new onDialogClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.cancel();			
		}
	};
	
	@SuppressWarnings("deprecation")
	private void copyToClipBoard(String textSetClip) {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
		     getSherlockActivity();
			android.content.ClipboardManager clipboard =  (android.content.ClipboardManager) getSherlockActivity().getSystemService(Context.CLIPBOARD_SERVICE); 
		        ClipData clip = ClipData.newPlainText("label", textSetClip);
		        clipboard.setPrimaryClip(clip); 
		} else{
		    getSherlockActivity();
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager)getSherlockActivity().getSystemService(Context.CLIPBOARD_SERVICE); 
		    clipboard.setText(textSetClip);
		}
		 Tools.showToast(getSherlockActivity(), getString(R.string.copyed_poem));
	}
	
	private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View v,
				int position, long id) {
			Log.d(TAG, "Ling click listener - pos: " + position + " id: " + id);
			poemBM = (int)id+1;
			chapterBM = chapterNumber;
			contentBM = listPoems.get((int)id);
			posPoemForCompare = (int)id;
			dialog.show();
			return false;
		}
	};
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_main_in_list_poem, menu);
		menu.setQwertyMode(true);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_exit:
			getSherlockActivity().finish();
			return true;
//		case R.id.action_next_chapter:
//			toNextChapter();
//			return true;
//		case R.id.action_previous_chapter:
//			toPreviousChapter();
//			return true;
		case R.id.action_setting_app:
	   		Intent startSetting = new Intent(getSherlockActivity(), SettingActivity.class);
			startActivity(startSetting);
	   		return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void toPreviousChapter() {
		chapterNumber--;
		if(/*dataBase.getPoemsInChapter(bookId, chapterNumber, nameTranslate) != null 
				&& */chapterNumber > 0 && chapterNumber <= maxChapter){
			chengeChapter(chapterNumber);
		}
		else
		{
			chapterNumber++;
		}
	}

	private void toNextChapter() {
		chapterNumber++;
		if(/*dataBase.getPoemsInChapter(bookId, chapterNumber, nameTranslate) != null 
				&& */chapterNumber > 0 && chapterNumber <= maxChapter){
			chengeChapter(chapterNumber);
		}
		else
		{
			chapterNumber--;
			Tools.showToast(getSherlockActivity(), getString(R.string.it_is_chapter_last));
			//In future? may make switch on next book
		}
	}

	public void chengeChapter(int count){
		ChangeChapterAsyncTask changechapterTask = new ChangeChapterAsyncTask();
		changechapterTask.execute();
	}
	
	class ChangeChapterAsyncTask extends AsyncTask<Void, Void, Void>{

		private ProgressDialog pd = null;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(getSherlockActivity(), 
					getString(R.string.progress_dialog_title), 
					getString(R.string.progress_dialog_download_chapter));
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			Log.d(TAG, "ChangeChapterAsyncTask()");
			listPoems = dataBase.getPoemsInChapter(bookId, chapterNumber, nameTranslate);
			sp = getSherlockActivity().getSharedPreferences(App.PREF_SEND_DATA, 0);
			Editor editor = sp.edit();
			editor.putInt(App.BOOK_ID, bookId);
			editor.putInt(App.CHAPTER, chapterNumber);
			editor.commit();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Log.d(TAG, "ChangeChapterAsyncTask() - END");
			if(pd.isShowing()) pd.cancel();
			getSherlockActivity().getActionBar().setTitle(""+
					Tools.getBookNameByBookId(bookId, getSherlockActivity()) + " " + chapterNumber);
			btnChapter.setText(""+chapterNumber);
			ItemListPoemAdapter adapterListPoem = new ItemListPoemAdapter(getSherlockActivity(), R.layout.item_list_poems, listPoems);
			lvShowPoem.setAdapter(adapterListPoem);
		}		
	};
	
	@Override
	public void onResume() {
		super.onResume();
		List<String> listPoems = dataBase.getPoemsInChapter(bookId, chapterNumber, getTranslateWitchPreferences());
		
		ItemListPoemAdapter adapterListPoem = new ItemListPoemAdapter(getSherlockActivity(), R.layout.item_list_poems, listPoems);
		lvShowPoem.setAdapter(adapterListPoem);
		
		dayNight = (defPref.getString(getString(R.string.pref_mode_read), "0").equals("0"))?false:true;
		if(dayNight){
			lvShowPoem.setBackgroundColor(Color.BLACK);
			llMenuLink.setBackgroundColor(Color.BLACK);
		}
		else
		{
			lvShowPoem.setBackgroundColor(Color.WHITE);
			llMenuLink.setBackgroundColor(Color.WHITE);
		}
		
		selectPrefPoem();
		useVolBtn = defPref.getBoolean(getString(R.string.pref_use_vol_up_down_btn), false);
		speedScroolList = defPref.getInt(getString(R.string.pref_smooth_duration), 2);
		
		Log.d(TAG, "OnResume TESTING");
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		Editor e = sp.edit();
		e.putInt(App.POEM_SET_FOCUS, poemPos);
		e.commit();
	}
	
	public String getTranslateWitchPreferences(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
		String translateName = DataBase.TABLE_NAME_RST;
		if(prefs.contains(getString(R.string.pref_default_translaters))){
			
			switch(Integer.parseInt(prefs.getString(getString(R.string.pref_default_translaters), "0"))){
				case 0:
					translateName = DataBase.TABLE_NAME_RST;
					break;
				case 1:
					translateName = DataBase.TABLE_NAME_MT;
					break;
			}
		}
		return translateName;
	}
}
