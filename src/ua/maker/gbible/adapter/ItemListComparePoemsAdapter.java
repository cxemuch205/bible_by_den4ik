package ua.maker.gbible.adapter;

import java.util.HashMap;
import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.constant.App;
import ua.maker.gbible.utils.DataBase;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ItemListComparePoemsAdapter extends
		ArrayAdapter<HashMap<String, String>> {
	
	private int layoutResId = 0;
	private Context context = null;
	private List<HashMap<String, String>> listComparePoems = null;

	public ItemListComparePoemsAdapter(Context context, int textViewResourceId,
			List<HashMap<String, String>> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		layoutResId = textViewResourceId;
		listComparePoems = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View row = convertView;
		AdapterHolder holder = null;
		
		if(row == null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResId, parent, false);
			
			holder = new AdapterHolder();
			holder.tvLabelTranslate = (TextView)row.findViewById(R.id.tv_translate_label);
			holder.tvContent = (TextView)row.findViewById(R.id.tv_compare_content_poem);
			
			row.setTag(holder);
		}
		else
		{
			holder = (AdapterHolder)row.getTag();
		}
		
		HashMap<String,String> content = listComparePoems.get(position);
		
		if(content.get(App.TRANSLATE_LABEL).equals(DataBase.TABLE_NAME_RST)){
			Spannable nameTranslate = new SpannableString(DataBase.TRANSLATE_NAME_RST);
			nameTranslate.setSpan(new ForegroundColorSpan(Color.RED), 0, DataBase.TRANSLATE_NAME_RST.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			holder.tvLabelTranslate.setText(nameTranslate);
		}
		else
		{
			Spannable nameTranslate = new SpannableString(DataBase.TRANSLATE_NAME_MT);
			nameTranslate.setSpan(new ForegroundColorSpan(Color.RED), 0, DataBase.TRANSLATE_NAME_MT.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			holder.tvLabelTranslate.setText(nameTranslate);
		}
		holder.tvContent.setText(content.get(App.POEM));
		return row;
	}
	
	static class AdapterHolder{
		TextView tvLabelTranslate;
		TextView tvContent;
	}

}
