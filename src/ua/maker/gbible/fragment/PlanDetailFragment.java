package ua.maker.gbible.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.adapter.PlanItemAdapter;
import ua.maker.gbible.constant.App;
import ua.maker.gbible.constant.PlanData;
import ua.maker.gbible.listeners.onDialogClickListener;
import ua.maker.gbible.structs.ItemPlanStruct;
import ua.maker.gbible.structs.PlanStruct;
import ua.maker.gbible.utils.DataBase;
import ua.maker.gbible.utils.Tools;
import ua.maker.gbible.utils.UserDB;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

@SuppressLint("ValidFragment")
public class PlanDetailFragment extends SherlockFragment {
	
	private static final String TAG = "PlanDetailFragment";
	
	private static final int BTN_DRAP_AND_DROP = 100;
	private static final int BTN_EDIT = 101;
	private static final int BTN_DELETE = 102;
	
	private View view = null;
	private SharedPreferences pref = null;
	private UserDB db = null;
	private DataBase dbBible = null;
	private PlanStruct plan = null;
	private AlertDialog dialog = null;
	private ItemPlanStruct backUpItem = null;
	
	private ListView lvPlanItems = null;
	private List<ItemPlanStruct> listItemsPlan = null;
	private PlanItemAdapter adapter = null;
	
	private int planId = 1;
	private int numberOfPoem = 0;
	private int posStartItemDrag = 0;
	private int itemSelected = 0;
	
	private boolean pasteItem = false;
	private boolean isEdit = false;
	private boolean check = false;
	
	private MenuItem itemCancel = null;
	
	private RadioButton rbIsText = null;
	private RadioButton rbIsLink = null;
	
	private LinearLayout llText = null;
	private EditText etTextItem = null;
	private CheckBox cbBoldText = null;
	
	private LinearLayout llLink = null;
	private Spinner spinnerBooks = null;
	private Spinner spinnerChapter = null;
	private Spinner spinnerPoem = null;
	private Spinner spinnerToPoem = null;
	private CheckBox cbQuotePoem = null;
	
	private ArrayAdapter<String> adapterBooks = null;
	private ArrayAdapter<Integer> adapterChapters = null;
	private ArrayAdapter<Integer> adapterPoem = null;
	private ArrayAdapter<Integer> adapterToPoem = null;
	
	private List<String> listBooks = null;
	private List<Integer> listChapter = null;
	private List<Integer> listPoem = null;
	private List<Integer> listToPoem = null;
	
	private static PlanDetailFragment instance;
	
	private PlanDetailFragment(){};
	
