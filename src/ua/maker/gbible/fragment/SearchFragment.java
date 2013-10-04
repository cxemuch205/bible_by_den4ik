package ua.maker.gbible.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.adapter.ItemListSearchAdapter;
import ua.maker.gbible.constant.App;
import ua.maker.gbible.structs.SearchStruct;
import ua.maker.gbible.utils.DataBase;
import ua.maker.gbible.utils.Tools;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SearchFragment extends SherlockFragment {

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
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_search, null);
		
		etSearchText = (EditText)view.findViewById(R.id.et_search_text);
		btnStartSearch = (Button)view.findViewById(R.id.btn_start_search);
		spinnerBookFrom = (Spinner)view.findViewById(R.id.spinner_book_from);
		spinnerBookTo = (Spinner)view.findViewById(R.id.spinner_book_to);
		lvResultSearch = (ListView)view.findViewById(R.id.lv_search_result);
		
		pref = getSherlockActivity().getSharedPreferences(App.PREF_SEND_DATA, 0);
		if(!getSherlockActivity().getActionBar().isShowing())
			getSherlockActivity().getActionBar().show();
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		getSherlockActivity().setTitle(getString(R.string.search_str));
		db = new DataBase(getSherlockActivity());
		try {
			db.createDataBase();
		} catch (IOException e) {e.printStackTrace();}
		
		db.openDataBase();
		searchResult = new ArrayList<SearchStruct>();
		adapter = new ItemListSearchAdapter(getSherlockActivity(), searchResult);
		lvResultSearch.setAdapter(adapter);
		
		if(pref.contains(App.SEARCH_REQUEST)){
			requestDB = pref.getString(App.SEARCH_REQUEST, "");
			etSearchText.setHint(requestDB);
		}
		
		searchResult.addAll(db.getSearchResult());
		updateListResult();
		
		lvResultSearch.setOnItemClickListener(itemResultSearchListener);
		spinnerBookFrom.setOnItemSelectedListener(itemSelectedBookFromListener);
		spinnerBookTo.setOnItemSelectedListener(itemSelectedBookToListener);
		spinnerBookTo.setSelection(65);
		btnStartSearch.setOnClickListener(clickSearchListener);
		etSearchText.addTextChangedListener(changeTextListener);
	}
	
	private void updateListResult() {
		adapter.notifyDataSetChanged();
	}
	
	private OnItemClickListener itemResultSearchListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			Editor editor = pref.edit();
			editor.putInt(App.BOOK_ID, searchResult.get((int)id).getIdBook());
			editor.putInt(App.CHAPTER, searchResult.get((int)id).getChapter());
			editor.putInt(App.POEM_SET_FOCUS, searchResult.get((int)id).getPoem()-1);
			editor.commit();
			
			FragmentTransaction ft = getFragmentManager().
					 beginTransaction();
			ft.replace(R.id.flRoot, new ListPoemsFragment(), App.TAG_FRAGMENT_POEMS);
			ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
			ft.addToBackStack(null);
			ft.commit();
		}
	};

	private OnClickListener clickSearchListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(!etSearchText.getText().toString().equals("") || requestDB != ""){
				if(etSearchText.getText().toString().equals(""))
					startSearch(requestDB);
				else
					startSearch(etSearchText.getText().toString());
				
				//hide keyboard
				getSherlockActivity();
				InputMethodManager imm = (InputMethodManager)getSherlockActivity()
						.getSystemService(SherlockFragmentActivity.INPUT_METHOD_SERVICE); 
				imm.hideSoftInputFromWindow(etSearchText.getWindowToken(), 0);
				
				pref.edit().putString(App.SEARCH_REQUEST, requestDB).commit();
				db.clearSearchResultHistory();
				
				if(searchResult.size()>0)
					searchResult.clear();
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
			idBookStart = (int)id;
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {}
	};
	
	private OnItemSelectedListener itemSelectedBookToListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int position,
				long id) {
			idBookEnd = (int)id;
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {}
	};
	
	private void startSearch(String request){
		requestDB = "";
		requestDB = request;
		Log.d(TAG, "START - SEARCH");
		
		searchTask = new SearchTast();
		searchTask.execute();
		
	}
	
	private class SearchTast extends AsyncTask<String, Void, List<SearchStruct>>{
		
		private ProgressDialog pd = null;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.d(TAG, "Search AsyncTask starting");
			pd= ProgressDialog.show(getSherlockActivity(), 
					getString(R.string.dialog_title_search)+" "+requestDB, 
					getString(R.string.dialog_start_search));
		};

		@Override
		protected List<SearchStruct> doInBackground(String... params) {
			return db.searchInDataBase(requestDB, idBookStart, idBookEnd);
		}
		
		@Override
		protected void onPostExecute(java.util.List<SearchStruct> result) {
			super.onPostExecute(result);
			if(pd.isShowing()) pd.dismiss();
			Log.d(TAG, "Search AsyncTask - CANCEL");
			searchResult.addAll(result);
			
			db.insertSearchResult(result);
			
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
			getSherlockActivity().finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
