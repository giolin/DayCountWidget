package mmpud.project.daycountwidget.pages.configure;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;

import javax.annotation.CheckForNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmpud.project.daycountwidget.R;

public class ColorSelectDialog extends DialogFragment {

    static private String KEY_COLOR = "color";

    @BindView(R.id.color_picker)
    ColorPicker colorPicker;
    @BindView(R.id.svbar)
    SVBar svBar;

    private static ColorSelectDialog dialog;

    private OnColorSelectedListener onColorSelectedListener;

    public static ColorSelectDialog newInstance(int initColor) {
        if (dialog == null) {
            synchronized (ColorSelectDialog.class) {
                if (dialog == null) {
                    dialog = new ColorSelectDialog();
                }
            }
        }
        Bundle args = new Bundle();
        args.putInt(KEY_COLOR, initColor);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.color_select_dialog, null);
        ButterKnife.bind(this, view);
        colorPicker.addSVBar(svBar);
        Bundle bundle = getArguments();
        if (bundle != null) {
            int color = bundle.getInt(KEY_COLOR);
            colorPicker.setColor(color);
            colorPicker.setOldCenterColor(color);
        }
        return new AlertDialog.Builder(context)
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        if (onColorSelectedListener != null) {
                            onColorSelectedListener.OnColorSelected(colorPicker.getColor());
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null).create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onColorSelectedListener = null;
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
