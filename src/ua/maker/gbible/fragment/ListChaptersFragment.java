package ua.maker.gbible.fragment;

import java.io.IOException;
import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.constant.App;
import ua.maker.gbible.utils.DataBase;
import ua.maker.gbible.utils.Tools;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ListChaptersFragment extends SherlockFragment {
	
	private View view = null;
	private GridView gvShowChapters = null;
	private DataBase dataBase = null;
	
	private int bookId = 0;
	private String translate = "";
	
	private Button btnBook = null;
	private Button btnChapter = null;
	private Button btnPoem = null;
	
	private SharedPreferences sp = null;
	private SharedPreferences spDef = null;
	
	private ProgressDialog pd = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.activity_list_chapters, null);
		gvShowChapters = (GridView)view.findViewById(R.id.gv_show_chapters);
		getSherlockActivity().setTitle(getString(R.string.title_activity_list_chapters));

		btnBook = (Button)view.findViewById(R.id.btn_book);
		btnChapter = (Button)view.findViewById(R.id.btn_chapter);
		btnPoem = (Button)view.findViewById(R.id.btn_poem);
		
		dataBase = new DataBase(getSherlockActivity());
		try {
			dataBase.createDataBase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dataBase.openDataBase();
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		sp = getSherlockActivity().getSharedPreferences(App.PREF_SEND_DATA, 0);
		spDef = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
		
		btnChapter.setBackgroundResource(R.drawable.btn_active_select);		

		bookId = sp.getInt(App.BOOK_ID, 1);
		Log.d("Getting Book_id", "id = " + bookId);
		btnBook.setText(Tools.getBookNameByBookId(bookId, getSherlockActivity()));
		btnBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(getFragmentManager().findFragmentByTag(App.TAG_FRAGMENT_BOOKS) != null){
					FragmentTransaction ft = getFragmentManager().
							 beginTransaction();
					ft.remove(getFragmentManager().findFragmentByTag(App.TAG_FRAGMENT_BOOKS));
					ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
					ft.addToBackStack(null);
					ft.commit();
				}
				
				FragmentTransaction ft = getFragmentManager().
						 beginTransaction();
				ft.replace(R.id.flRoot, new StartFragment(), App.TAG_FRAGMENT_BOOKS);
				ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		btnPoem.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!sp.contains(App.CHAPTER) && sp.contains(App.BOOK_ID))
					Tools.showToast(getSherlockActivity(), getString(R.string.no_select_chapter));
			}
		});
		
		//Start get content from dataBase
		pd = ProgressDialog.show(getSherlockActivity(), 
				getString(R.string.progress_dialog_title), 
				getString(R.string.progress_dialog_message));
		
		List<Integer> listChapters = dataBase.getChapters(DataBase.TABLE_NAME_RST, bookId);
		ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getSherlockActivity(), 
				android.R.layout.simple_list_item_1, listChapters);
		gvShowChapters.setAdapter(adapter);
		if(pd.isShowing()) pd.cancel();
		gvShowChapters.setOnItemClickListener(listenerItemGriatView);
	}
	
	private OnItemClickListener listenerItemGriatView = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			SharedPreferences sp = getSherlockActivity().getSharedPreferences(App.PREF_SEND_DATA, 0);
			Editor editor = sp.edit();
			editor.putInt(App.BOOK_ID, bookId);
			editor.putInt(App.CHAPTER, position+1);
			editor.putInt(App.POEM_SET_FOCUS, 0);
			editor.commit();
			
			//Write to history
			dataBase.insertHistory(bookId, (int)(id+1), 1, translate);
			//#######################
			
			FragmentTransaction ft = getFragmentManager().
					 beginTransaction();
			ft.replace(R.id.flRoot, new ListPoemsFragment(), App.TAG_FRAGMENT_POEMS);
			ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
			ft.addToBackStack(null);
			ft.commit();
		}
	};
	
	@Override
	public void onResume() {
		super.onResume();
		translate = spDef.getString(getString(R.string.pref_default_translaters), DataBase.TABLE_NAME_RST);
	};
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_main, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	   	switch(item.getItemId()){
	   	case R.id.action_exit:
	   		getSherlockActivity().finish();
	   		return true;
	   	}
	   	return super.onOptionsItemSelected(item);
	}
}
