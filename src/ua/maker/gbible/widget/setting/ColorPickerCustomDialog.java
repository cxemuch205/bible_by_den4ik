/*
 * Copyright (C) 2010 Daniel Nilsson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ua.maker.gbible.widget.setting;

import java.util.List;
import java.util.Locale;

import ua.maker.gbible.R;
import ua.maker.gbible.listeners.OnDefClickListener;
import ua.maker.gbible.structs.ColorStruct;
import ua.maker.gbible.utils.UserDB;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ColorPickerCustomDialog extends Dialog implements
		ColorPickerView.OnColorChangedListener, View.OnClickListener {

	private ColorPickerView mColorPicker;

	private LinearLayout llHistoryColor;

	private ColorPickerPanelView mOldColor;
	private ColorPickerPanelView mNewColor;
	private ColorPickerPanelView colorH1;
	private ColorPickerPanelView colorH2;
	private ColorPickerPanelView colorH3;
	private ColorPickerPanelView colorH4;
	private ColorPickerPanelView colorH5;

	private List<ColorStruct> listColorHist;
	private UserDB db;

	private EditText mHexVal;
	private boolean mHexValueEnabled = false;
	private ColorStateList mHexDefaultTextColor;

	private OnColorChangedListener mListener;

	public interface OnColorChangedListener {
		public void onColorChanged(int color, String colorHEX);
	}

	public ColorPickerCustomDialog(Context context, int initialColor) {
		super(context);

		init(initialColor);
	}

	private void init(int color) {
		getWindow().setFormat(PixelFormat.RGBA_8888);
		db = new UserDB(getContext());
		listColorHist = db.getListHistoryColorsSelect();

		setUp(color);
	}

	private void setUp(int color) {

		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View layout = inflater.inflate(R.layout.dialog_color_picker_custom, null);

		setContentView(layout);

		setTitle(R.string.dialog_color_picker);

		mColorPicker = (ColorPickerView) layout
				.findViewById(R.id.color_picker_view);
		mOldColor = (ColorPickerPanelView) layout
				.findViewById(R.id.old_color_panel);
		mNewColor = (ColorPickerPanelView) layout
				.findViewById(R.id.new_color_panel);

		llHistoryColor = (LinearLayout) layout
				.findViewById(R.id.ll_color_history);

		colorH1 = (ColorPickerPanelView) layout.findViewById(R.id.last_color_1);
		colorH2 = (ColorPickerPanelView) layout.findViewById(R.id.last_color_2);
		colorH3 = (ColorPickerPanelView) layout.findViewById(R.id.last_color_3);
		colorH4 = (ColorPickerPanelView) layout.findViewById(R.id.last_color_4);
		colorH5 = (ColorPickerPanelView) layout.findViewById(R.id.last_color_5);

		mHexVal = (EditText) layout.findViewById(R.id.hex_val);
		mHexVal.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		mHexDefaultTextColor = mHexVal.getTextColors();

		if (listColorHist.size() > 0) {
			llHistoryColor.setVisibility(LinearLayout.VISIBLE);
			updateLastColorsPanel();
		} else {
			llHistoryColor.setVisibility(LinearLayout.GONE);
		}
		
		colorH1.setOnClickListener(colorHistiryClickListener);
		colorH2.setOnClickListener(colorHistiryClickListener);
		colorH3.setOnClickListener(colorHistiryClickListener);
		colorH4.setOnClickListener(colorHistiryClickListener);
		colorH5.setOnClickListener(colorHistiryClickListener);

		mHexVal.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					InputMethodManager imm = (InputMethodManager) v
							.getContext().getSystemService(
									Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					String s = mHexVal.getText().toString();
					if (s.length() > 5 || s.length() < 10) {
						try {
							int c = ColorPickerPreference.convertToColorInt(s
									.toString());
							mColorPicker.setColor(c, true);
							mHexVal.setTextColor(mHexDefaultTextColor);
						} catch (IllegalArgumentException e) {
							mHexVal.setTextColor(Color.RED);
						}
					} else {
						mHexVal.setTextColor(Color.RED);
					}
					return true;
				}
				return false;
			}
		});

		((LinearLayout) mOldColor.getParent()).setPadding(
				Math.round(mColorPicker.getDrawingOffset()), 0,
				Math.round(mColorPicker.getDrawingOffset()), 0);

		mOldColor.setOnClickListener(this);
		mNewColor.setOnClickListener(this);
		mColorPicker.setOnColorChangedListener(this);
		mOldColor.setColor(color);
		mColorPicker.setColor(color, true);

	}
	
	private OnDefClickListener colorHistiryClickListener = new OnDefClickListener(){
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.last_color_1:
				sendColorToListenerWithHistory(colorH1.getColor());
				break;
			case R.id.last_color_2:
				sendColorToListenerWithHistory(colorH2.getColor());
				break;
			case R.id.last_color_3:
				sendColorToListenerWithHistory(colorH3.getColor());
				break;
			case R.id.last_color_4:
				sendColorToListenerWithHistory(colorH4.getColor());
				break;
			case R.id.last_color_5:
				sendColorToListenerWithHistory(colorH5.getColor());
				break;
			}
			dismiss();
		}
	};
	
	private void sendColorToListenerWithHistory(int color){
		String colorHex = ColorPickerPreference.convertToARGB(
				color).toUpperCase(
				Locale.getDefault());
		mListener.onColorChanged(
				color, colorHex);
		db.insertColorStruct(new ColorStruct(colorHex));
		listColorHist = db.getListHistoryColorsSelect();
		updateLastColorsPanel();
	}

	private void updateLastColorsPanel() {
		llHistoryColor.setVisibility(LinearLayout.VISIBLE);
		switch (listColorHist.size()) {
		case 1:
			colorH1.setColor(Color.parseColor(listColorHist.get(0).getHex()));
			
			colorH1.setVisibility(ColorPickerPanelView.VISIBLE);
			colorH2.setVisibility(ColorPickerPanelView.INVISIBLE);
			colorH3.setVisibility(ColorPickerPanelView.INVISIBLE);
			colorH4.setVisibility(ColorPickerPanelView.INVISIBLE);
			colorH5.setVisibility(ColorPickerPanelView.INVISIBLE);
			break;
		case 2:
			colorH1.setColor(Color.parseColor(listColorHist.get(1).getHex()));
			colorH2.setColor(Color.parseColor(listColorHist.get(0).getHex()));
			
			colorH1.setVisibility(ColorPickerPanelView.VISIBLE);
			colorH2.setVisibility(ColorPickerPanelView.VISIBLE);
			colorH3.setVisibility(ColorPickerPanelView.INVISIBLE);
			colorH4.setVisibility(ColorPickerPanelView.INVISIBLE);
			colorH5.setVisibility(ColorPickerPanelView.INVISIBLE);
			break;
		case 3:
			colorH1.setColor(Color.parseColor(listColorHist.get(2).getHex()));
			colorH2.setColor(Color.parseColor(listColorHist.get(1).getHex()));
			colorH3.setColor(Color.parseColor(listColorHist.get(0).getHex()));
			
			colorH1.setVisibility(ColorPickerPanelView.VISIBLE);
			colorH2.setVisibility(ColorPickerPanelView.VISIBLE);
			colorH3.setVisibility(ColorPickerPanelView.VISIBLE);
			colorH4.setVisibility(ColorPickerPanelView.INVISIBLE);
			colorH5.setVisibility(ColorPickerPanelView.INVISIBLE);
			break;
		case 4:
			colorH1.setColor(Color.parseColor(listColorHist.get(3).getHex()));
			colorH2.setColor(Color.parseColor(listColorHist.get(2).getHex()));
			colorH3.setColor(Color.parseColor(listColorHist.get(1).getHex()));
			colorH4.setColor(Color.parseColor(listColorHist.get(0).getHex()));
			
			colorH1.setVisibility(ColorPickerPanelView.VISIBLE);
			colorH2.setVisibility(ColorPickerPanelView.VISIBLE);
			colorH3.setVisibility(ColorPickerPanelView.VISIBLE);
			colorH4.setVisibility(ColorPickerPanelView.VISIBLE);
			colorH5.setVisibility(ColorPickerPanelView.INVISIBLE);
			break;
		case 5:
		default:
			int length = listColorHist.size();
			colorH1.setColor(Color.parseColor(listColorHist.get(length-1).getHex()));
			colorH2.setColor(Color.parseColor(listColorHist.get(length-2).getHex()));
			colorH3.setColor(Color.parseColor(listColorHist.get(length-3).getHex()));
			colorH4.setColor(Color.parseColor(listColorHist.get(length-4).getHex()));
			colorH5.setColor(Color.parseColor(listColorHist.get(length-5).getHex()));
			
			colorH1.setVisibility(ColorPickerPanelView.VISIBLE);
			colorH2.setVisibility(ColorPickerPanelView.VISIBLE);
			colorH3.setVisibility(ColorPickerPanelView.VISIBLE);
			colorH4.setVisibility(ColorPickerPanelView.VISIBLE);
			colorH5.setVisibility(ColorPickerPanelView.VISIBLE);
		}
	}

	@Override
	public void onColorChanged(int color) {

		mNewColor.setColor(color);

		if (mHexValueEnabled)
			updateHexValue(color);

		/*
		 * if (mListener != null) { mListener.onColorChanged(color); }
		 */

	}

	public void setHexValueEnabled(boolean enable) {
		mHexValueEnabled = enable;
		if (enable) {
			mHexVal.setVisibility(View.VISIBLE);
			updateHexLengthFilter();
			updateHexValue(getColor());
		} else
			mHexVal.setVisibility(View.GONE);
	}

	public boolean getHexValueEnabled() {
		return mHexValueEnabled;
	}

	private void updateHexLengthFilter() {
		if (getAlphaSliderVisible())
			mHexVal.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					9) });
		else
			mHexVal.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					7) });
	}

	private void updateHexValue(int color) {
		if (getAlphaSliderVisible()) {
			mHexVal.setText(ColorPickerPreference.convertToARGB(color)
					.toUpperCase(Locale.getDefault()));
		} else {
			mHexVal.setText(ColorPickerPreference.convertToRGB(color)
					.toUpperCase(Locale.getDefault()));
		}
		mHexVal.setTextColor(mHexDefaultTextColor);
	}

	public void setAlphaSliderVisible(boolean visible) {
		mColorPicker.setAlphaSliderVisible(visible);
		if (mHexValueEnabled) {
			updateHexLengthFilter();
			updateHexValue(getColor());
		}
	}

	public boolean getAlphaSliderVisible() {
		return mColorPicker.getAlphaSliderVisible();
	}

	/**
	 * Set a OnColorChangedListener to get notified when the color selected by
	 * the user has changed.
	 * 
	 * @param listener
	 */
	public void setOnColorChangedListener(OnColorChangedListener listener) {
		mListener = listener;
	}

	public int getColor() {
		return mColorPicker.getColor();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.new_color_panel) {
			if (mListener != null) {
				
				if (getAlphaSliderVisible()) {
					String colorHex = ColorPickerPreference.convertToARGB(
							mNewColor.getColor()).toUpperCase(
							Locale.getDefault());
					mListener.onColorChanged(
							mNewColor.getColor(), colorHex);
					db.insertColorStruct(new ColorStruct(colorHex));
					listColorHist = db.getListHistoryColorsSelect();
					updateLastColorsPanel();
				} else {
					String colorHex = ColorPickerPreference.convertToRGB(
							mNewColor.getColor()).toUpperCase(
							Locale.getDefault());
					mListener.onColorChanged(
							mNewColor.getColor(), colorHex);
					db.insertColorStruct(new ColorStruct(colorHex));
					listColorHist = db.getListHistoryColorsSelect();
					updateLastColorsPanel();
				}

			}
		}
		dismiss();
	}

	@Override
	public Bundle onSaveInstanceState() {
		Bundle state = super.onSaveInstanceState();
		state.putInt("old_color", mOldColor.getColor());
		state.putInt("new_color", mNewColor.getColor());
		if(colorH1.getVisibility() == ColorPickerPanelView.VISIBLE)
			state.putInt("color_h1", colorH1.getColor());
		if(colorH2.getVisibility() == ColorPickerPanelView.VISIBLE)
			state.putInt("color_h2", colorH2.getColor());
		if(colorH3.getVisibility() == ColorPickerPanelView.VISIBLE)
			state.putInt("color_h3", colorH3.getColor());
		if(colorH4.getVisibility() == ColorPickerPanelView.VISIBLE)
			state.putInt("color_h4", colorH4.getColor());
		if(colorH5.getVisibility() == ColorPickerPanelView.VISIBLE)
			state.putInt("color_h5", colorH5.getColor());
		return state;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mOldColor.setColor(savedInstanceState.getInt("old_color"));
		mColorPicker.setColor(savedInstanceState.getInt("new_color"), true);
		if(colorH1.getVisibility() == ColorPickerPanelView.VISIBLE)
			colorH1.setColor(savedInstanceState.getInt("color_h1"));
		if(colorH2.getVisibility() == ColorPickerPanelView.VISIBLE)
			colorH2.setColor(savedInstanceState.getInt("color_h2"));
		if(colorH3.getVisibility() == ColorPickerPanelView.VISIBLE)
			colorH3.setColor(savedInstanceState.getInt("color_h3"));
		if(colorH4.getVisibility() == ColorPickerPanelView.VISIBLE)
			colorH4.setColor(savedInstanceState.getInt("color_h4"));
		if(colorH5.getVisibility() == ColorPickerPanelView.VISIBLE)
			colorH5.setColor(savedInstanceState.getInt("color_h5"));
	}
}
