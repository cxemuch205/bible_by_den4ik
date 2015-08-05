package ua.maker.gbible_v2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ua.maker.gbible_v2.BaseActivity;
import ua.maker.gbible_v2.Constants.App;
import ua.maker.gbible_v2.DataBases.UserDB;
import ua.maker.gbible_v2.Models.BookMark;
import ua.maker.gbible_v2.R;

/**
 * Created by Daniil on 18.06.2015.
 */
public class BookmarksAdapter extends ArrayAdapter<BookMark> {

    private Context context;
    private ArrayList<BookMark> data;
    private Animation animToUp, animToDown;
    private UserDB userDB;
    private LocalBroadcastManager broadcastManager;

    public BookmarksAdapter(Context context, ArrayList<BookMark> data, UserDB userDB) {
        super(context, R.layout.item_bookmark, data);
        this.context = context;
        this.data = data;
        broadcastManager = LocalBroadcastManager.getInstance(context);
        animToUp = AnimationUtils.loadAnimation(context, R.anim.translate_to_up);
        animToDown = AnimationUtils.loadAnimation(context, R.anim.translate_to_down);
        this.userDB = userDB;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
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
            holder.llMenuOptions = (LinearLayout) view.findViewById(R.id.ll_menu_options);

            holder.optionMenu = new ViewHolder.OptionMenu();
            holder.optionMenu.btnDel = (Button) holder.llMenuOptions.findViewById(R.id.btn_del);
            holder.optionMenu.btnOpen = (Button) holder.llMenuOptions.findViewById(R.id.btn_open);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final BookMark item = data.get(position);
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

        showHideOptionMenu(holder, item);

        return view;
    }

    private void showHideOptionMenu(ViewHolder holder, final BookMark bookMark) {
        if (bookMark.isShowMenu) {
            if (holder.llMenuOptions.getVisibility() == LinearLayout.GONE)
                holder.llMenuOptions.startAnimation(animToUp);
            holder.llMenuOptions.setVisibility(LinearLayout.VISIBLE);
            holder.optionMenu.btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userDB.deleteBookmark(bookMark, true)) {
                        remove(bookMark);
                        notifyDataSetChanged();
                        sendUpdateStatusEmpty();
                    }
                }
            });
            holder.optionMenu.btnOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((BaseActivity) context).openReadContent(bookMark.getBookId(), bookMark.getChapter() - 1, bookMark.getPoem() + 1);
                }
            });
        } else {
            if (holder.llMenuOptions.getVisibility() == LinearLayout.VISIBLE)
                holder.llMenuOptions.startAnimation(animToDown);
            holder.llMenuOptions.setVisibility(LinearLayout.GONE);
            holder.optionMenu.btnDel.setOnClickListener(null);
            holder.optionMenu.btnOpen.setOnClickListener(null);
        }
    }

    private void sendUpdateStatusEmpty() {
        Intent data = new Intent(App.Actions.UPDATE_BOOKMARKS);
        broadcastManager.sendBroadcast(data);
    }

    private String getLinkPoem(BookMark item) {
        return String.valueOf(item.getChapter()) + ":" + item.getPoem();
    }

    public void toggleMenuByItem(BookMark bookMark) {
        if (bookMark != null) {
            bookMark.isShowMenu = !bookMark.isShowMenu;
            setDefaultOtherItems(bookMark);
        } else try {
            throw new Throwable("BOOKMARK is NULL");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void setDefaultOtherItems(BookMark bookMark) {
        for (BookMark bk : data) {
            if (bookMark == null || !bk.getDbxId().equals(bookMark.getDbxId())) {
                bk.isShowMenu = false;
            }
        }
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView tvBookName;
        TextView tvLinkPoem;
        TextView tvData;
        TextView tvTitleComment;
        TextView tvCommentData;
        LinearLayout llMenuOptions;
        OptionMenu optionMenu;

        static class OptionMenu{
            Button btnDel;
            Button btnOpen;
        }
    }
}
