package ua.maker.gbible.fragment;

import java.io.IOException;

import ua.maker.gbible.R;
import ua.maker.gbible.constant.App;
import ua.maker.gbible.utils.DataBase;
import ua.maker.gbible.utils.Tools;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class StartFragment extends SherlockFragment {
	
	private static final String TAG = "StartFragment";
	
	private View view = null;
	private ListView lvShowBooks = null;
	private ArrayAdapter<String> adapter = null;
	
	private DataBase dataBase = null;
	
	private Button btnBook = null;
	private Button btnChapter = null;
	private Button btnPoem = null;
	
	private SharedPreferences sp = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.activity_select_book, null);
		lvShowBooks = (ListView)view.findViewById(R.id.lv_show_books);
		
		btnBook = (Button)view.findViewById(R.id.btn_book);
		btnChapter = (Button)view.findViewById(R.id.btn_chapter);
		btnPoem = (Button)view.findViewById(R.id.btn_poem);
		
		Log.d(TAG, "Start activity create");
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setHasOptionsMenu(true);
		getSherlockActivity().setTitle(getString(R.string.app_name));
		dataBase = new DataBase(getSherlockActivity());
		
		try {
			dataBase.createDataBase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dataBase.openDataBase();
		
		btnBook.setBackgroundResource(R.drawable.btn_active_select);
		
		sp = getSherlockActivity().getSharedPreferences(App.PREF_SEND_DATA, 0);
		if(sp.contains(App.BOOK_ID) && sp.contains(App.CHAPTER)){
			FragmentTransaction ft = getFragmentManager().
					 beginTransaction();
			ft.replace(R.id.flRoot, new ListPoemsFragment(), App.TAG_FRAGMENT_POEMS);
			ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
			ft.addToBackStack(null);
			ft.commit();
		}		
		
		btnChapter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(sp.contains(App.BOOK_ID) /*&& sp.contains(App.CHAPTER)*/){
					FragmentTransaction ft = getFragmentManager().
							 beginTransaction();
					ft.replace(R.id.flRoot, new ListChaptersFragment(), App.TAG_FRAGMENT_CHAPTERS);
					ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
					ft.addToBackStack(null);
					ft.commit();
				}
				else
					Tools.showToast(getSherlockActivity(), getString(R.string.no_select_book));
			}
		});
		
		btnPoem.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(sp.contains(App.BOOK_ID) && sp.contains(App.CHAPTER)){
					FragmentTransaction ft = getFragmentManager().
							 beginTransaction();
					ft.replace(R.id.flRoot, new ListPoemsFragment(), App.TAG_FRAGMENT_POEMS);
					ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
					ft.addToBackStack(null);
					ft.commit();
				}
				else
					Tools.showToast(getSherlockActivity(), getString(R.string.no_select_book));
			}
		});

		adapter = new ArrayAdapter<String>(getSherlockActivity(), 
				android.R.layout.simple_list_item_1,
				dataBase.getBooks(DataBase.TABLE_NAME_RST));
		lvShowBooks.setAdapter(adapter);
		lvShowBooks.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				// TODO Auto-generated method stub
				SharedPreferences sp = getSherlockActivity().getSharedPreferences(App.PREF_SEND_DATA, 0);
				Editor editor = sp.edit();
				editor.putInt(App.BOOK_ID, position+1);
				editor.commit();
				FragmentTransaction ft = getFragmentManager().
						 beginTransaction();
				ft.replace(R.id.flRoot, new ListChaptersFragment(), App.TAG_FRAGMENT_CHAPTERS);
				ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.menu_main, menu);
		menu.setQwertyMode(true);
		super.onCreateOptionsMenu(menu, inflater);
	}
	    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	   	// TODO Auto-generated method stub
	   	switch(item.getItemId()){
	   	case R.id.action_exit:
	   		getSherlockActivity().finish();
	   		return true;
	   	}
	   	return super.onOptionsItemSelected(item);
	}

}
