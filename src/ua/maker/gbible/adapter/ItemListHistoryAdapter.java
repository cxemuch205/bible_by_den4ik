package ua.maker.gbible.adapter;

import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.structs.HistoryStruct;
import ua.maker.gbible.utils.DataBase;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ItemListHistoryAdapter extends ArrayAdapter<HistoryStruct> {
	
	private Context context = null;
	private List<HistoryStruct> data = null;

	public ItemListHistoryAdapter(Context context, List<HistoryStruct> data) {
		super(context, R.layout.item_list_history, data);
		this.context = context;
		this.data = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder = null;
		
		if(view == null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			view = inflater.inflate(R.layout.item_list_history, null);
			
			holder = new ViewHolder();
			holder.tvTranslate = (TextView)view.findViewById(R.id.tv_translate);
			holder.tvLink = (TextView)view.findViewById(R.id.tv_link_h);
			holder.tvDateCreated = (TextView)view.findViewById(R.id.tv_date_created);
			
			view.setTag(holder);
		}
		else
			holder = (ViewHolder)view.getTag();
		
		HistoryStruct item = data.get(position);
		String translate = getTranslateWitchPreferences(item.getTranslate());
		holder.tvTranslate.setText(""+translate);
		holder.tvLink.setText(""+item.getBookName()+" "+item.getChapter());
		holder.tvDateCreated.setText(""+item.getDateCreated());
		
		return view;
	}
	
	public String getTranslateWitchPreferences(String idTranslate){
		String translateName = DataBase.TABLE_NAME_RST;

		switch(Integer.parseInt(""+idTranslate)){
			case 0:
				translateName = context.getString(R.string.is_rst_translate);
				break;
			case 1:
				translateName = context.getString(R.string.is_mt_translate);
				break;
		}
		return translateName;
	}
	
	static class ViewHolder{
		TextView tvTranslate;
		TextView tvLink;
		TextView tvDateCreated;
	}

}
