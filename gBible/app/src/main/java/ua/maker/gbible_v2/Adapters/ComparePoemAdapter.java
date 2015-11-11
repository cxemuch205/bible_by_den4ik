package ua.maker.gbible_v2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ua.maker.gbible_v2.Constants.App;
import ua.maker.gbible_v2.Helpers.Tools;
import ua.maker.gbible_v2.Managers.PreferenceManager;
import ua.maker.gbible_v2.Models.Poem;
import ua.maker.gbible_v2.R;

/**
 * Created by Daniil on 04.08.2015.
 */
public class ComparePoemAdapter extends ArrayAdapter<Poem> {

    private Context context;
    private ArrayList<Poem> data;
    private float textSize = App.DEFAULT_TEXT_SIZE;
    private PreferenceManager prefs;

    public ComparePoemAdapter(Context context, ArrayList<Poem> data) {
        super(context, R.layout.item_compare_poem, data);
        this.context = context;
        this.data = data;
        prefs = new PreferenceManager(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.item_compare_poem, null);

            holder = new ViewHolder();
            holder.tvNameTranslate = (TextView) view.findViewById(R.id.tv_name_translate);
            holder.tvPoem = (TextView) view.findViewById(R.id.tv_poem_id);
            holder.tvContent = (TextView) view.findViewById(R.id.tv_content);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Poem poem = data.get(position);

        textSize = prefs.getTextPoemSize();

        holder.tvContent.setTextSize(textSize);
        holder.tvPoem.setTextSize(textSize);

        if (position == 0
                || !data.get(position - 1).translateName.equals(poem.translateName)) {
            holder.tvNameTranslate.setVisibility(TextView.VISIBLE);
            holder.tvNameTranslate.setText(
                    Tools.getTranslateNameByTranslateDB(
                            poem.translateName, context));
        } else {
            holder.tvNameTranslate.setVisibility(TextView.GONE);
        }
        holder.tvPoem.setText(String.valueOf(poem.poem));
        holder.tvContent.setText(poem.content);

        return view;
    }

    private static class ViewHolder {
        TextView tvNameTranslate;
        TextView tvPoem;
        TextView tvContent;
    }
}
