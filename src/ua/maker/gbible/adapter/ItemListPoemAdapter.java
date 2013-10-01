package ua.maker.gbible.adapter;

import java.util.List;

import ua.maker.gbible.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ItemListPoemAdapter extends ArrayAdapter<String> {
	
	private static final String TAG = "ItemListPoemAdapter";
	
	private int layoutResourseId = 0;
	private List<String> listPoems = null;
	private Context context = null;
	private SharedPreferences defPref = null;
	private boolean dayNight = false; //false - day / true - night

	public ItemListPoemAdapter(Context context, int textViewResourceId,
			List<String> objects) {
		super(context, textViewResourceId, objects);
		layoutResourseId = textViewResourceId;
		listPoems = objects;
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		AdapterHolder holder = null;
		
		defPref = PreferenceManager.getDefaultSharedPreferences(context);
		dayNight = (defPref.getString(context.getString(R.string.pref_mode_read), "0").equals("0"))?false:true;
		
		if(view == null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			view = inflater.inflate(layoutResourseId, parent, false);
			
			holder = new AdapterHolder();
			holder.tvNumberPoem = (TextView)view.findViewById(R.id.tv_number_of_poem);
			holder.tvContentPoem = (TextView)view.findViewById(R.id.tv_compare_content_poem);
			
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
		
		String content = listPoems.get(position);
		if(dayNight){
			holder.tvContentPoem.setTextColor(Color.WHITE);
			holder.tvNumberPoem.setTextColor(Color.WHITE);
			holder.tvContentPoem.setText(content);
			holder.tvContentPoem.setTextSize(size);
			holder.tvNumberPoem.setText(""+(position+1));
			holder.tvNumberPoem.setTextSize(size-1);
		}
		else
		{
			holder.tvContentPoem.setTextColor(Color.BLACK);
			holder.tvNumberPoem.setTextColor(Color.BLACK);
			holder.tvContentPoem.setText(content);
			holder.tvContentPoem.setTextSize(size);
			holder.tvNumberPoem.setText(""+(position+1));
			holder.tvNumberPoem.setTextSize(size-1);
		}
		
		return view;
	}
	
	static class AdapterHolder{
		TextView tvNumberPoem;
		TextView tvContentPoem;
	}

}
