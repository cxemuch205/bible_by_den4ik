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

/**
 * Created by Daniil on 05.08.2015.
 */
public class SimpleDialogReadAdapter extends ArrayAdapter<Poem> {

    private Context context;
    private ArrayList<Poem> data;

    public SimpleDialogReadAdapter(Context context, ArrayList<Poem> data) {
        super(context, android.R.layout.simple_list_item_1, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        TextView tvData;
        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(android.R.layout.simple_list_item_1, null);

            tvData = (TextView)view;
            view.setTag(tvData);
        } else {
            tvData = (TextView) view.getTag();
        }

        Poem item = data.get(position);
        if (item.poemTo == 0) {
            tvData.setText(item.bookName + " " + item.chapter);
        } else {
            tvData.setText(item.bookName + " "
                    + item.chapter + ":" + item.poem
                    + "-" + item.poemTo);
        }
        return view;
    }
}
