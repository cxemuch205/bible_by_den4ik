package ua.maker.gbible.fragment;

import ua.maker.gbible.R;
import ua.maker.gbible.activity.SettingActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class EmptyFragment extends SherlockFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.empty_fragment, null);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_main, menu);
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
}
