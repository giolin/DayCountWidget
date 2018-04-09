package mmpud.project.daycountwidget.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mmpud.project.daycountwidget.R;

public class NumberPicker extends LinearLayout {

    @BindView(R.id.number_text)
    TextView numberText;

    private int number;

    public NumberPicker(Context context) {
        this(context, null);
    }

    public NumberPicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        setVerticalGravity(Gravity.CENTER);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.number_picker, this, true);
        ButterKnife.bind(this, this);
        number = 0;
        updateUi();
    }

    public void setNumber(int n) {
        number = n;
        updateUi();
    }

    public int getNumber() {
        return number;
    }

    @OnClick(R.id.add)
    void addNumber() {
        if (number == 99) {
            return;
        }
        number++;
        updateUi();
    }

    @OnClick(R.id.minus)
    void minusNumber() {
        if (number == 0) {
            return;
        }
        number--;
        updateUi();
    }

    private void updateUi() {
        numberText.setText(String.format(Locale.ENGLISH, "%02d", number));
    }

}