package ua.maker.gbible.adapter;

import java.util.ArrayList;
import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.structs.ColorStruct;
import ua.maker.gbible.structs.PoemStruct;
import ua.maker.gbible.utils.UserDB;
import ua.maker.gbible.widget.setting.ColorPickerPreference;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ItemListPoemAdapter extends ArrayAdapter<PoemStruct> {
	
	private List<PoemStruct> listPoems = null;
	private Context context = null;
	private SharedPreferences defPref = null;
	private boolean dayNight = false;
	private UserDB userDB;
	private List<View> listViews = new ArrayList<View>();
	private SharedPreferences prefs;

	public ItemListPoemAdapter(Context context, List<PoemStruct> data, UserDB db) {
		super(context, R.layout.item_list_poems, data);
		this.listPoems = data;
		this.context = context;
		this.userDB = db;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		defPref = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public boolean updateListView(int position, ListView listView, boolean isUpdate) {
	    int first = listView.getFirstVisiblePosition();
	    int last = listView.getLastVisiblePosition();
	    if(position < first || position > last) {
	        return false;
	    }
	    else {
	        View convertView = listView.getChildAt(position - first);
	        PoemStruct content = listPoems.get(position);
	        if(isUpdate){
	        	ColorStruct colorItem = userDB.getPoemMarkerColor(content.getBookId(), content.getChapter(), position);
	        	content.setColorHEX(colorItem.getHex());
	        	content.setPosColor(colorItem.getPosition());
	        }
			
			if(content.getColorHEX().length() > 0){
				convertView.setBackgroundColor(Color.parseColor(content.getColorHEX()));
			}
			else
			{
				convertView.setBackgroundColor(Color.parseColor("#00000000"));
			}
	        return true;
	    }
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		AdapterHolder holder = null;
		
		dayNight = (defPref.getString(context.getString(R.string.pref_mode_read), "0").equals("0"))?false:true;
		String color = ""+defPref.getInt(context.getString(R.string.pref_color_text), context.getResources().getInteger(R.integer.COLOR_BLACK));
		
		if(view == null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			view = inflater.inflate(R.layout.item_list_poems, null);
			
			holder = new AdapterHolder();
			holder.tvNumberPoem = (TextView)view.findViewById(R.id.tv_number_of_poem);
			holder.tvContentPoem = (TextView)view.findViewById(R.id.tv_compare_content_poem);
			
			view.setTag(holder);
		}
		else
		{
			holder = (AdapterHolder)view.getTag();
		}
		
		int size = 14;
		if(prefs.contains(context.getString(R.string.pref_size_text_poem))){
			size = prefs.getInt(context.getString(R.string.pref_size_text_poem), 15);
		}
		
		PoemStruct content = listPoems.get(position);
		if(!(content.getColorHEX().length() > 0)){
			ColorStruct colorItem = userDB.getPoemMarkerColor(content.getBookId(), content.getChapter(), position);
			content.setColorHEX(colorItem.getHex());
			content.setPosColor(colorItem.getPosition());
		}
		
		if(content.isChecked()){
			view.setBackgroundResource(R.color.color_selected_list_item);
		}
		
		if(content.getColorHEX().length() > 0){
			view.setBackgroundColor(Color.parseColor(content.getColorHEX()));
		}
		else
		{
			view.setBackgroundColor(Color.parseColor("#00000000"));
		}
		
		if(dayNight){
			holder.tvContentPoem.setTextColor(Color.WHITE);
			holder.tvNumberPoem.setTextColor(Color.WHITE);
			holder.tvContentPoem.setText(content.getText());
			holder.tvContentPoem.setTextSize(size);
			holder.tvNumberPoem.setText(""+(position+1));
			holder.tvNumberPoem.setTextSize(size-1);
		}
		else
		{
			holder.tvContentPoem.setTextColor(Color.parseColor(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(color)))));
			holder.tvNumberPoem.setTextColor(Color.parseColor(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(color)))));
			holder.tvContentPoem.setText(content.getText());
			holder.tvContentPoem.setTextSize(size);
			holder.tvNumberPoem.setText(""+(position+1));
			if(size <=18){
				holder.tvNumberPoem.setTextSize(size-3);
			}
			else
			{
				holder.tvNumberPoem.setTextSize(15);
			}
		}
		int k = 0;
		for(int i = 0; i < listViews.size(); i++){
			if(listViews.get(i) != view){
				k++;
			}
		}
		if(listViews.size() == 0 || k == listViews.size())
			listViews.add(view);
		
		return view;
	}
	
	static class AdapterHolder{
		TextView tvNumberPoem;
		TextView tvContentPoem;
	}

}
