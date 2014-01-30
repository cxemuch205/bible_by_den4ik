package ua.maker.gbible.adapter;

import java.util.List;

import ua.maker.gbible.BuildConfig;
import ua.maker.gbible.R;
import ua.maker.gbible.structs.ItemReadDay;
import ua.maker.gbible.utils.DataBase;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ItemLinksWithHeadersAdapter extends ArrayAdapter<ItemReadDay> {
	
	private static final String TAG = "LinksWithHeadersAdapter";
	
	private List<ItemReadDay> data = null;
	private Context context = null;
	private DataBase db = null;
	
	public ItemLinksWithHeadersAdapter(Context context, List<ItemReadDay> data, DataBase db) {
		super(context, R.layout.item_read_day_layout, data);
		this.data = data;
		this.context = context;
		this.db = db;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder = null;
		
		if(view == null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			view = inflater.inflate(R.layout.item_read_day_layout, null);
			
			holder = new ViewHolder();
			holder.tvDay = (TextView)view.findViewById(R.id.tv_day);
			holder.llOldTestament = (TextView)view.findViewById(R.id.tv_list_old_testament);
			holder.llNewTestament = (TextView)view.findViewById(R.id.tv_list_new_testament);
			holder.tbStatus = (ToggleButton)view.findViewById(R.id.tb_status);
			
			view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)view.getTag();
		}
		
		final ItemReadDay item = data.get(position);
		boolean status = false;
		if(item.isStatusReaded()){
			status = true;
		}
		else
			status = false;
		
		holder.tbStatus.setChecked(status);
		String date = String.valueOf(item.getDay());
		if(item.getDay() == 1){
			date = item.getMonth()+" "+String.valueOf(item.getDay());
		}
		holder.tvDay.setText(date);
		
		String poemOldLink = "";
		boolean noStandartOld = false;
		if(BuildConfig.DEBUG)
			Log.i(TAG, "Pos error: " + position);
		for(int i = 0; i < item.getListPoemOld().size(); i++)
			if(item.getListPoemOld().get(i).getPoemTo() != 0){
				noStandartOld = true;
				break;
			}	
		for(int i = 0; i < item.getListPoemOld().size(); i++){			
			if(!noStandartOld){
				poemOldLink += item.getListPoemOld().get(i).getBookName()+" "
						+item.getListPoemOld().get(i).getChapter();
			}
			else
			{
				poemOldLink += item.getListPoemOld().get(i).getBookName()+" "
						+item.getContentChapterOldTFull();
			}
			if(i != item.getListPoemOld().size()-1)
				poemOldLink += "\n";
		}
		String poemNewLink = "";
		boolean noStandartNew = false;
		for(int i = 0; i < item.getListPoemNew().size(); i++)
			if(item.getListPoemNew().get(i).getPoemTo() != 0){
				noStandartNew = true;
				break;
			}	
		for(int i = 0; i < item.getListPoemNew().size(); i++){			
			if(!noStandartNew){
				poemNewLink += item.getListPoemNew().get(i).getBookName()+" "
						+item.getListPoemNew().get(i).getChapter();
			}
			else
			{
				poemNewLink += item.getListPoemNew().get(i).getBookName()+" "
						+item.getContentChapterNewTFull();
			}
			
			if(i != item.getListPoemNew().size()-1)
				poemNewLink += "\n";
		}
		
		holder.llNewTestament.setText(String.valueOf(poemNewLink));
		holder.llOldTestament.setText(String.valueOf(poemOldLink));
		
		holder.tbStatus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				item.setStatus(!item.isStatusReaded());
				db.setStatusItemReadForEveryDay(position, item.isStatusReaded());
			}
		});
		
		return view;
	}
	
	static class ViewHolder{
		TextView tvDay;
		ToggleButton tbStatus;
		TextView llOldTestament;
		TextView llNewTestament;
	}
}