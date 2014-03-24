package ua.maker.gbible.activity;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import ua.maker.gbible.R;
import ua.maker.gbible.constant.App;
import ua.maker.gbible.listeners.onDialogClickListener;
import ua.maker.gbible.utils.Tools;
import android.app.Activity;
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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalActivity;
import com.paypal.android.MEP.PayPalPayment;

public class DonateActivity extends SherlockActivity {
	
	private Button btnCopyToClipBoard = null;
	private Button btnPayOnPayPal;
	private ImageView ivQr = null;
	private PayPal pp;
	private EditText etMoney;
	public static final String TAG = "PayPalMethActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.donate_layout);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.background_action_bar));
		
		btnCopyToClipBoard = (Button)findViewById(R.id.btn_copy_info_donate);
		ivQr = (ImageView)findViewById(R.id.iv_qr_share);
		btnPayOnPayPal = (Button)findViewById(R.id.btn_pay_pal_donating);
		etMoney = (EditText)findViewById(R.id.et_enter_price);
		
		btnPayOnPayPal.setOnClickListener(clickPayPalDonateListener);
		btnCopyToClipBoard.setOnClickListener(clickBtnCopyListener);
		ivQr.setOnClickListener(clickIVShareListener);
	}
	
	private void updatePayPal(){
		if(Tools.checkConnection(DonateActivity.this)){
			pp = PayPal.getInstance();
			if(pp == null){
				pp = PayPal.initWithAppID(DonateActivity.this, App.PAY_PAL_KEY_LIVE, PayPal.ENV_LIVE);
				pp.setLanguage(Locale.ROOT.getLanguage());
				pp.setFeesPayer(PayPal.FEEPAYER_EACHRECEIVER);
				pp.setShippingEnabled(true);
			}
		} else {
			Tools.showToast(DonateActivity.this, getString(R.string.no_connections));
		}
	}
	
	private OnClickListener clickPayPalDonateListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String count = etMoney.getText().toString();
			updatePayPal();
			if(count.length() > 0){
				if(Tools.checkConnection(DonateActivity.this)){
					PayPalPayment payment = new PayPalPayment();
					payment.setCurrencyType(Currency.getInstance(Locale.US));
					payment.setRecipient(App.MY_EMAIL);
					payment.setSubtotal(new BigDecimal(Double.parseDouble(count)));
					payment.setPaymentType(PayPal.PAY_TYPE_SIMPLE);
					startActivityForResult(PayPal.getInstance().checkout(payment, DonateActivity.this), 105);
				} else {
					Tools.showToast(DonateActivity.this, getString(R.string.no_connections));					
				}
			} else {
				etMoney.setError(getString(R.string.enter_count));
			}
		}
	};
	
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
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		switch (resultCode) {
		case Activity.RESULT_OK:
			String payKey = intent.getStringExtra(PayPalActivity.EXTRA_PAY_KEY);
			Log.i(TAG, "RESULT_OK: " + payKey);
			Tools.showToast(getApplicationContext(), getString(R.string.tnx_for_donate));
			break;
		case Activity.RESULT_CANCELED:
			Log.i(TAG, "RESULT_CANCELED");
			break;

		case PayPalActivity.RESULT_FAILURE:
			String errorID = intent
					.getStringExtra(PayPalActivity.EXTRA_ERROR_ID);
			String errorMessage = intent
					.getStringExtra(PayPalActivity.EXTRA_ERROR_MESSAGE);
			Log.i(TAG, "RESULT_FAILURE: errId: "+errorID+" |ErrMsg: "+errorMessage);
			Tools.showToast(getApplicationContext(), "ERROR - латеж не проведен!");
		}
	};
}
