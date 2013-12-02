package ua.maker.gbible.adapter;

import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.structs.BookMarksStruct;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ItemListBookmarksAdapter extends ArrayAdapter<BookMarksStruct> {
	
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
			holder.tvComment = (TextView)view.findViewById(R.id.tv_comment_bookmark_fragment);
			
			view.setTag(holder);
		}
		else
			holder = (ViewHolder)view.getTag();
		
		BookMarksStruct item = listBookmarks.get(position);
		holder.tvLink.setText(""+item.getBookName()+" "+item.getChapter()+":"+item.getPoem()+item.getLinkNext());		
		holder.tvSmallContent.setText(""+item.getContent());
		String comment = item.getComment();
		if(comment.length() > 0){
			holder.tvSmallContent.setSingleLine(true);
			holder.tvComment.setText(""+item.getComment());
			holder.tvComment.setTextColor(Color.RED);
		}
		else
		{
			holder.tvSmallContent.setSingleLine(false);
			holder.tvSmallContent.setMaxLines(2);
			holder.tvComment.setVisibility(TextView.GONE);
		}
		
		return view;
	}
	
	static class ViewHolder
	{
		TextView tvLink;
		TextView tvSmallContent;
		TextView tvComment;
	}

}
