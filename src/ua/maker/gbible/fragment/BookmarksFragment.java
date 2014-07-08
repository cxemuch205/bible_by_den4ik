package ua.maker.gbible.fragment;

import java.util.ArrayList;
import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.activity.ComparePoemActivity;
import ua.maker.gbible.activity.SettingActivity;
import ua.maker.gbible.adapter.ItemListBookmarksAdapter;
import ua.maker.gbible.adapter.ItemPlanListAdapter;
import ua.maker.gbible.constant.App;
import ua.maker.gbible.constant.PlanData;
import ua.maker.gbible.listeners.onDialogClickListener;
import ua.maker.gbible.structs.BookMarksStruct;
import ua.maker.gbible.structs.ItemPlanStruct;
import ua.maker.gbible.structs.PlanStruct;
import ua.maker.gbible.utils.Tools;
import ua.maker.gbible.utils.UserDB;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

@SuppressLint("ValidFragment")
public class BookmarksFragment extends Fragment {
	
	private static final String TAG = "BookmarksFragment";
	
	private static final int BTN_COMPARE_POEM = 100;
	private static final int BTN_ADD_TO_PLAN = 101;
	private static final int BTN_DELETE = 102;
	private static final int BTN_COPY = 103;
	private static final int BTN_SHARE = 104;
	
	private View view = null;
	private ListView lvBookmarks = null;
	private TextView tvInfo = null;
	private TextView tvContentPoemToCopy = null;
	private ItemListBookmarksAdapter adapter = null;
	private View viewDialogCopy = null;
	private AlertDialog dialogSelect = null;
	
	private List<BookMarksStruct> listBookmarks = null;
	private List<PlanStruct> listPlans = null;
	
	private UserDB db = null;
	private int selectItem = 0;
	
	private static BookmarksFragment instance;
	
	private BookmarksFragment(){};
	
