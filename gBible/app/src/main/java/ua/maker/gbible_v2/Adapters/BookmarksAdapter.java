package ua.maker.gbible_v2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
            holder.tvBookName = (TextView) view.findViewById(R.id.tv_book_name);
            holder.tvLinkPoem = (TextView) view.findViewById(R.id.tv_link_poem);
            holder.tvData = (TextView) view.findViewById(R.id.tv_data);
            holder.tvTitleComment = (TextView) view.findViewById(R.id.tv_title_comments);
            holder.tvCommentData = (TextView) view.findViewById(R.id.tv_comment_data);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        BookMark item = data.get(position);
        holder.tvBookName.setText(item.getBookName());
        holder.tvLinkPoem.setText(getLinkPoem(item));
        holder.tvData.setText(item.getContent());
        if (item.getComment() != null && !item.getComment().isEmpty()) {
            holder.tvTitleComment.setVisibility(TextView.VISIBLE);
            holder.tvCommentData.setVisibility(TextView.VISIBLE);
            holder.tvCommentData.setText(item.getComment());
        } else {
            holder.tvTitleComment.setVisibility(TextView.GONE);
            holder.tvCommentData.setVisibility(TextView.GONE);
        }

        return view;
    }

    private String getLinkPoem(BookMark item) {
        return String.valueOf(item.getChapter()) + ":" + item.getPoem();
    }

    private static class ViewHolder {
        TextView tvBookName;
        TextView tvLinkPoem;
        TextView tvData;
        TextView tvTitleComment;
        TextView tvCommentData;
    }
}
