package ua.maker.gbible.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.activity.SettingActivity;
import ua.maker.gbible.adapter.ItemListSearchAdapter;
import ua.maker.gbible.constant.App;
import ua.maker.gbible.structs.SearchStruct;
import ua.maker.gbible.utils.DataBase;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.analytics.tracking.android.EasyTracker;

@SuppressLint("ValidFragment")
public class SearchFragment extends Fragment {

	private static final String TAG = "SearchFragment";
	
	private View view = null;
	
	private EditText etSearchText = null;
	private Button btnStartSearch = null;
	private Spinner spinnerBookFrom = null;
	private Spinner spinnerBookTo = null;
	private ListView lvResultSearch = null;
	private ItemListSearchAdapter adapter = null;
	private List<SearchStruct> searchResult = null;
	
	private DataBase db = null;
	
	private int idBookStart = 1;
	private int idBookEnd = 66;
	
	private String requestDB = "";
	private SearchTast searchTask = null;
	
	private SharedPreferences pref = null;
	
	private static SearchFragment instance;
	
	private SearchFragment(){};
	
	public static SearchFragment getInstance(){
		if(instance == null){
			instance = new SearchFragment();
		}
		return instance;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		setRetainInstance(true);
		if(db == null){
			pref = getActivity().getSharedPreferences(App.PREF_SEND_DATA, 0);
			db = new DataBase(getActivity());
			try {
				db.createDataBase();
			} catch (IOException e) {e.printStackTrace();}
			
			db.openDataBase();
			searchResult = new ArrayList<SearchStruct>();
			adapter = new ItemListSearchAdapter(getActivity(), searchResult);
		}		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_search, null);
		
		etSearchText = (EditText)view.findViewById(R.id.et_search_text);
		btnStartSearch = (Button)view.findViewById(R.id.btn_start_search);
		spinnerBookFrom = (Spinner)view.findViewById(R.id.spinner_book_from);
		spinnerBookTo = (Spinner)view.findViewById(R.id.spinner_book_to);
		lvResultSearch = (ListView)view.findViewById(R.id.lv_search_result);
		lvResultSearch.setAdapter(adapter);
		
		if(!((ActionBarActivity)getActivity()).getSupportActionBar().isShowing())
			((ActionBarActivity)getActivity()).getSupportActionBar().show();
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.search_str));
		
		if(pref.contains(App.SEARCH_REQUEST)){
			requestDB = pref.getString(App.SEARCH_REQUEST, "");
			etSearchText.setHint(requestDB);
		}
		if(searchResult.size() == 0){
			searchResult.clear();
			searchResult.addAll(db.getSearchResult());
			updateListResult();
		} else
			updateListResult();
		
		lvResultSearch.setOnItemClickListener(itemResultSearchListener);
		spinnerBookFrom.setOnItemSelectedListener(itemSelectedBookFromListener);
		spinnerBookTo.setOnItemSelectedListener(itemSelectedBookToListener);
		spinnerBookTo.setSelection(65);
		btnStartSearch.setOnClickListener(clickSearchListener);
		etSearchText.addTextChangedListener(changeTextListener);
		etSearchText.setOnKeyListener(keyEnterClickLIstener);
	}
	
	private void updateListResult() {
		adapter.notifyDataSetChanged();
	}
	
	private OnKeyListener keyEnterClickLIstener = new OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if(keyCode == KeyEvent.KEYCODE_ENTER
					&& event.getAction()==KeyEvent.ACTION_UP){
				clickSearchListener.onClick(v);
			}
			return false;
		}
	};
	
	private OnItemClickListener itemResultSearchListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			Editor editor = pref.edit();
			editor.putInt(App.BOOK_ID, searchResult.get((int)id).getIdBook());
			editor.putInt(App.CHAPTER, searchResult.get((int)id).getChapter());
			editor.putInt(App.POEM_SET_FOCUS, searchResult.get((int)id).getPoem()-1);
			editor.putBoolean(App.is_SEARCH_REQUEST, true);
			editor.commit();
			
			FragmentTransaction ft = getFragmentManager().
					 beginTransaction();
			ft.replace(R.id.flRoot, ListPoemsFragment.getInstence(), App.TAG_FRAGMENT_POEMS);
			ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
			ft.addToBackStack(null);
			ft.commit();
		}
	};

	private OnClickListener clickSearchListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(!etSearchText.getText().toString().equals("") || requestDB != ""){				
				//hide keyboard
				getActivity();
				InputMethodManager imm = (InputMethodManager)getActivity()
						.getSystemService(FragmentActivity.INPUT_METHOD_SERVICE); 
				imm.hideSoftInputFromWindow(etSearchText.getWindowToken(), 0);
				
				pref.edit().putString(App.SEARCH_REQUEST, requestDB).commit();
				db.clearSearchResultHistory();
				if(searchResult.size()>0)
					searchResult.clear();
				
				if(etSearchText.getText().toString().equals(""))
					startSearch(requestDB);
				else
					startSearch(etSearchText.getText().toString());
				
				updateListResult();
			}
			else
				etSearchText.setError(getString(R.string.empty_et));
		}
	};
	
	private TextWatcher changeTextListener = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			etSearchText.setError(null);
		}		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {}		
		@Override
		public void afterTextChanged(Editable s) {}
	};
	
	private OnItemSelectedListener itemSelectedBookFromListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int position,
				long id) {
			idBookStart = (int)id+1;
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {}
	};
	
	private OnItemSelectedListener itemSelectedBookToListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int position,
				long id) {
			idBookEnd = (int)id+1;
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {}
	};
	
	private void startSearch(String request){
		requestDB = "";
		requestDB = request;
		
		searchTask = new SearchTast();
		searchTask.execute();
		
	}
	
	private class SearchTast extends AsyncTask<Void, Void, List<SearchStruct>>{
		
		private ProgressDialog pd = null;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.d(TAG, "Search AsyncTask starting");
			pd = ProgressDialog.show(getActivity(), 
					getString(R.string.dialog_title_search)+" "+requestDB, 
					getString(R.string.dialog_start_search));
			pd.setCanceledOnTouchOutside(false);
		};

		@Override
		protected List<SearchStruct> doInBackground(Void... params) {
			Log.d(TAG, "IdBookStart: "+idBookStart+" | idBookEnd: "+idBookEnd);
			List<SearchStruct> result = db.searchInDataBase(requestDB, idBookStart, idBookEnd);
			db.insertSearchResult(result);
			return result;
		}
		
		@Override
		protected void onPostExecute(List<SearchStruct> result) {
			super.onPostExecute(result);
			searchResult.clear();
			searchResult.addAll(result);
			if(pd != null && pd.isShowing()) pd.dismiss();
			Log.d(TAG, "Search AsyncTask - CANCEL");
			
			updateListResult();
		};
		
	};
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_main, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.action_exit:
			getActivity().finish();
			break;
		case R.id.action_setting_app:
	   		Intent startSetting = new Intent(getActivity(), SettingActivity.class);
			startActivity(startSetting);
	   		return true;
		default:
			break;
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
