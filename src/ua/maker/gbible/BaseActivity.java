package ua.maker.gbible;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class BaseActivity extends SherlockFragmentActivity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (getSupportActionBar() != null) {
			getSupportActionBar().setHomeButtonEnabled(true);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    	switch (item.getItemId()) {
			case android.R.id.home:
				
    			Toast.makeText(getApplicationContext(), "Developer by Den4ik", Toast.LENGTH_SHORT).show();
    			
    			return true;
        }
		
        return super.onOptionsItemSelected(item);
    }
}
