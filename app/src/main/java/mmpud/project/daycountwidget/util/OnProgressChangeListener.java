package mmpud.project.daycountwidget.util;

import android.widget.SeekBar;

public abstract class OnProgressChangeListener implements SeekBar.OnSeekBarChangeListener {

    @Override
    public abstract void onProgressChanged(SeekBar seekBar, int i, boolean b);

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // do nothing intended
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // do nothing intended
    }

}
