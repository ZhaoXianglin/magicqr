package me.yiqi8.magicqr;
/**
 * 这是一个界面布局尚待调整的播放activity
 * 我的设计思路是在扫描了一个音频二维码后把音频下载下来然后跳转到这个activity，
 * 同时在intent中传入 一个叫做“path”的值 
 * 于是这个activity会根据path播放音频
 * 现阶段界面比较简洁，只有进度条和一个按钮，主要是因为我觉得只完成播放的功能的话有着两个就够了
 * 按钮同时可以起到播放和暂停的功能
 * */
import java.io.File;
import java.io.IOException;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class Audio_palyActivity extends Activity {
	Intent it = null;
	SeekBar bar = null;
	Button but = null;
	MediaPlayer mp = null;
	Thread myThread =null;
	private boolean isplay = false;
	private boolean ispause = false;
	private String path = "";
	Button back = null;
	EditText title = null;
	EditText content = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_paly);
		it = getIntent();				//接收intent用于传递路径
		String[] arr = new String[3];
		arr=it.getStringArrayExtra("QR_REL_CONTENT");
		String s= arr[0];
		StringBuffer sb = new StringBuffer(s);
		sb.deleteCharAt(sb.length()-1);
		path = null;//path保存路径
		but = (Button)super.findViewById(R.id.audio_play_button);
		but.setEnabled(false);
		mp = new MediaPlayer();
		AsyncHttpClient client = new AsyncHttpClient();
		FileAsyncHttpResponseHandler fah = new FileAsyncHttpResponseHandler(this){
			@Override
		    public void onSuccess(int statusCode, Header[] headers, File response) {
				 path = this.getTargetFile().getAbsolutePath();
		         but.setEnabled(true);			//		准备播放
		 		try{
		 			mp.setDataSource(path);
		 			mp.prepare();
		 		}catch(Exception e){
		 			e.printStackTrace();
		 		}
		    }
			@Override
			public void onFailure(int arg0, Header[] arg1, Throwable arg2,
					File arg3) {
				// TODO Auto-generated method stub
				
			}
		};
		client.get(sb.toString(),fah);
		title=(EditText)super.findViewById(R.id.audio_play_title);
		title.setText(arr[1]);
		content=(EditText)super.findViewById(R.id.audio_play_content);
		content.setText(arr[2]);
		back = (Button)super.findViewById(R.id.audio_play_back);
		back.setOnClickListener(new OnClickListener() {				//点击返回时返回主页面
			@Override
			public void onClick(View v) {
				Intent tomain = new Intent(Audio_palyActivity.this,MainActivity.class);
				Audio_palyActivity.this.startActivity(tomain);
				Audio_palyActivity.this.finish();
			}
		});
		
		
		mp.setOnCompletionListener(new OnCompletionListener() {	  //播放结束
			@Override
			public void onCompletion(MediaPlayer arg0) {
				mp.reset();
				try {
					mp.setDataSource(path);
					mp.prepare();
				} catch (Exception e) {
					e.printStackTrace();
				} 
				isplay = false;
				ispause = false;
				bar.setProgress(0);
				but.setText("播 放");
			}
		});
		bar = (SeekBar)super.findViewById(R.id.audio_play_seekBar);
		but.setOnTouchListener(new OnTouchListenerImp());
		bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {					//拖动进度条
				if(isplay){
					mp.seekTo(arg0.getProgress());
					bar.setProgress(arg0.getProgress());	
				}
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
								
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
						
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.audio_paly, menu);
		return true;
	}
	
	class OnTouchListenerImp implements OnTouchListener{

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			switch(arg1.getAction()){
			case MotionEvent.ACTION_DOWN:
				if(Audio_palyActivity.this.isplay == false){
					Audio_palyActivity.this.but.setText("播 放");
				}else if(Audio_palyActivity.this.isplay == true){
					Audio_palyActivity.this.but.setText("暂 停");
				}
				break;
			case MotionEvent.ACTION_UP:
				if(Audio_palyActivity.this.isplay == false){
					Audio_palyActivity.this.but.setText("暂 停");
					if(Audio_palyActivity.this.ispause == false){//没播放没暂停
						Audio_palyActivity.this.bar.setMax(Audio_palyActivity.this.mp.getDuration());
						Audio_palyActivity.this.mp.start();
						Audio_palyActivity.this.isplay = true;
						myThread = new MyThread();
						myThread.start();
					}else{										//没播放暂停了
						Audio_palyActivity.this.ispause = false;
						Audio_palyActivity.this.isplay = true;
						Audio_palyActivity.this.mp.start();
					}
					
				}else if(Audio_palyActivity.this.isplay == true){//播放时
					Audio_palyActivity.this.but.setText("播 放");
					Audio_palyActivity.this.mp.pause();
					Audio_palyActivity.this.ispause = true;
					Audio_palyActivity.this.isplay = false;
				}
				break;
			
			}
			return false;
		}
		
	}
	class MyThread extends Thread{
		@Override
		public void run(){
			while(mp!=null){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(mp!=null){
					bar.setProgress(mp.getCurrentPosition());	
				}
			}
		}
	}
	
}
