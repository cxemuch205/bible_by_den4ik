package ua.maker.gbible.adapter;

import java.util.List;

import ua.maker.gbible.R;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemDialogAdapter extends ArrayAdapter<String> {
	
	private static final String TAG = "ItemDialogAdapter";
	
	private String[] data = null;
	private List<Integer> idPictures = null;
	private Context context = null;
	
	public ItemDialogAdapter(Context context, String[] data, List<Integer> idPictures) {
		super(context, R.layout.item_dialog_list,data);
		this.context = context;
		this.data = data;
		this.idPictures = idPictures;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder = null;
		
		if(view == null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			view = inflater.inflate(R.layout.item_dialog_list, null);
			
			holder = new ViewHolder();
			holder.ivAction = (ImageView)view.findViewById(R.id.iv_action);
			holder.tvAction = (TextView)view.findViewById(R.id.tv_action);
			
			view.setTag(holder);
		}
		else
			holder = (ViewHolder)view.getTag();
		
		Log.d(TAG, "Add new Item["+position+"]");
		holder.ivAction.setImageResource(idPictures.get(position));
		holder.tvAction.setText(data[position]);
		
		return view;
	}
	
	static class ViewHolder
	{
		ImageView ivAction;
		TextView tvAction;
	}

}
