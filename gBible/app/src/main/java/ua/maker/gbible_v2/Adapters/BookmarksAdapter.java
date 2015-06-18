package ua.maker.gbible_v2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import ua.maker.gbible_v2.Models.BookMark;
import ua.maker.gbible_v2.R;

/**
 * Created by Daniil on 18.06.2015.
 */
public class BookmarksAdapter extends ArrayAdapter<BookMark> {

    private Context context;
    private ArrayList<BookMark> data;

    public BookmarksAdapter(Context context, ArrayList<BookMark> data) {
        super(context, R.layout.item_bookmark, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.item_bookmark, null);

            holder = new ViewHolder();

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        return view;
    }

    private static class ViewHolder {

    }
}
