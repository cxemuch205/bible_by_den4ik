package ua.maker.gbible_v2.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by daniilpavenko on 10/2/15.
 */
public abstract class ItemCursorAdapter extends BaseAdapter {

    private int mSize = 0;
    private Cursor mCursor = null;
    private final DataSource dataSource;
    private final Context context;

    public ItemCursorAdapter(Context context, DataSource dataSource) {
        this.dataSource = dataSource;
        this.context = context;
        doQuery();
    }

    private void doQuery() {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = dataSource.getRowIds();
        mSize = mCursor.getCount();
    }

    @Override
    public int getCount() {
        return mSize;
    }

    @Override
    public Object getItem(int position) {
        if (mCursor.moveToPosition(position)) {
            long rawId = mCursor.getLong(0); // 0 - it is ID for field _id
            return dataSource.getRowsById(rawId);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (mCursor.moveToPosition(position)) {
            return mCursor.getLong(0);
        } else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        mCursor.moveToPosition(position);
        long rawId = mCursor.getLong(0);
        Cursor cursor = dataSource.getRowsById(rawId);
        cursor.moveToFirst();
        View v;
        if (view == null) {
            v = newView(context, cursor, parent);
        } else {
            v = view;
        }

        bindView(v, context, cursor);
        cursor.close();

        return v;
    }

    @Override
    public void notifyDataSetChanged() {
        doQuery();
        super.notifyDataSetChanged();
    }

    public abstract View newView(Context context, Cursor cursor, ViewGroup parent);
    public abstract void bindView(View view, Context context, Cursor cursor);

    public interface DataSource {
        Cursor getRowIds();

        Cursor getRowsById(long rowId);
    }
}
