package ua.maker.gbible.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.activity.SettingActivity;
import ua.maker.gbible.adapter.ItemPlanListAdapter;
import ua.maker.gbible.constant.App;
import ua.maker.gbible.listeners.onDialogClickListener;
import ua.maker.gbible.structs.PlanStruct;
import ua.maker.gbible.utils.Tools;
import ua.maker.gbible.utils.UserDB;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

@SuppressLint("ValidFragment")
public class PlansListFragment extends SherlockFragment {
	
	private static final String TAG = "PlanFragment";
	
	private static final int CMB_EDIT = 1001;
	private static final int CMB_DELETE = 1002;
	
	private View view = null;
	private Button btnCreatePlan = null;
	private Button btnDialogOK = null;
	private ListView lvPlans = null;
	private EditText etName = null;
	private EditText etSubDescription = null;
	private TextView tvShowDataTime = null;
	
	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat dataFormat = new SimpleDateFormat("kk:mm yyyy/MMM/D");
	
	private ItemPlanListAdapter adapter = null;
	private List<PlanStruct> listPlans = null;
	private UserDB db = null;
	private SharedPreferences pref = null;
	
	private AlertDialog dialog = null;
	private boolean addEdit = true; //add - true | edit - false
	
	private int itemSelect = 0;
	
	private static PlansListFragment instance;
	
	private PlansListFragment(){};
	
	public static PlansListFragment getInstence() {
		if(instance == null){
			instance = new PlansListFragment();
		}
		return instance;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		if(db == null){
			db = new UserDB(getSherlockActivity());
			
			listPlans = new ArrayList<PlanStruct>();
			adapter = new ItemPlanListAdapter(getSherlockActivity(), listPlans);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
			builder.setTitle(getString(R.string.dialog_title_create_plan));
			
			LayoutInflater inflater = getSherlockActivity().getLayoutInflater();
			View viewDialog = inflater.inflate(R.layout.dialog_add_plan_settings, null);
			
			etName = (EditText)viewDialog.findViewById(R.id.et_name_add_plan);
			etSubDescription = (EditText)viewDialog.findViewById(R.id.et_description_add_plan);
			tvShowDataTime = (TextView)viewDialog.findViewById(R.id.tv_show_data_time);
			etName.addTextChangedListener(textChangeNameListener);
			
			builder.setView(viewDialog);
			builder.setPositiveButton(getString(R.string.create_plan_btn), clickDialogOkListener);
			builder.setNegativeButton(getString(R.string.dialog_cancel), new onDialogClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			
			dialog = builder.create();
			pref = getSherlockActivity().getSharedPreferences(App.PREF_SEND_DATA, 0);
		}		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView()");
		view = inflater.inflate(R.layout.activity_plan_layout, null);
		btnCreatePlan = (Button)view.findViewById(R.id.btn_create_plan);
		lvPlans = (ListView)view.findViewById(R.id.lv_list_plans);
		lvPlans.setAdapter(adapter);
		
		if(!getSherlockActivity().getSupportActionBar().isShowing())
			getSherlockActivity().getSupportActionBar().show();
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(lvPlans);
		
		getSherlockActivity().getActionBar().setTitle(getString(R.string.title_activit_plan));
		
		btnCreatePlan.setOnClickListener(clickAddNewPlanListener);
		lvPlans.setOnItemClickListener(itemClickListener);
		lvPlans.setOnItemLongClickListener(itemLongClickListener);
		
		listPlans.clear();
		listPlans.addAll(db.getPlansList());
		
		getSherlockActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
	}
	
	private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
			Log.d(TAG, "longItemClickLIstener(): " + position);
			itemSelect = position;
			return false;
		}
	};
	
	private OnClickListener clickAddNewPlanListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			dialog.show();
			addEdit = true;
			btnDialogOK = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
			btnDialogOK.setVisibility(Button.GONE);
			etName.setText("");
			etSubDescription.setText("");
			tvShowDataTime.setText(""+dataFormat.format(new Date()));
		}
	};
	
	private DialogInterface.OnClickListener clickDialogOkListener = new onDialogClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(addEdit)
			{
				PlanStruct plan = new PlanStruct();
				plan.setName(etName.getText().toString());
				plan.setSubDescription(""+etSubDescription.getText().toString());
				plan.setDate(""+tvShowDataTime.getText());
				
				int idPlan = db.insertPlan(plan);
				
				Log.d(TAG, "Create plan - OK - planId: " + idPlan);
				
				plan.setId(idPlan);
				listPlans.add(plan);
				updateListPlans();
			}
			else
			{
				Log.d(TAG, "EDIT plan - OK - item select: " + (itemSelect+1));
				PlanStruct plan = new PlanStruct();
				plan.setName(etName.getText().toString());
				plan.setSubDescription(""+etSubDescription.getText().toString());
				plan.setDate(""+tvShowDataTime.getText());
				plan.setId((itemSelect+1));
				
				listPlans.remove(itemSelect);
				listPlans.add(itemSelect, plan);
				db.updatePlan(plan);
				updateListPlans();
			}
			
			dialog.cancel();
		}
	};
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			itemSelect = position;
			pref.edit().putInt(App.PLAN_ID, listPlans.get(position).getId()).commit();
			getFragmentManager().beginTransaction()
			.replace(R.id.flRoot, (getFragmentManager()
					.findFragmentByTag(App.TAG_FRAGMENT_PLAN_DETAIL) != null)?
							getFragmentManager()
							.findFragmentByTag(App.TAG_FRAGMENT_PLAN_DETAIL): PlanDetailFragment.getInstance(), App.TAG_FRAGMENT_PLAN_DETAIL).commit();
		}
	};
	
	private TextWatcher textChangeNameListener = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if(etName.getText().toString().length() == 0) {
				btnDialogOK.setVisibility(Button.GONE);
			} else {
				btnDialogOK.setVisibility(Button.VISIBLE);
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {}		
		@Override
		public void afterTextChanged(Editable s) {}
	};
	
	private void updateListPlans() {
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_main, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(Menu.NONE, CMB_EDIT, Menu.NONE, getString(R.string.context_edit));
		menu.add(Menu.NONE, CMB_DELETE, Menu.NONE, getString(R.string.context_delete));
	}
	
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case CMB_EDIT:
			addEdit = false;
			dialog.show();
			if(btnDialogOK == null){
				btnDialogOK = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
			}
			PlanStruct plan = new PlanStruct();
			plan = listPlans.get(itemSelect);
			etName.setText(plan.getName());
			etSubDescription.setText(plan.getSubDescription());
			tvShowDataTime.setText(""+plan.getDate());
			btnDialogOK.setText(getString(R.string.dialog_save));
			
			return true;
		case CMB_DELETE:
			db.deletePlan(listPlans.get(itemSelect).getId());
			Tools.showToast(getSherlockActivity(), 
					String.format(getString(R.string.toast_delete_plan_msg), 
							listPlans.get(itemSelect).getName()));
			listPlans.remove(itemSelect);
			updateListPlans();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.action_exit:
	   		getSherlockActivity().finish();
	   		return true;
	   	case R.id.action_setting_app:
	   		Intent startSetting = new Intent(getSherlockActivity(), SettingActivity.class);
			startActivity(startSetting);
	   		return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(getSherlockActivity());
	}
	
	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(getSherlockActivity());
	}
}
