package ua.maker.gbible_v2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ua.maker.gbible_v2.Constants.App;
import ua.maker.gbible_v2.Models.Poem;
import ua.maker.gbible_v2.R;
import ua.maker.gbible_v2.Views.SeveralColorsDrawable;

/**
 * Created by daniil on 11/7/14.
 */
public class PoemAdapter extends ArrayAdapter<Poem> {

    private Context context;
    private ArrayList<Poem> data;
    private SharedPreferences pref;
    private SparseBooleanArray selectedItemIds;
    private float textSize = 16;

    public PoemAdapter(Context context, ArrayList<Poem> data) {
        super(context, R.layout.item_poem, data);
        this.context = context;
        this.data = data;
        pref = context.getSharedPreferences(App.Pref.NAME, 0);
        selectedItemIds = new SparseBooleanArray();
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
            holder.vIsBookmark = view.findViewById(R.id.v_is_bookmark);

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

        if (selectedItemIds.get(position)) {
            view.setBackgroundDrawable(new SeveralColorsDrawable(
                    App.COLOR_SELECT,
                    item.colorHighlight,
                    view.getWidth(),
                    view.getHeight()));
        } else {
            view.setBackgroundColor(item.colorHighlight);
        }

        if (item.isBookmark) {
            holder.vIsBookmark.setVisibility(View.VISIBLE);
        } else {
            holder.vIsBookmark.setVisibility(View.GONE);
        }

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
        View vIsBookmark;
    }

    public void toggleSelection(int position) {
        position -= 1;
        selectView(position, !selectedItemIds.get(position));
    }

    private void selectView(int position, boolean checked) {
        if(checked)
            selectedItemIds.put(position, true);
        else
            selectedItemIds.delete(position);

        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return selectedItemIds.size();
    }

    public SparseBooleanArray getSelectedItemIds() {
        return selectedItemIds;
    }

    public ArrayList<Poem> getSelectedItems() {
        ArrayList<Poem> result = new ArrayList<Poem>();

        for (int i = 0; i < getSelectedCount(); i++) {
            result.add(data.get(selectedItemIds.keyAt(i)));
        }

        return result;
    }

    public void removeSelection() {
        selectedItemIds.clear();
        selectedItemIds = null;
        selectedItemIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }
}