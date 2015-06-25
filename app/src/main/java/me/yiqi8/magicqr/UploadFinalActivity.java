package me.yiqi8.magicqr;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;
import org.apache.http.Header;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UploadFinalActivity extends Activity implements OnClickListener {
	private EditText title = null;
	private EditText description = null;
	private EditText question = null;
	private EditText answer = null;
	private Button cancel = null;
	private Button cancel1 = null;
	private Button ok = null;
	private Button back = null;
	private String titleStr = null;
	private String desStr = null;
	private String queStr = null;
	private String ansStr = null;
	private String upFilePath = null;
	private Intent it = null;
	int fileType = 1;
	private List<String> imgList = null;
	public static String QRCODE = "";
	public  static final int TYPE_IMG = 1;
	public static final int TYPE_MUC = 5;
	public static final int TYPE_MOV = 10;
	private static final String audioPath = "/up_voice";
	private static final String imagePath = "/up_image";
	private static final String moviePath = "/up_movie";
	private static final String BASE_URL = "http://api.yiqi8.me/index.php/Home/Upload";
	private static AsyncHttpClient client = new AsyncHttpClient();
	ProgressDialog progressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_final);
		//client.setEnableRedirects(false, false, false);
		title = (EditText) super.findViewById(R.id.act_upload_title);
		description = (EditText) super.findViewById(R.id.act_upload_content);
		question = (EditText) super
				.findViewById(R.id.act_upload_password_question);
		answer = (EditText) super.findViewById(R.id.act_upload_password_answer);
		cancel = (Button) super.findViewById(R.id.act_upload_button_reset);
		cancel1 = (Button)super.findViewById(R.id.act_upload_header_btn_back);
		ok = (Button) super.findViewById(R.id.act_upload_button_upbtn);
		back = (Button)findViewById(R.id.act_upload_header_btn_back);
		ok.setOnClickListener(this);
		cancel.setOnClickListener(this);
		cancel1.setOnClickListener(this);
		it = this.getIntent();
		fileType = it.getIntExtra("type",fileType);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.act_upload_button_upbtn:
			this.getContent();
			if (this.titleStr == "" || this.desStr == "") {
				Toast.makeText(this, "you must have a title and description",
						Toast.LENGTH_SHORT).show();
				break;
			}
			switch (fileType) {
			case TYPE_IMG:
				imgList = this.it.getStringArrayListExtra("imgList");
				uploadImgs(titleStr,desStr,queStr,ansStr,imgList,imagePath);
				break;
			case TYPE_MUC:
				this.upFilePath = it.getStringExtra("path");
				uploadFiles(titleStr,desStr,queStr,ansStr,upFilePath,audioPath);
				break;
			case TYPE_MOV:
				this.upFilePath = it.getStringExtra("path");
				uploadFiles(titleStr,desStr,queStr,ansStr,upFilePath,moviePath);
				break;
			default:
				break;
			}
			break;
		case R.id.act_upload_button_reset:
			toHome();
			break;
		case R.id.act_img_header_btn_back:
			toHome();
			break;
		default : break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			UploadFinalActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	public void toHome() {
		this.finish();
	}

	public void getContent() {
		this.titleStr = this.title.getText().toString();
		this.desStr = this.description.getText().toString();
		this.queStr = this.question.getText().toString();
		this.ansStr = this.answer.getText().toString();
	}

	public void uploadFiles(String title, String content, String que,
			String answer, String path, String type) {
		File myFile = new File(path);
		RequestParams params = new RequestParams();
		try {
			params.put("title", title);
			params.put("content", content);
			params.put("check_code",QRCODE);
			if (que != "" && answer != "") {
				params.put("question", que);
				params.put("answer", answer);
			}
		    params.put("uploadfile", myFile);
		} catch(FileNotFoundException e) {}
		this.showUploadingDia();
		client.post(BASE_URL+type,params,new AsyncHttpResponseHandler(){
				@Override
				public void onFailure(int arg0, Header[] arg1,byte[] arg2, Throwable arg3) {
					Log.i("hehe","update error");
					UploadFinalActivity.this.dismissUploadingDia();
					Toast.makeText(UploadFinalActivity.this, "上传失败，请检查网络连接", Toast.LENGTH_SHORT).show();

				}
				@Override
				public void onSuccess(int arg0, Header[] arg1,byte[] arg2) {
					Log.i("hehe","update success");
					UploadFinalActivity.this.dismissUploadingDia();
					SuccessDialog();
				}
		});
	}
	public void uploadImgs(String title,String content,String que,String answer,List<String> imgList,String type){
		RequestParams params = new RequestParams();
		try{
		params.put("title", title);
		params.put("content", content);
		params.put("check_code",QRCODE);
		if(que!=""&&answer!=""){
			params.put("question", que);
			params.put("answer", answer);
		}
		Iterator<String> iter = imgList.iterator();
		int i = 0;
		while(iter.hasNext()){
			File f = new File(iter.next().toString());
			params.put("uploadfile"+i,f);
			Log.i("hehe",f.toString());
			i++;
		}
		}catch(Exception e){e.printStackTrace();}
		this.showUploadingDia();
		client.post(BASE_URL+type,params,new AsyncHttpResponseHandler(){
				@Override
				public void onFailure(int arg0, Header[] arg1,byte[] arg2, Throwable arg3) {
					Log.i("hehe","update error");
					UploadFinalActivity.this.dismissUploadingDia();
					Toast.makeText(UploadFinalActivity.this, "上传失败，请检查网络连接", Toast.LENGTH_SHORT).show();

				}
				@Override
				public void onSuccess(int arg0, Header[] arg1,byte[] arg2) {
					Log.i("hehe","update success");
					UploadFinalActivity.this.dismissUploadingDia();
					SuccessDialog();
					//FileUtils.deleteDir();
				}
		});
	}
	public void SuccessDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(UploadFinalActivity.this);
		builder.setTitle("提示").setMessage("上传成功").setPositiveButton("我知道了",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//Intent it = new Intent(UploadFinalActivity.this,MainActivity.class);
			//	UploadFinalActivity.this.startActivity(it);
				dialog.dismiss();
				UploadFinalActivity.this.finish();
			}
		}).create().show();
	}
	public void showUploadingDia(){
		progressDialog = ProgressDialog.show(UploadFinalActivity.this, "请稍等...", "正在生成属于你的二维码...", true);
		progressDialog.setCancelable(true);
		progressDialog.setCanceledOnTouchOutside(false);
	}
	public void dismissUploadingDia(){
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
}
