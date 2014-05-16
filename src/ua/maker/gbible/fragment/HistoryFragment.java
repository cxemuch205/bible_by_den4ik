package ua.maker.gbible.fragment;

import java.util.ArrayList;
import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.activity.SettingActivity;
import ua.maker.gbible.adapter.ItemListHistoryAdapter;
import ua.maker.gbible.constant.App;
import ua.maker.gbible.listeners.onDialogClickListener;
import ua.maker.gbible.structs.HistoryStruct;
import ua.maker.gbible.utils.UserDB;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

@SuppressLint("ValidFragment")
public class HistoryFragment extends SherlockFragment {
	
	private static final String TAG = "HistoryFragment";
	
	private View view = null;
	
	private ListView lvListShowHistoryItem = null;
	private TextView tvShowInfoEmpty = null;
	private LinearLayout llPaige = null;
	private UserDB db = null;
	
	private ItemListHistoryAdapter adapter = null;
	private List<HistoryStruct> listHistory = null;
	
	private static HistoryFragment instance;
	
	private HistoryFragment(){};
	
	public static HistoryFragment getInstance(){
		if(instance == null){
			instance = new HistoryFragment();
		}
		return instance;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		setHasOptionsMenu(true);
		setRetainInstance(true);
		if(db == null){
			db = new UserDB(getSherlockActivity());		
			listHistory = new ArrayList<HistoryStruct>();
			adapter = new ItemListHistoryAdapter(activity, listHistory);
		}		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_history, null);
		lvListShowHistoryItem = (ListView)view.findViewById(R.id.lv_show_history);
		tvShowInfoEmpty = (TextView)view.findViewById(R.id.tv_info_empty_history);
		llPaige = (LinearLayout)view.findViewById(R.id.ll_history_paige);
		lvListShowHistoryItem.setAdapter(adapter);
		
		
		if(!getSherlockActivity().getSupportActionBar().isShowing())
			getSherlockActivity().getSupportActionBar().show();
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getSherlockActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		getSherlockActivity().getActionBar().setTitle(getString(R.string.title_activity_history));
		Log.d(TAG, "onActivityCreated()");
		updateListHistory();
		getLIstHistory();
		
		lvListShowHistoryItem.setOnItemClickListener(itemClickListener);
		
	}
	
	private void getLIstHistory(){
		listHistory.clear();
		listHistory.addAll(db.getHistory());
		updateListHistory();
	}

	private void updateListHistory() {
		Log.d(TAG, "Start - updateListHistory");
		adapter.notifyDataSetChanged();
		
		if(listHistory.size()<1){
			lvListShowHistoryItem.setVisibility(ListView.GONE);
			tvShowInfoEmpty.setVisibility(TextView.VISIBLE);
			llPaige.setGravity(Gravity.CENTER);
		}
		else
		{
			llPaige.setGravity(Gravity.TOP);
			lvListShowHistoryItem.setVisibility(ListView.VISIBLE);
			tvShowInfoEmpty.setVisibility(TextView.GONE);
		}
	}
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			SharedPreferences sp = getSherlockActivity().getSharedPreferences(App.PREF_SEND_DATA, 0);
			Editor editor = sp.edit();
			editor.putInt(App.BOOK_ID, listHistory.get((int)id).getBookId());
			editor.putInt(App.CHAPTER, listHistory.get((int)id).getChapter());
			editor.putInt(App.POEM_SET_FOCUS, 0);
			editor.commit();

			FragmentTransaction ft = getFragmentManager().
					 beginTransaction();
			ft.replace(R.id.flRoot, ListPoemsFragment.getInstence(), App.TAG_FRAGMENT_POEMS);
			ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
			ft.addToBackStack(null);
			ft.commit();
		}
	};
	
	private onDialogClickListener dialogOkListener = new  onDialogClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			db.clearHistory();
			getLIstHistory();
			dialog.cancel();
		}
	};
	
	private onDialogClickListener dialogCancelListener = new  onDialogClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.cancel();
		}
	};
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_history, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	   	switch(item.getItemId()){
	   	case R.id.action_exit:
	   		getSherlockActivity().finish();
	   		return true;
	   	case R.id.action_clear_history:
	   		AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
	   		builder.setTitle(getString(R.string.dialog_title_clear_history));
	   		builder.setMessage(getString(R.string.dialog_message_clear_history));
	   		builder.setPositiveButton(getString(R.string.dialog_yes), dialogOkListener);
	   		builder.setNegativeButton(getString(R.string.dialog_cancel), dialogCancelListener);
	   		
	   		AlertDialog dialog = builder.create();
	   		dialog.show();
	   		
	   		return true;
	   	case R.id.action_setting_app:
	   		Intent startSetting = new Intent(getSherlockActivity(), SettingActivity.class);
			startActivity(startSetting);
	   		return true;
	   	}
	   	return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(getSherlockActivity());
	}
	
	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(getSherlockActivity());
	}
}
