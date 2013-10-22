package ua.maker.gbible.adapter;

import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.structs.PlanStruct;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ItemPlanListAdapter extends ArrayAdapter<PlanStruct> {

	private Context context = null;
	
	private List<PlanStruct> data = null;
	
	public ItemPlanListAdapter(Context context, List<PlanStruct> data) {
		super(context, R.layout.item_plans_list, data);
		this.context = context;
		this.data = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder = null;
		
		if(view == null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			view = inflater.inflate(R.layout.item_plans_list, null);
			
			holder = new ViewHolder();
			holder.tvName = (TextView)view.findViewById(R.id.tv_pl_name);
			holder.tvDate = (TextView)view.findViewById(R.id.tv_pl_date);
			holder.tvDescription = (TextView)view.findViewById(R.id.tv_pl_sub_description);
			
			view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)view.getTag();
		}
		
		PlanStruct item = data.get(position);
		holder.tvName.setText(""+item.getName());
		holder.tvDate.setText(""+item.getDate());
		holder.tvDescription.setText(""+item.getSubDescription());
		
		return view;
	}
	
	static class ViewHolder
	{
		TextView tvName;
		TextView tvDescription;
		TextView tvDate;
	}

}
