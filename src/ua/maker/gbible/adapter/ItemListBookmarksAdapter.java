package ua.maker.gbible.adapter;

import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.structs.BookMarksStruct;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ItemListBookmarksAdapter extends ArrayAdapter<BookMarksStruct> {
	
	private static final String TAG = "ItemListBookmarksAdapter";
	
	private Context mContext = null;
	private List<BookMarksStruct> listBookmarks = null;

	public ItemListBookmarksAdapter(Context context, List<BookMarksStruct> objects) {
		super(context, R.layout.item_bookmarks, objects);
		mContext = context;
		listBookmarks = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder = null;
		
		if(view == null){
			LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
			view = inflater.inflate(R.layout.item_bookmarks, null);
			
			holder = new ViewHolder();
			holder.tvLink = (TextView)view.findViewById(R.id.tv_link);
			holder.tvSmallContent = (TextView)view.findViewById(R.id.tv_smal_content);
			
			view.setTag(holder);
		}
		else
			holder = (ViewHolder)view.getTag();
		
		Log.d(TAG, "Add new item["+position+"]");
		BookMarksStruct item = listBookmarks.get(position);
		holder.tvLink.setText(""+item.getBookName()+" "+item.getChapter()+":"+item.getPoem());
		
		holder.tvSmallContent.setText(""+item.getContent());
		
		return view;
	}
	
	static class ViewHolder
	{
		TextView tvLink;
		TextView tvSmallContent;
	}

}
