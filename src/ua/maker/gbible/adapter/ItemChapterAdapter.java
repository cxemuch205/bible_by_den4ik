package ua.maker.gbible.adapter;

import java.util.List;

import ua.maker.gbible.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ItemChapterAdapter extends ArrayAdapter<Integer> {
	
	private List<Integer> data = null;
	private Context context = null;

	public ItemChapterAdapter(Context context, List<Integer> data) {
		super(context, R.layout.item_chapter_layout, data);
		this.data = data;
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder = null;
		
		if(view == null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			view = inflater.inflate(R.layout.item_chapter_layout, null);
			
			holder = new ViewHolder();
			holder.tvChapter = (TextView)view.findViewById(R.id.tv_number_chapter);
			
			view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)view.getTag();
		}
		
		view.setBackgroundResource(R.drawable.selector_item_chapter);
		
		holder.tvChapter.setText(""+data.get(position));
		
		return view;
	}
	
	static class ViewHolder
	{
		TextView tvChapter;
	}
}