	public static BookmarksFragment getInstance(){
		if(instance == null){
			instance = new BookmarksFragment();
		}
		return instance;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		setRetainInstance(true);
		if(db == null){
			db = new UserDB(activity);
			listPlans = new ArrayList<PlanStruct>();
			listBookmarks = new ArrayList<BookMarksStruct>();
			adapter = new ItemListBookmarksAdapter(activity, listBookmarks);
			
			LayoutInflater inflater = activity.getLayoutInflater();
			viewDialogCopy = inflater.inflate(R.layout.dialog_select_poem, null);
			tvContentPoemToCopy = (TextView)viewDialogCopy.findViewById(R.id.textView_selected_poem_to_copy);
			tvContentPoemToCopy.setOnLongClickListener(longClickOnTextViewListener);
		}		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView()");
		view = inflater.inflate(R.layout.activity_bookmarks, null);
		lvBookmarks = (ListView)view.findViewById(R.id.lv_list_bookmarks);
		lvBookmarks.setAdapter(adapter);
		tvInfo = (TextView)view.findViewById(R.id.tv_info_bookmarks);
		
		if(!((ActionBarActivity)getActivity()).getSupportActionBar().isShowing())
			((ActionBarActivity)getActivity()).getSupportActionBar().show();
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.title_activity_bookmarks));
		registerForContextMenu(lvBookmarks);
		((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		
		listPlans.addAll(db.getPlansList());
		
		listBookmarks.clear();
		listBookmarks.addAll(db.getBookMarks());
		
		updateListView();
		
		lvBookmarks.setOnItemClickListener(itemClickListener);
		lvBookmarks.setOnItemLongClickListener(itemLongClickListener);
	}
	
	private void updateListView(){
		adapter.notifyDataSetChanged();
		if(listBookmarks.size() == 0){
			tvInfo.setVisibility(TextView.VISIBLE);
		} else {
			tvInfo.setVisibility(TextView.GONE);
		}
	}
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			SharedPreferences sp = getActivity().getSharedPreferences(App.PREF_SEND_DATA, 0);
			Editor editor = sp.edit();
			editor.putInt(App.BOOK_ID, listBookmarks.get((int)id).getBookId());
			editor.putInt(App.CHAPTER, listBookmarks.get((int)id).getChapter());
			editor.putInt(App.POEM_SET_FOCUS, listBookmarks.get((int)id).getPoem()-1);
			editor.commit();
			
			FragmentTransaction ft = getFragmentManager().
					 beginTransaction();
			ft.replace(R.id.flRoot, ListPoemsFragment.getInstence(), App.TAG_FRAGMENT_POEMS);
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
		listBookmarks.clear();
		listBookmarks.addAll(db.getBookMarks());
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
		menu.add(Menu.NONE, BTN_COPY, Menu.NONE, getString(R.string.dialog_copy_to_clicpboard));
		menu.add(Menu.NONE, BTN_SHARE, Menu.NONE, getString(R.string.dialog_share));
		menu.add(Menu.NONE, BTN_ADD_TO_PLAN, Menu.NONE, getString(R.string.add_to_plan_this_link));
		menu.add(Menu.NONE, BTN_DELETE, Menu.NONE, getString(R.string.context_delete));
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
	   	}
	   	return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		
		switch (item.getItemId()) {
		case BTN_ADD_TO_PLAN:
			Log.d(TAG, "click item - add to plan");
			ItemPlanListAdapter listPlanAdapter = new ItemPlanListAdapter(getActivity(), listPlans);
			if(listPlans.size()>0){
				AlertDialog.Builder builderPlanList = new AlertDialog.Builder(getActivity());
				builderPlanList.setTitle(getString(R.string.title_dialog_plans));
				builderPlanList.setSingleChoiceItems(listPlanAdapter, -1, itemDialogListPlanListener);
				builderPlanList.setNegativeButton(getString(R.string.dialog_cancel), new onDialogClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {dialog.cancel();}});
				AlertDialog dialogPlan = builderPlanList.create();
				dialogPlan.show();
			}
			else
			{
				Tools.showToast(getActivity(), getString(R.string.toast_msg_no_plans));
			}
			return true;
		case BTN_DELETE:
			db.deleteBookmark(listBookmarks.get(selectItem).getId());
			listBookmarks.remove(selectItem);
			updateListView();
			Tools.showToast(getActivity(), getString(R.string.deleted_bookmark));
			return true;
		case BTN_COMPARE_POEM:
			Intent intentCompare = new Intent(getActivity(), ComparePoemActivity.class);
			intentCompare.putExtra(App.BOOK_ID, listBookmarks.get(selectItem).getBookId());
			intentCompare.putExtra(App.CHAPTER, listBookmarks.get(selectItem).getChapter());
			intentCompare.putExtra(App.POEM, listBookmarks.get(selectItem).getPoem());
			startActivity(intentCompare);
			Log.d(TAG, "Click on list: pos = " + selectItem);
			return true;
		case BTN_COPY:
			BookMarksStruct itemS = listBookmarks.get(selectItem);
			AlertDialog.Builder builderSelect = new AlertDialog.Builder(getActivity());
			builderSelect.setTitle(getString(R.string.dialog_title_select_text));
			
			StringBuilder builderString = new StringBuilder("<p><b>"+Tools.getBookNameByBookId(itemS.getBookId(), getActivity())
	        		+" "+itemS.getChapter()+":"+itemS.getPoem()+"</b></p>"
	        		+"<p>"+itemS.getContent()+"</p>");
			
			tvContentPoemToCopy.setText(""+Html.fromHtml(builderString.toString()));
			
			builderSelect.setNegativeButton(getString(R.string.dialog_cancel), clickCancelCopyListener);
			builderSelect.setPositiveButton(getString(R.string.dialog_dtn_copy_all), clickCopyAllListener);
			builderSelect.setView(viewDialogCopy);
			
			dialogSelect = builderSelect.create();
			dialogSelect.show();
			return true;
		case BTN_SHARE:
			BookMarksStruct itemSS = listBookmarks.get(selectItem);
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/*");
			intent.putExtra(Intent.EXTRA_TEXT, Tools.getBookNameByBookId(itemSS.getBookId(), getActivity())
        		+" "+itemSS.getChapter()+":"+itemSS.getPoem()+"\n"
        		+itemSS.getContent());
			getActivity().startActivity(intent);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	private OnLongClickListener longClickOnTextViewListener = new OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			Log.d(TAG, "onLongClick()");
			tvContentPoemToCopy.setCustomSelectionActionModeCallback(new ActionModeCallback());
			return false;
		}
	};
	
	private class ActionModeCallback implements Callback {
		 
        @Override
        public boolean onCreateActionMode(android.view.ActionMode mode, android.view.Menu menu) {
        	Log.d(TAG, "onCreateActionMode()");
            return true;
        }
 
        @Override
        public boolean onPrepareActionMode(android.view.ActionMode mode, android.view.Menu menu) {
            return false;
        }
 
        @Override
        public boolean onActionItemClicked(android.view.ActionMode mode, android.view.MenuItem item) {
        	Log.d(TAG, "onActionItemClicked() - " + item.getItemId());
        	if(item.getItemId() == 16908321){
        		dialogSelect.dismiss();
        	}
        	return false;
        }
 
        @Override
        public void onDestroyActionMode(ActionMode mode) {
        	Log.d(TAG, "onDestroyActionMode()");
        }
    }
	
	private DialogInterface.OnClickListener itemDialogListPlanListener = new onDialogClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			String namePlane = "";
			ItemPlanStruct planItem = new ItemPlanStruct();
			planItem.setId(listPlans.get(which).getId());
			Log.d(TAG, "Select plan id: " + listPlans.get(which).getId());
			planItem.setDataType(PlanData.DATA_LINK_WITH_TEXT);
			planItem.setBookId(listBookmarks.get(selectItem).getBookId());
			planItem.setBookName(Tools.getBookNameByBookId(listBookmarks.get(selectItem).getBookId(), getActivity()));
			planItem.setChapter(listBookmarks.get(selectItem).getChapter());
			planItem.setPoem(listBookmarks.get(selectItem).getPoem());
			planItem.setToPoem(listBookmarks.get(selectItem).getPoem());
			planItem.setText(listBookmarks.get(selectItem).getContent());
			db.insertItemPlan(planItem);
			
			Tools.showToast(getActivity(), String.format(getString(R.string.toast_added_to_plan), namePlane));
			dialog.cancel();
		}
	};
	
	private DialogInterface.OnClickListener clickCopyAllListener = new onDialogClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			BookMarksStruct item = listBookmarks.get(selectItem);
			copyToClipBoard(Tools.getBookNameByBookId(item.getBookId(), getActivity())
	        		+" "+item.getChapter()+":"+item.getPoem()+"\n"
	        		+item.getContent());
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
			getActivity();
			android.content.ClipboardManager clipboard =  (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE); 
		        ClipData clip = ClipData.newPlainText(getString(R.string.app_name), textSetClip);
		        clipboard.setPrimaryClip(clip); 
		} else{
			getActivity();
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE); 
		    clipboard.setText(textSetClip);
		}
		 Tools.showToast(getActivity(), getString(R.string.copyed_poem));
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
