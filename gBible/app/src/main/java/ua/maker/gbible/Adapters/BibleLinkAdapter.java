package ua.maker.gbible.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ua.maker.gbible.Models.BibleLink;
import ua.maker.gbible.R;

/**
 * Created by daniil on 11/7/14.
 */
public class BibleLinkAdapter extends ArrayAdapter<BibleLink> {

    private Context context;
    private ArrayList<BibleLink> data;

    public BibleLinkAdapter(Context context, ArrayList<BibleLink> data) {
        super(context, R.layout.item_bible_link, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.item_bible_link, null);

            holder = new ViewHolder();
            holder.tvName = (TextView) view.findViewById(R.id.tv_name);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        BibleLink item = data.get(position);
        holder.tvName.setText(item.name);

        return view;
    }

    private static class ViewHolder {
        TextView tvName;
    }
}