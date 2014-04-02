package ua.maker.gbible.fragment;

import ua.maker.gbible.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockFragment;

@SuppressLint("ValidFragment")
public class UpdatesInfoFragment extends SherlockFragment {
	
	private static final String TAG = "";
	
	private View llRoot;
	private WebView wvInfo;
	
	private static UpdatesInfoFragment instance;
	
	public static UpdatesInfoFragment getInstance(){
		if(instance == null){
			instance = new UpdatesInfoFragment();
		}
		return instance;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		llRoot = inflater.inflate(R.layout.update_info_fragment_layout, null);
		wvInfo = (WebView)llRoot.findViewById(R.id.wv_update_info);
		wvInfo.getSettings().setUseWideViewPort(true);
		return llRoot;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);		
		wvInfo.loadUrl("file:///android_asset/info_updates.html");
	}

}
