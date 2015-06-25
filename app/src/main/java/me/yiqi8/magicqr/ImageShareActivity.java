package me.yiqi8.magicqr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import me.yiqi8.image.photoview.PhotoView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ImageShareActivity extends Activity {

	private PhotoView mPhotoView;
	private Bitmap mProImage;
	private Button mBack;
	private Button mSave;
	private Button mShare;
	private Button mEdit;
	private String imagePath;
	private String mtype;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_editandshare);
		init();
		setBtn();
		Intent intent = getIntent();
		if (intent != null) {
			imagePath = intent.getStringExtra(QRInImageActivity.PRO_VIEW_IMAGE);
			mtype = intent.getStringExtra("QR_TYPE");
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(imagePath);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mProImage = BitmapFactory.decodeStream(fis);
			mPhotoView.setImageBitmap(mProImage);
		}

	}

	public void init() {
		mPhotoView = (PhotoView) findViewById(R.id.iv_pro_view);
		mBack = (Button) findViewById(R.id.image_share_header_btn_back);
		mSave = (Button) findViewById(R.id.image_share_header_btn_save);
		mShare = (Button) findViewById(R.id.image_share_btn_share);
		mEdit = (Button) findViewById(R.id.image_share_btn_edit);
	}

	public void setBtn() {
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		mSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (mProImage != null) {
					MediaStore.Images.Media.insertImage(getContentResolver(),
							mProImage, "QRImage", "QRImage");
					Toast.makeText(ImageShareActivity.this, "生成成功，请到相册查看",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(ImageShareActivity.this, "图片生成错误，请返回重试",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		mShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_SEND);

				File f = new File(imagePath);
				if (f != null && f.exists() && f.isFile()) {
					intent.setType("image/jpg");
					Uri u = Uri.fromFile(f);
					intent.putExtra(Intent.EXTRA_STREAM, u);
				}
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(intent, "分享图片"));
			}
		});
		mEdit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				char type = mtype.charAt(0);
				Intent intent = new Intent();
				
				switch (type) {
				case 'I':
					intent.setClass(ImageShareActivity.this, ImageAddActivity.class);
					startActivity(intent);
					finish();
					break;
				case 'V':
					intent.setClass(ImageShareActivity.this, AudioActivity.class);
					startActivity(intent);
					finish();
					break;
				case 'M':
					intent.setClass(ImageShareActivity.this, CameraVideo.class);
					startActivity(intent);
					finish();
					break;
				default:
					Toast.makeText(ImageShareActivity.this, "魔码类型错误，请返回重试", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});
	}
}
