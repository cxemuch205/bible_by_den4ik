package ua.maker.gbible.adapter;

import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.constant.PlanData;
import ua.maker.gbible.structs.ItemPlanStruct;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlanItemAdapter extends ArrayAdapter<ItemPlanStruct> {
	
	private Context context = null;
	private List<ItemPlanStruct> data = null;
	
	public PlanItemAdapter(Context context, List<ItemPlanStruct> data) {
		super(context, R.layout.item_plan_item_text, data);
		this.context = context;
		this.data = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder = null;
		
		if(view == null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			view = inflater.inflate(R.layout.item_plan_item_text, null);
			
			holder = new ViewHolder();
			holder.tvTitle = (TextView)view.findViewById(R.id.tv_title_text);
			holder.tvBoldTitle = (TextView)view.findViewById(R.id.tv_title_bold_text);
			holder.tvMsgSmall = (TextView)view.findViewById(R.id.tv_msg_small);
			holder.tvMsgMedium = (TextView)view.findViewById(R.id.tv_medium_msg);
			holder.ivShowImg = (ImageView)view.findViewById(R.id.iv_img);
			holder.tvTitleForImg = (TextView)view.findViewById(R.id.tv_for_img);
			
			view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)view.getTag();
		}
		
		ItemPlanStruct item = data.get(position);
		switch (item.getDataType()) {
		case PlanData.DATA_TEXT:
			holder.tvTitle.setText(""+item.getText());
			holder.tvBoldTitle.setVisibility(TextView.GONE);
			holder.tvMsgMedium.setVisibility(TextView.GONE);
			holder.tvMsgSmall.setVisibility(TextView.GONE);
			holder.tvTitleForImg.setVisibility(TextView.GONE);
			holder.ivShowImg.setVisibility(ImageView.GONE);
			break;
		case PlanData.DATA_TEXT_BOLD:
			holder.tvTitle.setVisibility(TextView.GONE);
			holder.tvBoldTitle.setText(""+item.getText());
			holder.tvMsgMedium.setVisibility(TextView.GONE);
			holder.tvMsgSmall.setVisibility(TextView.GONE);
			holder.tvTitleForImg.setVisibility(TextView.GONE);
			holder.ivShowImg.setVisibility(ImageView.GONE);
			break;
		case PlanData.DATA_LINK:
			String toPoemShow = item.getToPoem()!=item.getPoem()?(" - "+item.getToPoem()):"";
			holder.tvTitle.setText(item.getBookName() + " "
					+ item.getChapter() + ":"
					+ item.getPoem()
					+ toPoemShow);
			holder.tvBoldTitle.setVisibility(TextView.GONE);
			holder.tvMsgMedium.setVisibility(TextView.GONE);
			holder.tvMsgSmall.setVisibility(TextView.GONE);
			holder.tvTitleForImg.setVisibility(TextView.GONE);
			holder.ivShowImg.setVisibility(ImageView.GONE);
			break;
		case PlanData.DATA_LINK_WITH_TEXT:
			String toPoemShowing = item.getToPoem()!=item.getPoem()?(" - "+item.getToPoem()):"";
			holder.tvTitle.setText(item.getBookName() + " "
					+ item.getChapter() + ":"
					+ item.getPoem()
					+ toPoemShowing);
			holder.tvBoldTitle.setVisibility(TextView.GONE);
			if(item.getText().length()>=64){
				holder.tvMsgSmall.setText(""+item.getText());
				holder.tvMsgMedium.setVisibility(TextView.GONE);
			}
			else
			{
				holder.tvMsgMedium.setText(""+item.getText());
				holder.tvMsgSmall.setVisibility(TextView.GONE);
			}
			holder.tvTitleForImg.setVisibility(TextView.GONE);
			holder.ivShowImg.setVisibility(ImageView.GONE);
			break;
		case PlanData.DATA_IMG:
			holder.tvTitle.setVisibility(TextView.GONE);
			holder.tvBoldTitle.setVisibility(TextView.GONE);
			holder.tvMsgMedium.setVisibility(TextView.GONE);
			holder.tvMsgSmall.setVisibility(TextView.GONE);
			holder.tvTitleForImg.setVisibility(TextView.GONE);
			Bitmap picture = BitmapFactory.decodeFile(item.getPathImg());
			holder.ivShowImg.setImageBitmap(picture);
			break;
		case PlanData.DATA_TEXT_WITH_IMG:
			holder.tvTitle.setText(""+item.getText());
			holder.tvBoldTitle.setVisibility(TextView.GONE);
			holder.tvMsgMedium.setVisibility(TextView.GONE);
			holder.tvMsgSmall.setVisibility(TextView.GONE);
			holder.tvTitleForImg.setVisibility(TextView.GONE);
			Bitmap image = BitmapFactory.decodeFile(item.getPathImg());
			holder.ivShowImg.setImageBitmap(image);
			break;
		}
		
		return view;
	}
	
	static class ViewHolder
	{
		TextView tvTitle;
		TextView tvBoldTitle;
		TextView tvMsgSmall;
		TextView tvMsgMedium;
		ImageView ivShowImg;
		TextView tvTitleForImg;
	}

}
