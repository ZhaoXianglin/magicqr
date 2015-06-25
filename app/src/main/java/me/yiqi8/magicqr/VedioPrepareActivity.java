package me.yiqi8.magicqr;

import java.io.File;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class VedioPrepareActivity extends Activity {
	Button back= null;
	EditText title= null;
	EditText content= null;
	ImageView iv = null;
	Intent it=null;
	String[] arr =null;
	String path;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vedio_prepare);
		back = (Button)super.findViewById(R.id.vedio_pre_back);
		title = (EditText)super.findViewById(R.id.vedio_pre_title);
		content = (EditText)super.findViewById(R.id.vedio_pre_content);
		iv = (ImageView)super.findViewById(R.id.vedio_pre_play);
		AsyncHttpClient client = new AsyncHttpClient();
		it = getIntent();				//接收intent用于传递路径
		arr = new String[3];
		arr=it.getStringArrayExtra("QR_REL_CONTENT");
		title.setText(arr[1]);
		content.setText(arr[2]);
		String s= arr[0];
		System.out.print(s);
		StringBuffer sb = new StringBuffer(s);
		sb.deleteCharAt(sb.length()-1);
		FileAsyncHttpResponseHandler fah = new FileAsyncHttpResponseHandler(this){
			@Override
		    public void onSuccess(int statusCode, Header[] headers, File response) {
				 path = this.getTargetFile().getAbsolutePath();
		       
		    }
			@Override
			public void onFailure(int arg0, Header[] arg1, Throwable arg2,
					File arg3) {
				// TODO Auto-generated method stub
				
			}
		};
		//client.get(sb.toString(),fah);
		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(VedioPrepareActivity.this,VideoPlayerActivity.class);
				in.putExtra("path", path);
				in.putExtra("QR_REL_CONTENT", arr);
				startActivity(in);
			}
		});
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.vedio_prepare, menu);
		return true;
	}

}
