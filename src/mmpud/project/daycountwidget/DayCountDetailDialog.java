package mmpud.project.daycountwidget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class DayCountDetailDialog extends Activity {

	Button btnEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_day_count_detail_dialog);
		
		btnEdit = (Button)findViewById(R.id.btn_edit);
		btnEdit.setOnClickListener(mOnClickListener);

	}
		
	View.OnClickListener mOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
	};

}