	public static PlanDetailFragment getInstance() {
		if(instance == null){
			instance = new PlanDetailFragment();
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
			listItemsPlan = new ArrayList<ItemPlanStruct>();
			pref = getSherlockActivity().getSharedPreferences(App.PREF_SEND_DATA, 0);
			adapter = new PlanItemAdapter(getSherlockActivity(), listItemsPlan);
			dbBible = new DataBase(getSherlockActivity());
			
			try {
				dbBible.createDataBase();
			} catch (IOException e) {e.printStackTrace();}
			
			dbBible.openDataBase();
			
			listBooks = new ArrayList<String>();
			listChapter = new ArrayList<Integer>();
			listPoem = new ArrayList<Integer>();
			listToPoem = new ArrayList<Integer>();
			String[] books = getResources().getStringArray(R.array.array_books);
			for(int i = 0; i < books.length; i++){
				listBooks.add(""+books[i]);
			}		 
			
			adapterBooks = new ArrayAdapter<String>(getSherlockActivity(), android.R.layout.simple_list_item_1, listBooks);
			adapterChapters = new ArrayAdapter<Integer>(getSherlockActivity(), android.R.layout.simple_list_item_1, listChapter);
			adapterPoem = new ArrayAdapter<Integer>(getSherlockActivity(), android.R.layout.simple_list_item_1, listPoem);
			adapterToPoem = new ArrayAdapter<Integer>(getSherlockActivity(), android.R.layout.simple_list_item_1, listToPoem);
			
			adapterBooks.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			adapterChapters.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			adapterPoem.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			adapterToPoem.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
			builder.setTitle(getString(R.string.dialog_title_add_item_plan));
			
			LayoutInflater inflater = getSherlockActivity().getLayoutInflater();
			View viewDialog = inflater.inflate(R.layout.dialog_add_item_to_plan, null);
			
			rbIsText = (RadioButton)viewDialog.findViewById(R.id.rb_text);
			rbIsLink = (RadioButton)viewDialog.findViewById(R.id.rb_link);
			
			llText = (LinearLayout)viewDialog.findViewById(R.id.ll_add_text_item);
			etTextItem = (EditText)viewDialog.findViewById(R.id.et_text_to_new_item_plan);
			cbBoldText = (CheckBox)viewDialog.findViewById(R.id.cb_set_text_bold);
			cbQuotePoem = (CheckBox)viewDialog.findViewById(R.id.cb_quote);
			
			llLink = (LinearLayout)viewDialog.findViewById(R.id.ll_add_link_item);
			spinnerBooks = (Spinner)viewDialog.findViewById(R.id.spinner_books);
			spinnerChapter = (Spinner)viewDialog.findViewById(R.id.spinner_chapter);
			spinnerPoem = (Spinner)viewDialog.findViewById(R.id.spinner_poem);
			spinnerToPoem = (Spinner)viewDialog.findViewById(R.id.spinner_to_poem);
			
			spinnerBooks.setAdapter(adapterBooks);
			spinnerChapter.setAdapter(adapterChapters);
			spinnerPoem.setAdapter(adapterPoem);
			spinnerToPoem.setAdapter(adapterToPoem);
			
			builder.setView(viewDialog);
			builder.setPositiveButton(getString(R.string.menu_title_add_point), clickAddListener);
			builder.setNegativeButton(getString(R.string.dialog_cancel), clickCancelListener);
			
			dialog = builder.create();
			
			rbIsText.setOnCheckedChangeListener(checkedIsTextChangeListener);
			rbIsLink.setOnCheckedChangeListener(checkedIsLinkChangeListener);
			spinnerBooks.setOnItemSelectedListener(itemSpinnerBooksSelectedListener);
			spinnerChapter.setOnItemSelectedListener(itemSpinnerChaptersSelectedListener);
			spinnerPoem.setOnItemSelectedListener(itemSpinnerPoemSelectedListener);
		}		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView()");
		view = inflater.inflate(R.layout.activity_plan_detail, null);
		lvPlanItems = (ListView)view.findViewById(R.id.lv_plan_items);
		lvPlanItems.setAdapter(adapter);
		
		if(!getSherlockActivity().getSupportActionBar().isShowing())
			getSherlockActivity().getSupportActionBar().show();
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(lvPlanItems);		
		
		planId = pref.getInt(App.PLAN_ID, 1);
		Log.d(TAG, "planid: " + planId);
		
		plan = db.getPlanById(planId);		
		
		getSherlockActivity().getActionBar().setTitle(getString(R.string.title_activit_plan)+": "+plan.getName());
		getSherlockActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		
		lvPlanItems.setOnItemClickListener(itemClickListener);
		lvPlanItems.setOnItemLongClickListener(itemLongClickListener);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "onStart() - getItem - planId:" + plan.getId());
		EasyTracker.getInstance().activityStart(getSherlockActivity());
		listItemsPlan.clear();
		listItemsPlan.addAll(db.getItemsPlanById(plan.getId()));
		updateList();
	}
	
	private void updateList(){
		adapter.notifyDataSetChanged();
	}
	
	private OnItemSelectedListener itemSpinnerBooksSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int position,
				long id) {
			updateListChapters(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {}
	};
	
	private void updateListChapters(int position){
		listChapter.clear();
		listChapter.addAll(dbBible.getChapters(Tools.getTranslateWitchPreferences(getSherlockActivity()), (position+1)));
		adapterChapters.notifyDataSetChanged();
	}

	private OnItemSelectedListener itemSpinnerChaptersSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int position,
				long id) {
			updateListPoem();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {}
	};
	
	private void updateListPoem(){
		listPoem.clear();
		
		numberOfPoem = dbBible.getNumberOfPoemInChapter(
				Tools.getBookIdByName(listBooks.get(spinnerBooks.getSelectedItemPosition()), getSherlockActivity()), 
				listChapter.get(spinnerChapter.getSelectedItemPosition()), 
				Tools.getTranslateWitchPreferences(getSherlockActivity()));
		for(int i = 1; i <= numberOfPoem; i++)
			listPoem.add(i);
		adapterPoem.notifyDataSetChanged();
	}
	
	private OnItemSelectedListener itemSpinnerPoemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int position,
				long id) {
			updateListToPoem();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {}
	};
	
	private void updateListToPoem(){
		listToPoem.clear();
		for(int i = (spinnerPoem.getSelectedItemPosition()+1); i <= numberOfPoem; i++)
			listToPoem.add(i);
		if(isEdit & !check){
			spinnerChapter.setSelection(backUpItem.getChapter()-1);
			spinnerPoem.setSelection(backUpItem.getPoem()-1);
			spinnerToPoem.setSelection(backUpItem.getToPoem()-1);
			check = true;
		}
		adapterToPoem.notifyDataSetChanged();
	}
	
	private OnCheckedChangeListener checkedIsTextChangeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(isChecked == true)
			{
				llLink.setVisibility(LinearLayout.GONE);
				llText.setVisibility(LinearLayout.VISIBLE);
				rbIsLink.setChecked(false);
			}
			else
			{
				llLink.setVisibility(LinearLayout.VISIBLE);
				llText.setVisibility(LinearLayout.GONE);
				rbIsLink.setChecked(true);
			}
		}
	};
	
	private OnCheckedChangeListener checkedIsLinkChangeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(isChecked == true)
			{
				llLink.setVisibility(LinearLayout.VISIBLE);
				llText.setVisibility(LinearLayout.GONE);
				rbIsText.setChecked(false);
			}
			else
			{
				llLink.setVisibility(LinearLayout.GONE);
				llText.setVisibility(LinearLayout.VISIBLE);
				rbIsText.setChecked(true);
			}
		}
	};
	
	private DialogInterface.OnClickListener clickAddListener = new onDialogClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Log.d(TAG, "Dialog OK");
			if(isEdit){
				switch (backUpItem.getDataType()) {
				case PlanData.DATA_TEXT:
					backUpItem.setText(""+etTextItem.getText().toString());
					if(cbBoldText.isChecked()){
						backUpItem.setDataType(PlanData.DATA_TEXT_BOLD);
					}
					listItemsPlan.remove(itemSelected);
					listItemsPlan.add(itemSelected, backUpItem);
					break;
				case PlanData.DATA_TEXT_BOLD:
					backUpItem.setText(""+etTextItem.getText().toString());
					if(!cbBoldText.isChecked()){
						backUpItem.setDataType(PlanData.DATA_TEXT);
					}
					listItemsPlan.remove(itemSelected);
					listItemsPlan.add(itemSelected, backUpItem);
					break;
					
				case PlanData.DATA_LINK:
				case PlanData.DATA_LINK_WITH_TEXT:
					int bookId = spinnerBooks.getSelectedItemPosition()+1;
					int chapter = listChapter.get(spinnerChapter.getSelectedItemPosition());
					int poem = listPoem.get(spinnerPoem.getSelectedItemPosition());
					int toPoem = listToPoem.get(spinnerToPoem.getSelectedItemPosition());
					backUpItem.setBookId(bookId);
					backUpItem.setBookName(Tools.getBookNameByBookId(bookId, getSherlockActivity()));
					backUpItem.setChapter(chapter);
					backUpItem.setPoem(poem);
					backUpItem.setToPoem(toPoem);
					backUpItem.setTranslate(Tools.getTranslateWitchPreferences(getSherlockActivity()));
					backUpItem.setDataType(PlanData.DATA_LINK);
					if(cbQuotePoem.isChecked()){
						backUpItem.setText(dbBible.getPoem(bookId, chapter, poem, Tools.getTranslateWitchPreferences(getSherlockActivity())));
						backUpItem.setDataType(PlanData.DATA_LINK_WITH_TEXT);
					}
					listItemsPlan.remove(itemSelected);
					listItemsPlan.add(itemSelected, backUpItem);
					break;
				}
				updateList();
			}
			else
			{
				ItemPlanStruct itemPlan = new ItemPlanStruct();
				itemPlan.setId(planId);
				
				if(rbIsText.isChecked()){
					Log.d(TAG, "New item plan is Text");
					if(etTextItem.getText().toString().length()>0){
						itemPlan.setText(""+etTextItem.getText().toString());
						if(cbBoldText.isChecked()){
							itemPlan.setDataType(PlanData.DATA_TEXT_BOLD);
						}
						else
						{
							itemPlan.setDataType(PlanData.DATA_TEXT);
						}
						listItemsPlan.add(itemPlan);
						updateList();
						etTextItem.setText("");
						
						Log.d(TAG, "Item insert - SUCCESS");
					}
					else
					{
						etTextItem.setError(""+getString(R.string.error_text_msg));
					}
				}
				else
				if(rbIsLink.isChecked()){
					Log.d(TAG, "New item plan is Link");
					int bookId = spinnerBooks.getSelectedItemPosition()+1;
					int chapter = listChapter.get(spinnerChapter.getSelectedItemPosition());
					int poem = listPoem.get(spinnerPoem.getSelectedItemPosition());
					int toPoem = listToPoem.get(spinnerToPoem.getSelectedItemPosition());
					itemPlan.setBookId(bookId);
					itemPlan.setBookName(Tools.getBookNameByBookId(bookId, getSherlockActivity()));
					itemPlan.setChapter(chapter);
					itemPlan.setPoem(poem);
					itemPlan.setToPoem(toPoem);
					itemPlan.setTranslate(Tools.getTranslateWitchPreferences(getSherlockActivity()));
					itemPlan.setDataType(PlanData.DATA_LINK);
					if(cbQuotePoem.isChecked()){
						itemPlan.setText(dbBible.getPoem(bookId, chapter, poem, Tools.getTranslateWitchPreferences(getSherlockActivity())));
						itemPlan.setDataType(PlanData.DATA_LINK_WITH_TEXT);
					}
					listItemsPlan.add(itemPlan);
					updateList();
				}
			}			
		}
	};
	
	@Override
	public void onPause() {
		super.onPause();
		for(ItemPlanStruct item : listItemsPlan){
			db.deleteItemPlan(item.getIdItem());
		}
		
		Log.d(TAG, "onPause() - insert all item in DB");
		db.insertPlanData(listItemsPlan);
	};
	
	private DialogInterface.OnClickListener clickCancelListener = new onDialogClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.cancel();
		}
	};
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			if(pasteItem){
				List<ItemPlanStruct> backupList = new ArrayList<ItemPlanStruct>();
				for(int i = 0; i < listItemsPlan.size(); i++){
					if(posStartItemDrag > position){
						if(i == position){
							backupList.add(listItemsPlan.get(posStartItemDrag));
						}
						if(i != posStartItemDrag)
							backupList.add(listItemsPlan.get(i));
					} else {
						if(i != posStartItemDrag)
							backupList.add(listItemsPlan.get(i));
						if(i == position){
							backupList.add(listItemsPlan.get(posStartItemDrag));
						}
					}
				}
				listItemsPlan.clear();
				listItemsPlan.addAll(backupList);
				pasteItem = false;
				itemCancel.setVisible(false);
				updateList();
			}
			else
			{
				switch (listItemsPlan.get(position).getDataType()) {
				case PlanData.DATA_LINK:
				case PlanData.DATA_LINK_WITH_TEXT:
					Editor editor = pref.edit();
					editor.putInt(App.BOOK_ID, listItemsPlan.get((int)id).getBookId());
					editor.putInt(App.CHAPTER, listItemsPlan.get((int)id).getChapter());
					editor.putInt(App.POEM_SET_FOCUS, listItemsPlan.get((int)id).getPoem()-1);
					editor.commit();
					
					FragmentTransaction ft = getFragmentManager().
							 beginTransaction();
					ft.replace(R.id.flRoot, ListPoemsFragment.getInstence(), App.TAG_FRAGMENT_POEMS);
					ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
					ft.addToBackStack(null);
					ft.commit();
					break;

				default:
					break;
				}
			}			
		}
	};
	
	private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View v,
				int position, long id) {
			itemSelected = position;
			return false;
		}
	};
	
	@Override
	public void onCreateContextMenu(android.view.ContextMenu menu, View v, android.view.ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(Menu.NONE, BTN_DRAP_AND_DROP, Menu.NONE, getString(R.string.context_drag_and_drop));
		menu.add(Menu.NONE, BTN_EDIT, Menu.NONE, getString(R.string.context_edit));
		menu.add(Menu.NONE, BTN_DELETE, Menu.NONE, getString(R.string.context_delete));
	};
	
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		
		switch (item.getItemId()) {
		case BTN_DRAP_AND_DROP:
			isEdit = false;
			posStartItemDrag = itemSelected;
			pasteItem = true;
			itemCancel.setVisible(true);
			Tools.showToast(getSherlockActivity(), getString(R.string.click_on_point_paste));
			return true;
		case BTN_EDIT:
			isEdit = true; check = false;
			final ItemPlanStruct itemSelectOfPlan = listItemsPlan.get(itemSelected);
			backUpItem = itemSelectOfPlan;
			try {
				dialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(getString(R.string.dialog_edit));
			} catch (Exception e) {}
			
			switch (itemSelectOfPlan.getDataType()) {
			case PlanData.DATA_TEXT:
				rbIsText.setChecked(true);
				etTextItem.setText(itemSelectOfPlan.getText());
				etTextItem.selectAll();
				cbBoldText.setChecked(false);
				dialog.show();
				rbIsLink.setEnabled(false);
				break;
			case PlanData.DATA_TEXT_BOLD:
				rbIsText.setChecked(true);
				etTextItem.setText(itemSelectOfPlan.getText());
				etTextItem.selectAll();
				cbBoldText.setChecked(true);
				dialog.show();
				rbIsLink.setEnabled(false);
				break;
				
			case PlanData.DATA_LINK:
				rbIsLink.setChecked(true);
				spinnerBooks.setSelection(itemSelectOfPlan.getBookId()-1);
				spinnerChapter.setSelection(itemSelectOfPlan.getChapter()-1);
				spinnerPoem.setSelection(itemSelectOfPlan.getPoem()-1);
				
				cbQuotePoem.setChecked(false);
				dialog.show();
				rbIsText.setEnabled(false);
				break;
			case PlanData.DATA_LINK_WITH_TEXT:
				rbIsLink.setChecked(true);
				spinnerBooks.setSelection(itemSelectOfPlan.getBookId()-1);
				spinnerChapter.setSelection(itemSelectOfPlan.getChapter()-1);
				spinnerPoem.setSelection(itemSelectOfPlan.getPoem()-1);
				
				cbQuotePoem.setChecked(true);
				dialog.show();
				rbIsText.setEnabled(false);
				break;
			}
			return true;
		case BTN_DELETE:
			isEdit = false;
			db.deleteItemPlan(listItemsPlan.get(itemSelected).getIdItem());
			listItemsPlan.remove(itemSelected);
			updateList();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_plan_detail, menu);
		itemCancel = menu.getItem(1);
		itemCancel.setVisible(false);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case android.R.id.home:
			pref.edit().putInt(App.PLAN_ID, -1).commit();
			getFragmentManager().beginTransaction()
			.replace(R.id.flRoot, (getFragmentManager()
					.findFragmentByTag(App.TAG_FRAGMENT_PLAN) != null)?
							getFragmentManager()
							.findFragmentByTag(App.TAG_FRAGMENT_PLAN):PlansListFragment.getInstence(), App.TAG_FRAGMENT_PLAN).commit();
			break;
		case R.id.item_add_poin_plan:
			try {
				dialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(getString(R.string.menu_title_add_point));
			} catch (Exception e) {}
			isEdit = false;
			cbBoldText.setChecked(false);
			cbQuotePoem.setChecked(false);
			rbIsLink.setEnabled(true);
			rbIsText.setEnabled(true);
			spinnerBooks.setSelection(0);
			spinnerChapter.setSelection(0);
			spinnerPoem.setSelection(0);
			spinnerToPoem.setSelection(0);
			dialog.show();			
			break;
		case R.id.action_cancel_drag_and_drop:
			pasteItem = false;
			itemCancel.setVisible(false);
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(getSherlockActivity());
	}
}
