package ua.maker.gbible.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.adapter.ItemListHistoryAdapter;
import ua.maker.gbible.constant.App;
import ua.maker.gbible.listeners.onDialogClickListener;
import ua.maker.gbible.structs.HistoryStruct;
import ua.maker.gbible.utils.DataBase;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

public class HistoryFragment extends SherlockFragment {
	
	private static final String TAG = "HistoryFragment";
	
	private View view = null;
	
	private ListView lvListShowHistoryItem = null;
	private TextView tvShowInfoEmpty = null;
	private LinearLayout llPaige = null;
	private DataBase dataBase = null;
	
	private ItemListHistoryAdapter adapter = null;
	private List<HistoryStruct> listHistory = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_history, null);
		lvListShowHistoryItem = (ListView)view.findViewById(R.id.lv_show_history);
		tvShowInfoEmpty = (TextView)view.findViewById(R.id.tv_info_empty_history);
		llPaige = (LinearLayout)view.findViewById(R.id.ll_history_paige);
		
		dataBase = new DataBase(getSherlockActivity());
		try {
			dataBase.createDataBase();
		} catch (IOException e) {}
		dataBase.openDataBase();
		listHistory = new ArrayList<HistoryStruct>();
		if(!getSherlockActivity().getActionBar().isShowing())
			getSherlockActivity().getActionBar().show();
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		Log.d(TAG, "onActivityCreated()");
		updateListHistory();
		getLIstHistory();
		
		lvListShowHistoryItem.setOnItemClickListener(itemClickListener);
		
	}
	
	private void getLIstHistory(){
		listHistory = dataBase.getHistory();
		updateListHistory();
	}

	private void updateListHistory() {
		Log.d(TAG, "Start - updateListHistory");
		adapter = new ItemListHistoryAdapter(getSherlockActivity(), listHistory);
		lvListShowHistoryItem.setAdapter(adapter);
		
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
			
			//SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
			//prefs.edit().putString(getString(R.string.pref_default_translaters), listHistory.get((int)id).getTranslate());
			
			FragmentTransaction ft = getFragmentManager().
					 beginTransaction();
			ft.replace(R.id.flRoot, new ListPoemsFragment(), App.TAG_FRAGMENT_POEMS);
			ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
			ft.addToBackStack(null);
			ft.commit();
		}
	};
	
	private onDialogClickListener dialogOkListener = new  onDialogClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dataBase.clearHistory();
			getLIstHistory();
			updateListHistory();
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
	   	}
	   	return super.onOptionsItemSelected(item);
	}
}
