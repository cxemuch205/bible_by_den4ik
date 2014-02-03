package ua.maker.gbible;

import com.google.analytics.tracking.android.EasyTracker;

import ua.maker.gbible.constant.App;
import ua.maker.gbible.fragment.BookmarksFragment;
import ua.maker.gbible.fragment.HistoryFragment;
import ua.maker.gbible.fragment.PlanDetailFragment;
import ua.maker.gbible.fragment.PlansListFragment;
import ua.maker.gbible.fragment.SearchFragment;
import ua.maker.gbible.fragment.SelectBookFragment;
import ua.maker.gbible.listeners.onDialogClickListener;
import ua.maker.gbible.utils.Tools;
import ua.maker.gbible.widget.setting.ColorPickerPreference;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.GestureDetector;
import android.view.WindowManager;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public abstract class SinglePanelActivity extends BaseActivity {
	
	private static final String TAG = "SinglaPaneActivity";
	
	private Fragment fragment = null;
	private GestureDetector gestureDetector = null;
	
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
    	getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.background_action_bar));
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
    	gestureDetector = new GestureDetector(SinglePanelActivity.this, gestureListener);
    	gestureDetector.setOnDoubleTapListener(doubleTapListener);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
	
    public void startFragment(Fragment fragment, String tag) {
    	FragmentTransaction ft = getSupportFragmentManager().
				 beginTransaction();
		ft.replace(R.id.flRoot, fragment, tag);
		ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
		ft.addToBackStack(null);
		ft.commit();
	}
    
    private OnDoubleTapListener doubleTapListener = new OnDoubleTapListener() {
		
    	@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			Log.d(TAG, "onSingleTapConfirmed");
			
			return false;
		}
		
		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			Log.d(TAG, "onDoubleTapEvent");
			return false;
		}
		
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Log.d(TAG, "onDoubleTap");
			if(getActionBar().isShowing()) 
				getActionBar().hide();
			else
				getActionBar().show();
			
			return false;
		}
	};
    
    private OnGestureListener gestureListener = new OnGestureListener() {
		
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
		
		@Override
		public void onShowPress(MotionEvent e) {
			
		}
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY) {
			return false;
		}
		
		@Override
		public void onLongPress(MotionEvent e) {
			
		}
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			
			return false;
		}
		
		@Override
		public boolean onDown(MotionEvent e) {
			
			return false;
		}
	};
    
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
    			if(spDef.contains(getString(R.string.pref_backg_control_panel)))
    			{
    				String color = ""+spDef.getInt(getString(R.string.pref_backg_control_panel), getResources().getInteger(R.integer.COLOR_WHITE));
    				llBlockNavi.setBackgroundColor(Color.parseColor(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(color)))));
    			}
    			else
    			{
    				llBlockNavi.setBackgroundColor(Color.WHITE);
    			}
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
    				if(spDef.contains(getString(R.string.pref_backg_control_panel)))
        			{
        				String color = ""+spDef.getInt(getString(R.string.pref_backg_control_panel), getResources().getInteger(R.integer.COLOR_WHITE));
        				llLeftPanel.setBackgroundColor(Color.parseColor(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(color)))));
        			}
        			else
        			{
        				llLeftPanel.setBackgroundColor(Color.WHITE);
        			}    				
    			}
    			llRightPanel.setVisibility(LinearLayout.GONE);
    		}
    		else
    		{
    			llRightPanel.setVisibility(LinearLayout.VISIBLE);
    			if(dayNight){
    				llRightPanel.setBackgroundColor(Color.BLACK);
    			}
    			else
    			{
    				if(spDef.contains(getString(R.string.pref_backg_control_panel)))
        			{
        				String color = ""+spDef.getInt(getString(R.string.pref_backg_control_panel), getResources().getInteger(R.integer.COLOR_WHITE));
        				llRightPanel.setBackgroundColor(Color.parseColor(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(color)))));
        			}
        			else
        			{
        				llRightPanel.setBackgroundColor(Color.WHITE);
        			} 
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
			Tools.hideKeyBoard(SinglePanelActivity.this);
			StartSelectBook selectLinck = new StartSelectBook();
			if(getSupportFragmentManager()
					.findFragmentByTag(App.TAG_FRAGMENT_POEMS) == null
					|| 
					getSupportFragmentManager()
					.findFragmentByTag(App.TAG_FRAGMENT_POEMS).isVisible() == false)
				selectLinck.execute();				
		}
	};
	
	class StartSelectBook extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			
			Editor editor = sp.edit();
			editor.putBoolean(App.is_OPEN_SETTING, true);
			editor.commit();
			
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.flRoot, (getSupportFragmentManager()
					.findFragmentByTag(App.TAG_FRAGMENT_BOOKS) != null)?
							getSupportFragmentManager()
							.findFragmentByTag(App.TAG_FRAGMENT_BOOKS):new SelectBookFragment(), App.TAG_FRAGMENT_BOOKS).commit();
			return null;
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
			Tools.hideKeyBoard(SinglePanelActivity.this);
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
			Tools.hideKeyBoard(SinglePanelActivity.this);
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
			Tools.hideKeyBoard(SinglePanelActivity.this);
			if(sp.getInt(App.PLAN_ID, -1) != -1){
				getSupportFragmentManager().beginTransaction()
				.replace(R.id.flRoot, (getSupportFragmentManager()
						.findFragmentByTag(App.TAG_FRAGMENT_PLAN_DETAIL) != null)?
								getSupportFragmentManager()
								.findFragmentByTag(App.TAG_FRAGMENT_PLAN_DETAIL):new PlanDetailFragment(), App.TAG_FRAGMENT_PLAN_DETAIL).commit();
			}
			else
			{
				getSupportFragmentManager().beginTransaction()
				.replace(R.id.flRoot, (getSupportFragmentManager()
						.findFragmentByTag(App.TAG_FRAGMENT_PLAN) != null)?
								getSupportFragmentManager()
								.findFragmentByTag(App.TAG_FRAGMENT_PLAN):new PlansListFragment(), App.TAG_FRAGMENT_PLAN).commit();
			}
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
        
    protected abstract Fragment onCreatePane();
}
