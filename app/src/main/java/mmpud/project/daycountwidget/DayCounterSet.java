package mmpud.project.daycountwidget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class DayCounterSet extends Activity {

    private DatePicker datePicker;
    private EditText edtTitle;
    private Button btnOK;
    private int mPosition;
    private int mId;
    private String mTargetDate;
    private String mTitle;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.set_btn_ok) {
                mTargetDate = datePicker.getYear() + "-"
                        + (datePicker.getMonth()+1) + "-" // To show the right month, add 1
                        + datePicker.getDayOfMonth();
                mTitle = edtTitle.getText().toString();

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("position", mPosition);
                bundle.putInt("id", mId);
                bundle.putString("target_date", mTargetDate);
                bundle.putString("title", mTitle);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    };

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // Set up the view layout resource to use.
        setContentView(R.layout.day_count_set_layout);

        datePicker = (DatePicker) findViewById(R.id.set_picker);
        edtTitle = (EditText) findViewById(R.id.set_edt_title);
        btnOK = (Button) findViewById(R.id.set_btn_ok);

        btnOK.setOnClickListener(mOnClickListener);

        Intent intent = getIntent();
        mPosition = intent.getIntExtra("position", -1);
        mId = intent.getIntExtra("id", -1);
        mTargetDate = intent.getStringExtra("target_date");
        mTitle = intent.getStringExtra("title");

        if(mTargetDate != null) {
            String[] ymd = mTargetDate.split("-");
            datePicker.updateDate(Integer.parseInt(ymd[0]),
                    Integer.parseInt(ymd[1])-1, // Month should be reduced by 1 in DatePicker
                    Integer.parseInt(ymd[2]));
        }
        if(mTitle != null) {
            edtTitle.setText(mTitle);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}
