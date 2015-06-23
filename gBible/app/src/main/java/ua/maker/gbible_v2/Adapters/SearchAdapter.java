package ua.maker.gbible_v2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ua.maker.gbible_v2.Models.Poem;
import ua.maker.gbible_v2.R;

/**
 * Created by Daniil on 23.06.2015.
 */
public class SearchAdapter extends ArrayAdapter<Poem> {

    private Context context;
    private ArrayList<Poem> data;

    public SearchAdapter(Context context, ArrayList<Poem> data) {
        super(context, R.layout.item_search_result, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.item_search_result, null);

            holder = new ViewHolder();
            holder.tvBookName = (TextView) view.findViewById(R.id.tv_book_name);
            holder.tvLink = (TextView) view.findViewById(R.id.tv_link);
            holder.tvContent = (TextView) view.findViewById(R.id.tv_content);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Poem item = data.get(position);
        holder.tvBookName.setText(item.bookName);
        holder.tvLink.setText(buildLink(item));
        holder.tvContent.setText(item.content);

        return view;
    }

    private String buildLink(Poem item) {
        return String.valueOf(item.chapter + ":" + item.poem);
    }

    private static class ViewHolder {
        TextView tvBookName;
        TextView tvLink;
        TextView tvContent;
    }
}
