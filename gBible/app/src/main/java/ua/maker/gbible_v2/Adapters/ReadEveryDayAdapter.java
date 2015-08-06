package ua.maker.gbible_v2.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import ua.maker.gbible_v2.Constants.App;
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
    private SharedPreferences prefs;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat simpleDateFormat
            = new SimpleDateFormat("D");
    private int day = 0;

    public ReadEveryDayAdapter(Context context, ArrayList<ItemReadDay> data, BibleDB bibleDB) {
        super(context, R.layout.item_read_for_every_day, data);
        this.context = context;
        this.data = data;
        this.bibleDB = bibleDB;
        prefs = context.getSharedPreferences(App.Pref.NAME, Context.MODE_PRIVATE);
        day = Integer.parseInt(simpleDateFormat.format(new Date()));
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
            holder.tvDay = (TextView) view.findViewById(R.id.tv_day);
            holder.tbStatus = (ToggleButton) view.findViewById(R.id.tb_status);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final ItemReadDay item = data.get(position);
        putInfo(item, holder);
        holder.tvDay.setText(String.valueOf(item.getDay()));
        holder.tbStatus.setChecked(item.isStatusReaded());
        holder.tbStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setStatus(!item.isStatusReaded());
                bibleDB.setStatusItemReadForEveryDay(position,
                        item.getDbxId(), item.isStatusReaded());
                if (item.isStatusReaded()) {
                    prefs.edit().putInt(App.Pref.LAST_RED_POSITION, position).apply();
                }
            }
        });

        if ((day - 1) == position) {
            view.setBackgroundColor(Color.parseColor("#5500af64"));
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        return view;
    }

    private void putInfo(ItemReadDay item, ViewHolder holder) {
        String poemOldLink = "";
        boolean noStandartOld = false;
        for(int i = 0; i < item.getListPoemOld().size(); i++)
            if(item.getListPoemOld().get(i).poemTo != 0){
                noStandartOld = true;
                break;
            }
        for(int i = 0; i < item.getListPoemOld().size(); i++){
            if(!noStandartOld){
                poemOldLink += item.getListPoemOld().get(i).bookName+" "
                        +item.getListPoemOld().get(i).chapter;
            }
            else
            {
                poemOldLink += item.getListPoemOld().get(i).bookName+" "
                        +item.getContentChapterOldTFull();
            }
            if(i != item.getListPoemOld().size()-1)
                poemOldLink += "\n";
        }
        String poemNewLink = "";
        boolean noStandartNew = false;
        for(int i = 0; i < item.getListPoemNew().size(); i++)
            if(item.getListPoemNew().get(i).poemTo != 0){
                noStandartNew = true;
                break;
            }
        for(int i = 0; i < item.getListPoemNew().size(); i++){
            if(!noStandartNew){
                poemNewLink += item.getListPoemNew().get(i).bookName+" "
                        +item.getListPoemNew().get(i).chapter;
            }
            else
            {
                poemNewLink += item.getListPoemNew().get(i).bookName+" "
                        +item.getContentChapterNewTFull();
            }

            if(i != item.getListPoemNew().size()-1)
                poemNewLink += "\n";
        }

        holder.tvNewTestament.setText(String.valueOf(poemNewLink));
        holder.tvOldTestament.setText(String.valueOf(poemOldLink));
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
    public long getHeaderId(int position) {
        return data.get(position).getMonth().contains("Ию") ?
                data.get(position).getMonth().charAt(2) :
                data.get(position).getMonth().charAt(1);
    }

    private static class ViewHolder{
        TextView tvOldTestament;
        TextView tvNewTestament;
        TextView tvDay;
        ToggleButton tbStatus;
    }

    private static class ViewHeaderHolder{
        TextView tvMonth;
    }
}
