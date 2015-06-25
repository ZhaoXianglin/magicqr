package me.yiqi8.magicqr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import me.yiqi8.image.utils.MyDatabaseHelper;
import me.yiqi8.image.utils.MylabOperate;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SaveDataActivity extends Activity {
	ListView lv = null;
	SQLiteDatabase db=null;
	SQLiteOpenHelper helper;
	List<String> array = null;
	String code = null;
	Intent intent = null;
	private static String BASE_URl = "http://api.yiqi8.me/index.php/Home/Qrcode/";
	
	public Handler myHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 0:
				intent.setClass(SaveDataActivity.this, ImageShowActivity.class);
				startActivity(intent);
				SaveDataActivity.this.finish();
				break;
			case 1:
				Log.i("hehe","运行至54");
				intent.setClass(SaveDataActivity.this, Audio_palyActivity.class);
				startActivity(intent);
				SaveDataActivity.this.finish();
				break;
			case 2:
				intent.setClass(SaveDataActivity.this, VedioPrepareActivity.class);
				startActivity(intent);
				SaveDataActivity.this.finish();
				break;
			default:break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_save_data);
		TextView tv= (TextView)super.findViewById(R.id.save_back);
		tv.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		lv = (ListView)super.findViewById(R.id.savelist);
		helper = new MyDatabaseHelper(this);
		db=helper.getWritableDatabase();
		Set<String> all = new MylabOperate(db).findAll();
		array = new ArrayList<String>();
		array.addAll(all);
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,array);
		lv.setAdapter(aa);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Network net = new Network(position);
				net.start();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.save_data, menu);
		return true;
	}
	public char checkType(String code){
		char[] c =code.toCharArray();
		return c[0];
	}
	class Network extends Thread{
		public int position;
		public Network(int position){
			this.position = position;
		}
		public void run(){
			String s = array.get(position);
			db=helper.getWritableDatabase();
			Map<String,String> map = new MylabOperate(db).findBytitle(s);
			code = map.get("code");
			Log.i("hehe", "222222222222222222222222");
			SyncHttpClient client = new SyncHttpClient();
			//RequestParams params = new RequestParams();
			client.get(BASE_URl+"return_code/check_code/"+code, new JsonHttpResponseHandler(){

			    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
			      // Handle resulting parsed JSON response here
			    	 String[] arr=new String[response.length()];  
			         for(int i=0;i<response.length();i++){
			             try {
							arr[i]=response.getString(i);
							Log.i("hehe", arr[i]);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			         }
			         Log.i("hehe", "运行至135");
			         char QRtype = SaveDataActivity.this.checkType(code);
			         intent = new Intent();
			         intent.putExtra("QR_REL_CONTENT", arr);
			         Message msg = new Message();
			         Log.i("hehe", "运行至140"+QRtype);
			         switch (QRtype) {
						case 'I':
							Log.i("hehe", "运行至143");
							msg.what = 0;
							SaveDataActivity.this.myHandler.sendMessage(msg);
							break; 
						case 'V':
							Log.i("hehe", "运行至148");
							msg.what = 1;
							SaveDataActivity.this.myHandler.sendMessage(msg);
							break; 
						case 'M':
							msg.what = 2;
							SaveDataActivity.this.myHandler.sendMessage(msg);
							break; 
	
						default:
							break;
					}
			    }
			    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				       // Handle resulting parsed JSON response here
						Log.i("nimei", response.toString());
				    }
			});
		}
	}
}
