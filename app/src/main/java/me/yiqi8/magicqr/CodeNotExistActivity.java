package me.yiqi8.magicqr;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class CodeNotExistActivity extends Activity {
	private TextView tresult = null;
	private TextView back = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_code_not_exist);
		Intent it = getIntent();
		String result = it.getStringExtra("result");
		tresult = (TextView)super.findViewById(R.id.text_result);
		tresult.setText(result);
		back = (TextView)super.findViewById(R.id.back_notfind);
		back.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.code_not_exist, menu);
		return true;
	}

}
