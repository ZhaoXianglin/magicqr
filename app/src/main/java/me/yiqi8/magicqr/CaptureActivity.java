package me.yiqi8.magicqr;

import java.io.IOException;


import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import me.yiqi8.image.utils.MyDatabaseHelper;
import me.yiqi8.image.utils.MylabOperate;
import me.yiqi8.image.utils.codeStrProcess;
import me.yiqi8.qrcode.camera.CameraManager;
import me.yiqi8.qrcode.decode.CaptureActivityHandler;
import me.yiqi8.qrcode.decode.InactivityTimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;



/**
 * 描述: 扫描界面
 * 负责：赵祥麟
 * 精简了zxing 二维码扫描代码
 * 
 */
public class CaptureActivity extends BaseActivity implements Callback {
	
	SQLiteDatabase db=null;
	SQLiteOpenHelper helper;
	private CaptureActivityHandler handler;
	private boolean hasSurface;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer = null;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.50f;
	private boolean vibrate;
	private int x = 0;
	private int y = 0;
	private int cropWidth = 0;
	private int cropHeight = 0;
	private RelativeLayout mContainer = null;
	private RelativeLayout mCropLayout = null;
	private boolean isNeedCapture = false;
	private static String BASE_URl = "http://api.yiqi8.me/index.php/Home/Qrcode/";
	
	/**
	 * 判断设置是否捕获，默认false
	 * @return
	 */
	public boolean isNeedCapture() {
		return isNeedCapture;
	}
	
	/**
	 * 设置捕获状态
	 * @param isNeedCapture
	 */

	public void setNeedCapture(boolean isNeedCapture) {
		this.isNeedCapture = isNeedCapture;
	}

	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	/**
	 *获取修剪宽度
	 */
	public int getCropWidth() {
		return cropWidth;
	}

	public void setCropWidth(int cropWidth) {
		this.cropWidth = cropWidth;
	}

	public int getCropHeight() {
		return cropHeight;
	}

	public void setCropHeight(int cropHeight) {
		this.cropHeight = cropHeight;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		helper = new MyDatabaseHelper(this);
		db=helper.getWritableDatabase();
		setContentView(R.layout.activity_qr_scan);
		// 初始化 CameraManager
		CameraManager.init(this.getApplication());
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		mContainer = (RelativeLayout) findViewById(R.id.capture_containter);
		mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);

		ImageView mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);//屏幕扫描线
		TranslateAnimation mAnimation = new TranslateAnimation(Animation.ABSOLUTE, 0f, Animation.ABSOLUTE, 0f,
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 0.9f);//扫描线移动动画
		mAnimation.setDuration(1500);
		mAnimation.setRepeatCount(-1);
		mAnimation.setRepeatMode(Animation.REVERSE);
		mAnimation.setInterpolator(new LinearInterpolator());
		mQrLineView.setAnimation(mAnimation);
	}

	boolean flag = true;

	/**
	 * 闪光灯开关
	 */
	protected void light() {
		if (flag == true) {
			flag = false;
			// 开闪光灯
			CameraManager.get().openLight();
		} else {
			flag = true;
			// 关闪光灯
			CameraManager.get().offLight();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * 解码结果处理
	 * @param result
	 */
	public void handleDecode(final String result) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		//Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
		new Thread(){
			public void run(){
				if(codeStrProcess.checkStr(result)){
					int i=codeStrProcess.checkEmpty(result);
					char c = CaptureActivity.this.checkType(result);
					if(i==0){
						Intent intent = new Intent(CaptureActivity.this,CodeNotExistActivity.class);
						intent.putExtra("result", result);
						CaptureActivity.this.startActivity(intent);
						Log.i("hehe", "0000000000000");
						CaptureActivity.this.finish();
					}
					if(i==1)//为空
					{
						UploadFinalActivity.QRCODE = getCode(result);
						Log.i("hehe", "1111111111111111111");
						CaptureActivity.this.onEmpty(c);
						//new MylabOperate(db).insert(getCode(result));
					}
					if(i==2)//不为空
					{
						Log.i("hehe", "222222222222222222222222");
						SyncHttpClient client = new SyncHttpClient();
						//RequestParams params = new RequestParams();
						client.get(BASE_URl+"return_code/check_code/"+getCode(result), new JsonHttpResponseHandler(){

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
						         new MylabOperate(db).insert(getCode(result),arr[0],arr[arr.length-2],arr[arr.length-1]);//insert into database
						         char QRtype = CaptureActivity.this.checkType(result);
						         Intent intent = new Intent();
						         intent.putExtra("QR_REL_CONTENT", arr);
						         switch (QRtype) {
								case 'I':
									intent.setClass(CaptureActivity.this, ImageShowActivity.class);
									startActivity(intent);
									finish();
									break;
								case 'V':
									intent.setClass(CaptureActivity.this, Audio_palyActivity.class);
									startActivity(intent);
									finish();
									break;
								case 'M':
									intent.setClass(CaptureActivity.this, VedioPrepareActivity.class);
									startActivity(intent);
									finish();
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
				}else{
					Log.i("hehehe", "字符串不匹配");
					Intent intent = new Intent(CaptureActivity.this,CodeNotExistActivity.class);
				intent.putExtra("result", result+"字符串不匹配");
				startActivity(intent);}
			}
		}.start();
		
		// 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
		// handler.sendEmptyMessage(R.id.restart_preview);
	}

	/**
	 * 初始化相机
	 * @param surfaceHolder
	 */
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);

			Point point = CameraManager.get().getCameraResolution();
			int width = point.y;
			int height = point.x;

			int x = mCropLayout.getLeft() * width / mContainer.getWidth();
			int y = mCropLayout.getTop() * height / mContainer.getHeight();

			int cropWidth = mCropLayout.getWidth() * width / mContainer.getWidth();
			int cropHeight = mCropLayout.getHeight() * height / mContainer.getHeight();

			setX(x);
			setY(y);
			setCropWidth(cropWidth);
			setCropHeight(cropHeight);
			// 设置是否需要截图
			setNeedCapture(true);
			

		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(CaptureActivity.this);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public Handler getHandler() {
		return handler;
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepareAsync();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	private final OnCompletionListener beepListener = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
	public char checkType(String result){
		char[] ch;
		String[] s=result.split("/");
		ch = s[s.length-1].toCharArray();
		return ch[0];
	}
	public void onEmpty(char c){
		Intent it;
		switch(c){
		case 'I':
			it = new Intent(CaptureActivity.this,ImageAddActivity.class);
			CaptureActivity.this.startActivity(it);
			CaptureActivity.this.finish();
			break;
		case 'V':
			it = new Intent(CaptureActivity.this,AudioActivity.class);
			CaptureActivity.this.startActivity(it);
			CaptureActivity.this.finish();
			break;
		case 'M':
			it = new Intent(CaptureActivity.this,CameraVideo.class);
			CaptureActivity.this.startActivity(it);
			CaptureActivity.this.finish();
			break;
		default:break;
		}
	}
	public String getCode(String result){
		String[] s=result.split("/");
		String str = s[s.length-1];
		return str;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK)
			finish();
		return super.onKeyDown(keyCode, event);
	}
	public void showNotFindDialog(String result){
		Dialog dialog = new AlertDialog.Builder(this)
		.setTitle("这不是我们的二维码")		// 创建标题
		.setMessage(result) // 表示对话框中的内容
		.setPositiveButton("确定",new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				CaptureActivity.this.finish();
			}
		})
		.setIcon(R.drawable.ic_launcher) // 设置LOGO
		.create(); // 创建了一个对话框
		dialog.show() ;	// 显示对话框
	}
	
}
