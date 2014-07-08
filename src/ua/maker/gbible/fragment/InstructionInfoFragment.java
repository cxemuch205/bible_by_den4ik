package ua.maker.gbible.fragment;

import ua.maker.gbible.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

@SuppressLint("ValidFragment")
public class InstructionInfoFragment extends Fragment {
	
	private static final String TAG = "InstructionInfoFragment";
	
	private View llRoot;
	private WebView wvInstrInfo;
	
	private static InstructionInfoFragment instance;
	
	private InstructionInfoFragment(){};
	
	public static InstructionInfoFragment getInstance(){
		if(instance == null){
			instance = new InstructionInfoFragment();
		}
		return instance;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		llRoot = inflater.inflate(R.layout.instruction_info_fragment_layout, null);
		wvInstrInfo = (WebView)llRoot.findViewById(R.id.wv_instr_info);
		return llRoot;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		wvInstrInfo.loadUrl("file:///android_asset/instruction_info.htm");
	}
}
