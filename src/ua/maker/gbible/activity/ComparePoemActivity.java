package ua.maker.gbible.activity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.adapter.ItemListComparePoemsAdapter;
import ua.maker.gbible.constant.App;
import ua.maker.gbible.structs.PoemStruct;
import ua.maker.gbible.utils.DataBase;
import ua.maker.gbible.utils.Tools;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;
import com.google.analytics.tracking.android.EasyTracker;

public class ComparePoemActivity extends SherlockActivity {
	
	public static final String TAG = "ComparePoemActivity";
	
	private TextView tvShowLink = null;
	private ListView lvCompare = null;
	private DataBase db = null;
	private List<PoemStruct> listPoem;
	private WeakReference<LoadComparedPoemsTask> taskWeakRef;
	private ItemListComparePoemsAdapter adapter;
	
	private int bookId = 1, chapter = 1, poem = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_compare_poems);
		getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.background_action_bar));
		tvShowLink = (TextView)findViewById(R.id.tv_show_link);
		lvCompare = (ListView)findViewById(R.id.lv_compare_poems);
		
		listPoem = new ArrayList<PoemStruct>();
		adapter = new ItemListComparePoemsAdapter(
				ComparePoemActivity.this,
				listPoem);
		lvCompare.setAdapter(adapter);
		
		db = new DataBase(ComparePoemActivity.this);
		try {
			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		db.openDataBase();
		
		bookId = getIntent().getIntExtra(App.BOOK_ID, 1);
		chapter = getIntent().getIntExtra(App.CHAPTER, 1);
		poem = getIntent().getIntExtra(App.POEM, 1);
		
		tvShowLink.setText(""
					+ Tools.getBookNameByBookId(bookId, ComparePoemActivity.this)
					+ " " + chapter + ":" + poem);
		
		loadDataCompared();
	}
	
	private void loadDataCompared(){
		LoadComparedPoemsTask task = new LoadComparedPoemsTask(ComparePoemActivity.this);
		this.taskWeakRef = new WeakReference<ComparePoemActivity.LoadComparedPoemsTask>(task);
		task.execute(new Integer[]{bookId, chapter, poem});
	}
	
	private static class LoadComparedPoemsTask extends AsyncTask<Integer, Void, List<PoemStruct>>{

		private WeakReference<ComparePoemActivity> activityWeakRef;
		
		public LoadComparedPoemsTask(ComparePoemActivity activity){
			this.activityWeakRef = new WeakReference<ComparePoemActivity>(activity);
			activityWeakRef.get().setSupportProgressBarIndeterminateVisibility(true);
		}
		
		@Override
		protected List<PoemStruct> doInBackground(Integer... params) {
			Log.i(TAG, "doInBackground()");
			List<PoemStruct> result;
		
			result = activityWeakRef.get().db.getPoemCompare(params[0], params[1], params[2]);
			
			return result;
		}
		
		@Override
		protected void onPostExecute(List<PoemStruct> result) {
			super.onPostExecute(result);
			if(activityWeakRef.get() != null){
				activityWeakRef.get().listPoem.addAll(result);
				activityWeakRef.get().adapter.notifyDataSetChanged();
				activityWeakRef.get().setSupportProgressBarIndeterminateVisibility(false);
			}
		}
		
	}

    @Override
    protected void onStart() {
    	super.onStart();
    	EasyTracker.getInstance().activityStart(this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	EasyTracker.getInstance().activityStop(this);
    }
}
