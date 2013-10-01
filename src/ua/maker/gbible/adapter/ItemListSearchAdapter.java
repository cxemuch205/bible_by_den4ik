package ua.maker.gbible.adapter;

import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.structs.SearchStruct;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class ItemListSearchAdapter extends ArrayAdapter<SearchStruct> {
	
	private static final String TAG = "ItemListPoemAdapter";
	
	private List<SearchStruct> listPoems = null;
	private Context context = null;

	public ItemListSearchAdapter(Context context, List<SearchStruct> objects) {
		super(context, R.layout.item_list_poems_search, objects);
		listPoems = objects;
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = convertView;
		AdapterHolder holder = null;
		
		if(view == null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			view = inflater.inflate(R.layout.item_list_poems_search, null);
			
			holder = new AdapterHolder();
			holder.tvLinkPoem = (TextView)view.findViewById(R.id.tv_link_poem_search);
			holder.tvContentPoem = (TextView)view.findViewById(R.id.tv_content_poem_search);
			
			view.setTag(holder);
		}
		else
		{
			holder = (AdapterHolder)view.getTag();
		}
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		int size = 14;
		Log.d(TAG, "Start geting prefs - size poem text");
		if(prefs.contains(context.getString(R.string.pref_size_text_poem))){
			Log.d(TAG, "contains - true");
			size = prefs.getInt(context.getString(R.string.pref_size_text_poem), 15);
			Log.d(TAG, "size = " + size);
		}
		else
			Log.d(TAG, "no contains");
		
		SearchStruct item = listPoems.get(position);
		holder.tvContentPoem.setText(""+item.getContent());
		holder.tvContentPoem.setTextSize(size);
		holder.tvLinkPoem.setText(""+item.getBookName()==null?"null":item.getBookName()+" "+item.getChapter()+":"+item.getPoem());
		holder.tvLinkPoem.setTextSize(size-1);
		return view;
	}

	static class AdapterHolder{
		TextView tvLinkPoem;
		TextView tvContentPoem;
	}

}
