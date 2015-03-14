package ua.maker.gbible_v2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ua.maker.gbible_v2.Constants.App;
import ua.maker.gbible_v2.Models.Poem;
import ua.maker.gbible_v2.R;

/**
 * Created by daniil on 11/7/14.
 */
public class PoemAdapter extends ArrayAdapter<Poem> {

    private Context context;
    private ArrayList<Poem> data;
    private SharedPreferences pref;
    private float textSize = 16;

    public PoemAdapter(Context context, ArrayList<Poem> data) {
        super(context, R.layout.item_poem, data);
        this.context = context;
        this.data = data;
        pref = context.getSharedPreferences(App.Pref.NAME, 0);
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
        holder.tvId.setText(String.valueOf(item.poem));
        holder.tvContent.setText(item.content);

        textSize = pref.getInt(App.Pref.POEM_TEXT_SIZE, 16);

        holder.tvContent.setTextSize(textSize);
        holder.tvId.setTextSize(textSize);

        return view;
    }

    private void initTypefaces(ViewHolder holder) {

    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    private static class ViewHolder {
        TextView tvId;
        TextView tvContent;
    }
}
