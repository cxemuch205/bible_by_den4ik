package ua.maker.gbible;

import ua.maker.gbible.activity.SettingActivity;
import ua.maker.gbible.constant.App;
import ua.maker.gbible.fragment.BookmarksFragment;
import ua.maker.gbible.fragment.HistoryFragment;
import ua.maker.gbible.fragment.SearchFragment;
import ua.maker.gbible.fragment.StartFragment;
import ua.maker.gbible.listeners.onDialogClickListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public abstract class SinglePanelActivity extends BaseActivity {
	
	@SuppressWarnings("unused")
	private static final String TAG = "SinglaPaneActivity";
	
	private Fragment fragment = null;
	
	private LinearLayout btnSelect = null;
	private LinearLayout btnSearch = null;
	private LinearLayout btnBookmarks = null;
	private LinearLayout btnHistory = null;
	private LinearLayout btnSettings = null;
	
	private LinearLayout llLeftPanel = null;
	private LinearLayout llRightPanel = null;
	
	private LinearLayout llBlockNavi = null;
	
	@SuppressWarnings("unused")
	private static FragmentTransaction ft = null;
	private SharedPreferences sp = null;
	private SharedPreferences spDef = null;
	
	private boolean dayNight = false;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_empty);
    	
    	sp = getSharedPreferences(App.PREF_SEND_DATA, 0);
    	spDef = PreferenceManager.getDefaultSharedPreferences(SinglePanelActivity.this);

    	Editor editor = sp.edit();
		editor.putBoolean(App.is_OPEN_SETTING, true);
		editor.commit();
		
		if (savedInstanceState == null) {
    		fragment = onCreatePane();
	    	    	
	    	getSupportFragmentManager().
	    		beginTransaction().
	    	    add(R.id.flRoot, fragment, App.TAG_FRAGMENT_BOOKS).
	    	    commit();
	    }
    	
    }
	
    public void startFragment(Fragment fragment, String tag) {
    	FragmentTransaction ft = getSupportFragmentManager().
				 beginTransaction();
		ft.replace(R.id.flRoot, fragment, tag);
		ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
		ft.addToBackStack(null);
		ft.commit();
	}
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	btnSelect = (LinearLayout)findViewById(R.id.ll_select_link);
    	btnSearch = (LinearLayout)findViewById(R.id.ll_search);
    	btnBookmarks = (LinearLayout)findViewById(R.id.ll_bookmarks);
    	btnHistory = (LinearLayout)findViewById(R.id.ll_history);
    	btnSettings = (LinearLayout)findViewById(R.id.ll_setting);
    	llBlockNavi = (LinearLayout)findViewById(R.id.ll_block_navi);
    	
    	dayNight = (spDef.getString(getString(R.string.pref_mode_read), "0").equals("0"))?false:true;
    	if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
    		if(dayNight){
    			llBlockNavi.setBackgroundColor(Color.BLACK);
    		}
    		else
    		{
    			llBlockNavi.setBackgroundColor(Color.WHITE);
    		}
    	}
    	
    	if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
    		llLeftPanel = (LinearLayout)findViewById(R.id.ll_left_control_panel);
    		llRightPanel = (LinearLayout)findViewById(R.id.ll_right_control_panel);
    		if(spDef.getString(getString(R.string.pref_pos_down_panel), "0").equals("0")){
    			llLeftPanel.setVisibility(LinearLayout.VISIBLE);
    			if(dayNight){
    				llLeftPanel.setBackgroundColor(Color.BLACK);
    			}
    			else
    			{
    				llLeftPanel.setBackgroundColor(Color.WHITE);
    			}
    			llRightPanel.setVisibility(LinearLayout.GONE);
    		}
    		else
    		{
    			llRightPanel.setVisibility(LinearLayout.VISIBLE);
    			if(dayNight){
    				llLeftPanel.setBackgroundColor(Color.BLACK);
    			}
    			else
    			{
    				llLeftPanel.setBackgroundColor(Color.WHITE);
    			}
    			llLeftPanel.setVisibility(LinearLayout.GONE);
    			
    			
    			btnSelect = (LinearLayout)findViewById(R.id.ll_select_link_right);
    	    	btnSearch = (LinearLayout)findViewById(R.id.ll_search_right);
    	    	btnBookmarks = (LinearLayout)findViewById(R.id.ll_bookmarks_right);
    	    	btnHistory = (LinearLayout)findViewById(R.id.ll_history_right);
    	    	btnSettings = (LinearLayout)findViewById(R.id.ll_setting_right);
    		}
    	}  	
    	
    	
    	btnSelect.setOnClickListener(clickSelectBookListener);    	
    	btnSearch.setOnClickListener(clickSearchFragmentListener);    	
    	btnBookmarks.setOnClickListener(clickBookmarkListener);    	
    	btnHistory.setOnClickListener(clickHistoryListener);    	
    	btnSettings.setOnClickListener(clickSettingListener);
    }
    
    private OnClickListener clickSelectBookListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Editor editor = sp.edit();
			editor.putBoolean(App.is_OPEN_SETTING, true);
			editor.commit();
			
			if(getFragmentManager()
						.findFragmentByTag(App.TAG_FRAGMENT_SETTINGS) != null)
			getFragmentManager().beginTransaction()
				.remove(getFragmentManager()
						.findFragmentByTag(App.TAG_FRAGMENT_SETTINGS)).commit();
			
			if(getSupportFragmentManager()
					.findFragmentByTag(App.TAG_FRAGMENT_CHAPTERS) != null)
				getSupportFragmentManager().beginTransaction()
							.remove(getSupportFragmentManager()
							.findFragmentByTag(App.TAG_FRAGMENT_CHAPTERS)).commit();
			
			if(getSupportFragmentManager()
					.findFragmentByTag(App.TAG_FRAGMENT_SEARCH) != null)
				getSupportFragmentManager().beginTransaction()
							.remove(getSupportFragmentManager()
							.findFragmentByTag(App.TAG_FRAGMENT_SEARCH)).commit();
			
			getSupportFragmentManager().beginTransaction()
				.replace(R.id.flRoot, (getSupportFragmentManager()
						.findFragmentByTag(App.TAG_FRAGMENT_BOOKS) != null)? 
								getSupportFragmentManager()
								.findFragmentByTag(App.TAG_FRAGMENT_BOOKS):new StartFragment(), App.TAG_FRAGMENT_BOOKS).commit();
		}
	};
	
	private OnClickListener clickSearchFragmentListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			getSupportFragmentManager().beginTransaction()
				.replace(R.id.flRoot, (getSupportFragmentManager()
						.findFragmentByTag(App.TAG_FRAGMENT_SEARCH) != null)?
								getSupportFragmentManager()
								.findFragmentByTag(App.TAG_FRAGMENT_SEARCH):new SearchFragment(), App.TAG_FRAGMENT_SEARCH).commit();
		}
	};
	
	private OnClickListener clickBookmarkListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.flRoot, (getSupportFragmentManager()
					.findFragmentByTag(App.TAG_FRAGMENT_BOOKMARKS) != null)?
							getSupportFragmentManager()
							.findFragmentByTag(App.TAG_FRAGMENT_BOOKMARKS):new BookmarksFragment(), App.TAG_FRAGMENT_BOOKMARKS).commit();
		}
	};
	
	private OnClickListener clickHistoryListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.flRoot, (getSupportFragmentManager()
					.findFragmentByTag(App.TAG_FRAGMENT_HISTORY) != null)?
							getSupportFragmentManager()
							.findFragmentByTag(App.TAG_FRAGMENT_HISTORY):new HistoryFragment(), App.TAG_FRAGMENT_HISTORY).commit();
		}
	};
	
	private OnClickListener clickSettingListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent startSetting = new Intent(SinglePanelActivity.this, SettingActivity.class);
			startActivity(startSetting);
		}
	};
    
    @Override
    public void onBackPressed() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(SinglePanelActivity.this);
    	builder.setTitle(getString(R.string.exit_question));
    	builder.setMessage(getString(R.string.closes_app));
    	builder.setPositiveButton(getString(R.string.dialog_yes), new onDialogClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
				dialog.cancel();
			}
		});
    	builder.setNegativeButton(getString(R.string.dialog_cancel), new onDialogClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
    	builder.create().show();
    }
        
    protected abstract Fragment onCreatePane();
}
