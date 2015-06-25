package me.yiqi8.magicqr;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.yiqi8.magicqr.R;
import me.yiqi8.qrcode.vedio.VedioNextWindow;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CameraVideo extends Activity implements Callback, OnClickListener,
		AutoFocusCallback {
	ImageButton del, c_recode, c_stop; // -----
	String exerciseId = "mycamera";
	// EditText upfilename;
	SurfaceView mySurfaceView;// surfaceView����
	SurfaceHolder holder;// surfaceHolder����
	Camera myCamera;// �������
	String filePath = Environment.getExternalStorageDirectory().toString()
			+ "/" + exerciseId + "/";// ��Ƭ�����ļ���
	String fileName = "";
	TextView timelimit = null;
	int i = 1, j = 0;
	private MediaRecorder mediaRecorder; // -----
	private boolean record; // -----
	String path = Environment.getExternalStorageDirectory() + "/MagicQR";// 保存路径//-----
	VedioNextWindow vnw = null;
	File videoFile = null;
	SimpleDateFormat format1;

	// 创建jpeg图片回调数据对象
	PictureCallback jpeg = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {				
			try {// 获得图片
				Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);					
				File file = new File(filePath + fileName);
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(file));
				bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);// 将图片压缩到流中
				bos.flush();// 输出
				bos.close();// 关闭
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	public Handler myHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (j < 10) {
					timelimit.setText("0" + j + ":" + i + "0/24:00");
				} else {
					timelimit.setText(j + ":" + i + "0/24:00");
				}
				if (j == 24) {
					stopRe();
				}
				break;
			case 1:
				c_recode.setImageResource(R.drawable.luxiang2);
				vnw.dismiss();
				startRe();
				new Time().start();
				break;
			case 2:
				Intent it = new Intent(CameraVideo.this,UploadFinalActivity.class);
				it.putExtra("type", UploadFinalActivity.TYPE_MOV);
				it.putExtra("path", videoFile.getAbsolutePath()); // 跳转至上传界面
				startActivity(it);
				finish();
			default:
				
				break;
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		record = false;
		format1 = new SimpleDateFormat("yyyyMMddHHmmss");
		/* ��SD���ϴ���Ŀ¼ */
		File eis = new File(Environment.getExternalStorageDirectory()
				.toString() + "/" + "MagicQR" + "/");
		try {
			if (!eis.exists()) {
				eis.mkdir();
			}
		} catch (Exception e) {

		}
		setContentView(R.layout.camera_layout);
		vnw = new VedioNextWindow(this);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题
		timelimit = (TextView) super.findViewById(R.id.timeview);
		// setContentView(R.layout.camera_layout);
		// 获得控件
		mySurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
		// 获得句柄
		holder = mySurfaceView.getHolder();
		// 添加回调
		holder.addCallback(this);
		// 设置类型
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// 设置监听
		mySurfaceView.setOnClickListener(this);
		// del = (Button)findViewById(R.id.c_del); //删除按钮
		c_recode = (ImageButton) findViewById(R.id.c_recode); // 开始 录像//-----
		// c_stop = (Button)findViewById(R.id.c_stop); //停止 录像//-----
		// 开始
		c_recode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!record) {
					c_recode.setImageResource(R.drawable.luxiang2);
					startRe();
					new Time().start();
					return;
				}
				if (record) {
					c_recode.setImageResource(R.drawable.luxiang1);
					stopRe();
					return;
				}
			}
		});
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		// ���ò���ʼԤ��
		Camera.Parameters params = myCamera.getParameters();
		params.setPictureFormat(PixelFormat.JPEG);

		myCamera.setParameters(params);
		myCamera.startPreview();

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		// �������
		if (myCamera == null) {
			myCamera = Camera.open();
			try {
				myCamera.setPreviewDisplay(holder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

		// �ر�Ԥ�����ͷ���Դ
		if (myCamera != null) {
			myCamera.stopPreview();
			myCamera.release();
			myCamera = null;
		}
		if (mediaRecorder != null) {
			mediaRecorder.release(); // ----
			mediaRecorder = null;
		}

	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		// TODO Auto-generated method stub
		if (success) {
			// ���ò���,������
			Camera.Parameters params = myCamera.getParameters();
			params.setPictureFormat(PixelFormat.JPEG);
			myCamera.setParameters(params);
			myCamera.takePicture(null, null, jpeg);
		}

	}

	public void startRe() {
		myCamera.stopPreview();
		myCamera.release();
		myCamera = null;
		if (mediaRecorder == null) {
			mediaRecorder = new MediaRecorder(); // -----
		}
		fileName = "recordvedio";
		mediaRecorder.reset();
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); // 从照相机采集视频
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		/* 引用android.util.DisplayMetrics 获取分辨率 */
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mediaRecorder.setVideoSize(800,480);
		mediaRecorder.setVideoFrameRate(15); // 每秒3帧
		mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP); // 设置视频编码方式
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); // 设置音频编码方式
		videoFile = new File(path, fileName + ".mp4"); // 保存路径及名称
		mediaRecorder.setOutputFile(videoFile.getAbsolutePath());
		mediaRecorder.setPreviewDisplay(holder.getSurface());
		try {
			mediaRecorder.prepare();// 预期准备
		} catch (Exception e) {
			e.printStackTrace();
		}
		mediaRecorder.start();// 开始刻录
		record = true;
	}

	public void stopRe() {
		if (record) {
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;

			record = false;
			c_recode.setImageResource(R.drawable.luxiang1);
			// �������
			if (myCamera == null) {
				myCamera = Camera.open();
				try {
					myCamera.setPreviewDisplay(holder);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Camera.Parameters params = myCamera.getParameters();
			params.setPictureFormat(PixelFormat.JPEG);
			// myCamera.setDisplayOrientation(90);
			myCamera.setParameters(params);
			myCamera.startPreview(); // ����Ԥ��
			vnw.showAtLocation(this.findViewById(R.id.record_vedio_main), 0, 0,
					(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL));
			i = 0;
			j = 0;
		}
	}

	public class Time extends Thread {
		public void run() {
			while (record) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Message msg = new Message();
				msg.what = 0;
				CameraVideo.this.myHandler.sendMessage(msg);
				i++;
				if (i == 10) {
					i = 0;
					j++;
				}
				if (j == 24) {

					return;
				}
			}
		}
	}
}