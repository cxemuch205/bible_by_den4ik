package ua.maker.gbible.activity;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import ua.maker.gbible.R;
import ua.maker.gbible.listeners.onDialogClickListener;
import ua.maker.gbible.utils.Tools;

public class DonateActivity extends SherlockActivity {
	
	private Button btnCopyToClipBoard = null;
	private ImageView ivQr = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.donate_layout);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.background_action_bar));
		
		btnCopyToClipBoard = (Button)findViewById(R.id.btn_copy_info_donate);
		ivQr = (ImageView)findViewById(R.id.iv_qr_share);
		
		btnCopyToClipBoard.setOnClickListener(clickBtnCopyListener);
		ivQr.setOnClickListener(clickIVShareListener);
	}
	
	private OnClickListener clickBtnCopyListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			copyToClipBoard(getString(R.string.donate_info));
			Tools.showToast(DonateActivity.this, getString(R.string.copyed_poem));
		}
	};
	
	private OnClickListener clickIVShareListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(DonateActivity.this);
			builder.setTitle(R.string.save_image_title);
			builder.setMessage(R.string.saved_into_galary);
			builder.setNegativeButton(R.string.dialog_cancel, new onDialogClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();	
				}
			});
			builder.setPositiveButton(R.string.dialog_save, new onDialogClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ivQr.buildDrawingCache();
					Bitmap b = ivQr.getDrawingCache();
					Uri uriImage = Uri.parse(""+MediaStore.Images.Media.insertImage(getContentResolver(), b, "qr-code-Donate_gBible", "Donate for gBible"));
					
					Intent shareImage = new Intent(Intent.ACTION_SEND);
					shareImage.setType("image/*");
					shareImage.putExtra(Intent.EXTRA_STREAM, uriImage);
					dialog.cancel();
					Tools.showToast(DonateActivity.this, getString(R.string.saved_str));					
					startActivity(shareImage);
				}
			});
			AlertDialog dialogSaveImage = builder.create();
			dialogSaveImage.show();
		}
	};
	
	@SuppressWarnings("deprecation")
	private void copyToClipBoard(String textSetClip) {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
			ClipboardManager clipboard =  (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE); 
		        ClipData clip = ClipData.newPlainText(getString(R.string.app_name), textSetClip);
		        clipboard.setPrimaryClip(clip); 
		} else{
			ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE); 
		    clipboard.setText(textSetClip);
		}
		 Tools.showToast(DonateActivity.this, getString(R.string.copyed_poem));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
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
