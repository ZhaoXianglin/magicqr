package me.yiqi8.magicqr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import me.yiqi8.image.photoview.PhotoView;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ImageQRDetailActivity extends Activity {

	private Bitmap mProImage;
	private Button mBtnBack;
	private PhotoView mPhotoview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_detail);
		mPhotoview = (PhotoView) findViewById(R.id.iv_pro_view);
		mBtnBack = (Button) findViewById(R.id.act_iamge_header_btn_back);

		Intent intent = getIntent();
		if (intent != null) {
			String imagePath = intent.getStringExtra(QRInImageActivity.PRO_VIEW_IMAGE);
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(imagePath);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mProImage = BitmapFactory.decodeStream(fis);
			mPhotoview.setImageBitmap(mProImage);
		}
		
		mBtnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
}
