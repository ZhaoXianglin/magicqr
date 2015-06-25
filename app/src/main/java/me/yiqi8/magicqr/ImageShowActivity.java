package me.yiqi8.magicqr;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageShowActivity extends Activity {
	private Button mBackButton;
	private EditText mShowTitle;
	private EditText mShowContent;
	private GridView mGridImage;
	private String[] marr;
	private String[] mImageUrl;
	private String mTitle;
	private String mContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_iamge_get_show);
		init();
	}
	
	public void init(){
		
		int length = getIntent().getStringArrayExtra("QR_REL_CONTENT").length;
		marr=new String[length];  
		for(int i = 0;i<length;i++){
			marr[i] = getIntent().getStringArrayExtra("QR_REL_CONTENT")[i];
		}
		mImageUrl = new String[length-2];
		for(int i = 0;i<length-2;i++){
			mImageUrl[i] = marr[i];
		}
		mTitle = marr[length-2];
		mContent = marr[length-1];
		mBackButton = (Button)findViewById(R.id.act_iamge_header_btn_back);
		mShowTitle = (EditText)findViewById(R.id.et_image_title);
		mShowContent = (EditText)findViewById(R.id.et_image_content);
		
		mShowContent.setText(mContent);
		mShowTitle.setText(mTitle);
		
		mGridImage = (GridView)findViewById(R.id.gv_img_get_show);
		mGridImage.setSelector(new ColorDrawable(Color.TRANSPARENT));//设置背景
		mGridImage.setAdapter(new GridAdapter(mImageUrl, ImageShowActivity.this));
		mGridImage.setOnItemClickListener(new OnItemClickListener() {
		
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				imageBrower(position,mImageUrl);
			}
		});
		
		//返回按钮
		mBackButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	//图片显示的Adapter
	public class GridAdapter extends BaseAdapter {
		private String[] files;
		private LayoutInflater mLayoutInflater;
		
		public GridAdapter(String[] files, Context context) {
			this.files = files;
			mLayoutInflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			return files == null ? 0 : files.length;
		}

		@Override
		public String getItem(int position) {
			return files[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView mImageView;
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.gridview_item,
						parent, false);
				mImageView = (ImageView) convertView.findViewById(R.id.album_image);
				convertView.setTag(mImageView);
			} else {
				mImageView = (ImageView) convertView.getTag();
			}
			String url = getItem(position);
			
			ImageLoader.getInstance().displayImage(url, mImageView);
			return convertView;
		}
		
	}
	
	private void imageBrower(int position, String[] urls) {
		Intent intent = new Intent(ImageShowActivity.this, ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		startActivity(intent);
	}
}
