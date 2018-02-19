package mmpud.project.daycountwidget.pages.configure;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mmpud.project.daycountwidget.R;

public class ColorSelectDialog extends DialogFragment {

    static private String KEY_COLOR = "color";

    @BindView(R.id.color_picker)
    ColorPicker colorPicker;
    @BindView(R.id.svbar)
    SVBar svBar;

    private OnColorSelectedListener onColorSelectedListener;

    public static ColorSelectDialog newInstance(int initColor) {
        ColorSelectDialog dialog = new ColorSelectDialog();
        Bundle args = new Bundle();
        args.putInt(KEY_COLOR, initColor);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.color_select_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        colorPicker.addSVBar(svBar);
        Bundle bundle = getArguments();
        if (bundle != null) {
            int color = bundle.getInt(KEY_COLOR);
            colorPicker.setColor(color);
            colorPicker.setOldCenterColor(color);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onColorSelectedListener = null;
    }

    @OnClick(R.id.ok)
    void onColorSelected() {
        if (onColorSelectedListener != null) {
            onColorSelectedListener.OnColorSelected(colorPicker.getColor());
        }
        dismiss();
    }

    @OnClick(R.id.cancel)
    void onCancel() {
        dismiss();
    }

    /**
     * Set color selected listener.
     *
     * @param onColorSelectedListener color selected listener
     */
    void setOnColorSelectedListener(OnColorSelectedListener onColorSelectedListener) {
        this.onColorSelectedListener = onColorSelectedListener;
    }

    interface OnColorSelectedListener {
        void OnColorSelected(int color);
    }

}
