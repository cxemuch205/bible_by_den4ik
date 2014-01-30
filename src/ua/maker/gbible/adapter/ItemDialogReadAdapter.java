package ua.maker.gbible.adapter;

import java.util.List;

import ua.maker.gbible.structs.PoemStruct;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ItemDialogReadAdapter extends ArrayAdapter<PoemStruct> {
	
	private Context context = null;
	private List<PoemStruct> data = null;

	public ItemDialogReadAdapter(Context context, List<PoemStruct> data) {
		super(context, android.R.layout.simple_list_item_1, data);
		this.context = context;
		this.data = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
				
		if(convertView == null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
			holder = new ViewHolder();
			holder.tvData = (TextView)convertView;
			
			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder)convertView.getTag();
		}
		PoemStruct item = data.get(position);
		if(item.getPoemTo() == 0){
			holder.tvData.setText(item.getBookName()+" "+item.getChapter());
		}
		else
		{
			holder.tvData.setText(item.getBookName()+" "+item.getChapter()+":"+item.getPoem()+"-"+item.getPoemTo());
		}
		
		
		return convertView;
	}

	static class ViewHolder{
		TextView tvData;
	}
}
