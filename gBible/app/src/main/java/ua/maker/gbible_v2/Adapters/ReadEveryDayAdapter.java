package ua.maker.gbible_v2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import ua.maker.gbible_v2.DataBases.BibleDB;
import ua.maker.gbible_v2.Models.ItemReadDay;
import ua.maker.gbible_v2.R;

/**
 * Created by Daniil on 04.08.2015.
 */
public class ReadEveryDayAdapter extends ArrayAdapter<ItemReadDay> implements StickyListHeadersAdapter{

    private Context context;
    private ArrayList<ItemReadDay> data;
    private BibleDB bibleDB;

    public ReadEveryDayAdapter(Context context, ArrayList<ItemReadDay> data, BibleDB bibleDB) {
        super(context, R.layout.item_read_for_every_day, data);
        this.context = context;
        this.data = data;
        this.bibleDB = bibleDB;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.item_read_for_every_day, null);

            holder = new ViewHolder();
            holder.tvOldTestament = (TextView) view.findViewById(R.id.tv_old_testament);
            holder.tvNewTestament = (TextView) view.findViewById(R.id.tv_new_testament);
            holder.tbStatus = (ToggleButton) view.findViewById(R.id.tb_status);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final ItemReadDay item = data.get(position);
        holder.tvNewTestament.setText(item.getContentChapterNewTFull());
        holder.tvOldTestament.setText(item.getContentChapterOldTFull());
        holder.tbStatus.setChecked(item.isStatusReaded());
        holder.tbStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //bibleDB.setStatusItemReadForEveryDay(position, isChecked);
            }
        });

        return view;
    }

    @Override
    public View getHeaderView(int position, View view, ViewGroup viewGroup) {
        ViewHeaderHolder holder;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.item_header, null);

            holder = new ViewHeaderHolder();
            holder.tvMonth = (TextView) view.findViewById(R.id.tv_month);

            view.setTag(holder);
        } else {
            holder = (ViewHeaderHolder) view.getTag();
        }

        ItemReadDay item = data.get(position);
        holder.tvMonth.setText(item.getMonth());

        return view;
    }

    @Override
    public long getHeaderId(int i) {
        return (long)i;
    }

    private static class ViewHolder{
        TextView tvOldTestament;
        TextView tvNewTestament;
        ToggleButton tbStatus;
    }

    private static class ViewHeaderHolder{
        TextView tvMonth;
    }
}
