package ua.maker.gbible.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ua.maker.gbible.Models.Poem;
import ua.maker.gbible.R;

/**
 * Created by daniil on 11/7/14.
 */
public class PoemAdapter extends ArrayAdapter<Poem> {

    private Context context;
    private ArrayList<Poem> data;

    public PoemAdapter(Context context, ArrayList<Poem> data) {
        super(context, R.layout.item_poem, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.item_poem, null);

            holder = new ViewHolder();
            holder.tvId = (TextView) view.findViewById(R.id.tv_poem_id);
            holder.tvContent = (TextView) view.findViewById(R.id.tv_content);

            initTypefaces(holder);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Poem item = data.get(position);
        holder.tvId.setText(item.poemId);
        holder.tvContent.setText(item.content);

        return view;
    }

    private void initTypefaces(ViewHolder holder) {

    }

    private static class ViewHolder {
        TextView tvId;
        TextView tvContent;
    }
}
