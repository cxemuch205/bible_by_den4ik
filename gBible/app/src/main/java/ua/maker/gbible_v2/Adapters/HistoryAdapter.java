package ua.maker.gbible_v2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ua.maker.gbible_v2.Helpers.Tools;
import ua.maker.gbible_v2.Models.History;
import ua.maker.gbible_v2.R;

/**
 * Created by Daniil on 22.06.2015.
 */
public class HistoryAdapter extends ArrayAdapter<History> {

    private Context context;
    private ArrayList<History> data;

    public HistoryAdapter(Context context, ArrayList<History> data) {
        super(context, R.layout.item_history, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.item_history, null);

            holder = new ViewHolder();
            holder.tvTranslate = (TextView) view.findViewById(R.id.tv_translate);
            holder.tvLink = (TextView) view.findViewById(R.id.tv_link_h);
            holder.tvDateCreated = (TextView) view.findViewById(R.id.tv_date_created);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        History item = data.get(position);
        String translate = Tools.getTranslateWitchPreferences(item.getTranslate(), context);
        holder.tvTranslate.setText(translate);
        holder.tvLink.setText(buildLink(item));
        holder.tvDateCreated.setText(item.getDateCreated());

        return view;
    }

    private String buildLink(History item) {
        return String.valueOf(item.getBookName() + " " + item.getChapter());
    }

    private static class ViewHolder {
        TextView tvTranslate;
        TextView tvLink;
        TextView tvDateCreated;
    }
}
