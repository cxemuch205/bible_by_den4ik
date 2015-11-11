package ua.maker.gbible_v2.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.inject.Inject;

import roboguice.RoboGuice;
import roboguice.inject.InjectView;
import ua.maker.gbible_v2.Constants.App;
import ua.maker.gbible_v2.Managers.PreferenceManager;
import ua.maker.gbible_v2.R;

/**
 * Created by daniilpavenko on 11/11/15.
 */
public class TextSizeSelectSettings extends LinearLayout {

    SeekBar sbSelectTextSize;
    TextView tvSampleText;
    PreferenceManager prefs;

    public TextSizeSelectSettings(Context context) {
        super(context);
        init(context);
    }

    public TextSizeSelectSettings(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TextSizeSelectSettings(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public TextSizeSelectSettings(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.select_text_size_layout, this);
        /*RoboGuice.injectMembers(context, this);
        RoboGuice.getInjector(context).injectViewMembers(this);*/

        prefs = new PreferenceManager(context);

        sbSelectTextSize = (SeekBar) findViewById(R.id.sb_select_text_size);
        tvSampleText = (TextView) findViewById(R.id.tv_sample_text);

        sbSelectTextSize.setMax(App.MAX_TEXT_SIZE);
        sbSelectTextSize.setProgress((int) prefs.getTextPoemSize() - (int) App.DEFAULT_TEXT_SIZE);
        sbSelectTextSize.setOnSeekBarChangeListener(seekBarChangeListener);
        tvSampleText.setTextSize(prefs.getTextPoemSize());
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            tvSampleText.setTextSize(calculateTextSize(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            prefs.setTextPoemSize(calculateTextSize(seekBar.getProgress()));
        }
    };

    private float calculateTextSize(int progress) {
        return App.DEFAULT_TEXT_SIZE + progress;
    }
}
