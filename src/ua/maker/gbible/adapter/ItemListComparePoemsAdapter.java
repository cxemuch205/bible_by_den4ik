package ua.maker.gbible.adapter;

import java.util.HashMap;
import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.constant.App;
import ua.maker.gbible.structs.PoemStruct;
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
		ArrayAdapter<PoemStruct> {
	
	private Context context = null;
	private List<PoemStruct> listComparePoems = null;

	public ItemListComparePoemsAdapter(Context context,	List<PoemStruct> objects) {
		super(context, R.layout.item_compare_poem, objects);
		this.context = context;
		listComparePoems = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		AdapterHolder holder = null;
		
		if(row == null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(R.layout.item_compare_poem, null);
			
			holder = new AdapterHolder();
			holder.tvLabelTranslate = (TextView)row.findViewById(R.id.tv_translate_label);
			holder.tvContent = (TextView)row.findViewById(R.id.tv_compare_content_poem);
			
			row.setTag(holder);
		}
		else{
			holder = (AdapterHolder)row.getTag();
		}
		
		PoemStruct content = listComparePoems.get(position);
		
		if(content.getTranslateSource().equals(DataBase.TABLE_NAME_RST)){
			String nameT = context.getString(R.string.rus_translate_str);
			Spannable nameTranslate = new SpannableString(nameT);
			nameTranslate.setSpan(new ForegroundColorSpan(Color.RED), 0, nameT.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			holder.tvLabelTranslate.setText(nameTranslate);
		}
		else
			if(content.getTranslateSource().equals(DataBase.TABLE_NAME_MT))
			{
				String nameT = context.getString(R.string.rus_modern_translate_str);
				Spannable nameTranslate = new SpannableString(nameT);
				nameTranslate.setSpan(new ForegroundColorSpan(Color.RED), 0, nameT.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				holder.tvLabelTranslate.setText(nameTranslate);
			}
			else
				if(content.getTranslateSource().equals(DataBase.TABLE_NAME_UAT))
				{
					String nameT = context.getString(R.string.ua_translate_str);
					Spannable nameTranslate = new SpannableString(nameT);
					nameTranslate.setSpan(new ForegroundColorSpan(Color.RED), 0, nameT.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					holder.tvLabelTranslate.setText(nameTranslate);
				}
				else if(content.getTranslateSource().equals(DataBase.TABLE_NAME_ENT)){
					String nameT = context.getString(R.string.eng_translate_str);
					Spannable nameTranslate = new SpannableString(nameT);
					nameTranslate.setSpan(new ForegroundColorSpan(Color.RED), 0, nameT.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					holder.tvLabelTranslate.setText(nameTranslate);
				}
		holder.tvContent.setText(content.getContent());
		return row;
	}
	
	static class AdapterHolder{
		TextView tvLabelTranslate;
		TextView tvContent;
	}
}
