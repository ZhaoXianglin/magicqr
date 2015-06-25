package me.yiqi8.magicqr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import me.yiqi8.image.utils.EncodingHandler;
import me.yiqi8.internet.InternetUtil;

import org.apache.http.Header;

import com.google.zxing.WriterException;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

public class QRInImageActivity extends Activity {

	public ImageView mselectimg;
	public RadioGroup mselectqrtype;
	public RadioButton mRadioaudio, mRadiovideo, mRadioimage, mRadiomatch;
	public Button mBtnshow, mBtnmake;
	public RelativeLayout mllqrimage;
	private String selectedQrType = "";// 选中的二维码类型
	private String imagepath = "";
	private Bitmap tomakeimg = null;
	private Bitmap baseQr = null;
	private int toMakeImgWidth = 0;
	private int toMakeImgHeight = 0;
	private static int QRSize = 0;

	static String PRO_VIEW_IMAGE = "pro_view_image";
	static String IMAGE_FILE_PATH = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + File.separator + "MagicQR" + File.separator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_image);
		init();
	}

	protected void init() {
		mselectimg = (ImageView) findViewById(R.id.iv_select_img);
		mselectqrtype = (RadioGroup) findViewById(R.id.rg_typeof_qr);
		mRadioaudio = (RadioButton) findViewById(R.id.rb_typeof_qr_audio);
		mRadiovideo = (RadioButton) findViewById(R.id.rb_typeof_qr_video);
		mRadioimage = (RadioButton) findViewById(R.id.rb_typeof_qr_image);
		mRadiomatch = (RadioButton) findViewById(R.id.rb_typeof_qr_match);
		mllqrimage = (RelativeLayout) findViewById(R.id.act_match_imageqr);
		mBtnshow = (Button) findViewById(R.id.btn_match_image_show);
		mBtnmake = (Button) findViewById(R.id.btn_match_image_make);

		// 获取内部二维码图片资源
		Resources res = getResources();
		baseQr = BitmapFactory.decodeResource(res, R.drawable.base_qr);
		//

		// 选择图片
		mselectimg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new PopupWindows(QRInImageActivity.this, mllqrimage);
			}
		});
		// 选择二维码类型
		mselectqrtype.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == mRadiovideo.getId()) {
					selectedQrType = "M";
					Toast.makeText(QRInImageActivity.this,
							"选择视频标签" + selectedQrType, Toast.LENGTH_SHORT)
							.show();
				} else if (checkedId == mRadioaudio.getId()) {
					selectedQrType = "V";
					Toast.makeText(QRInImageActivity.this,
							"选择音频标签" + selectedQrType, Toast.LENGTH_SHORT)
							.show();
				} else if (checkedId == mRadioimage.getId()) {
					selectedQrType = "I";
					Toast.makeText(QRInImageActivity.this,
							"选择图片标签" + selectedQrType, Toast.LENGTH_SHORT)
							.show();
				} else if (checkedId == mRadiomatch.getId()) {
					selectedQrType = "A";
					Toast.makeText(QRInImageActivity.this,
							"选择万能标签" + selectedQrType, Toast.LENGTH_SHORT)
							.show();
				} else {
					selectedQrType = "ERROR";
					Toast.makeText(QRInImageActivity.this, "标签错误，请请选择标签",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		// 合成效果预览
		mBtnshow.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (tomakeimg != null && baseQr != null) {
					toMakeImgHeight = tomakeimg.getHeight();
					toMakeImgWidth = tomakeimg.getWidth();
					if (toMakeImgHeight < 128 || toMakeImgWidth < 128) {
						Toast.makeText(QRInImageActivity.this,
								"图片尺寸过小，无法制作二维码", Toast.LENGTH_SHORT).show();
						return;
					}
					if (toMakeImgWidth > toMakeImgHeight) {
						QRSize = toMakeImgHeight / 8;
					} else {
						QRSize = toMakeImgWidth / 8;
					}
					if (QRSize < 64) {
						QRSize = 64;
					}
					Bitmap tempqr = createBitmapBySize(baseQr, QRSize, QRSize);
					Bitmap tempmadeimg = createBitmapForQRmark(tomakeimg,
							tempqr);
					String temppath  = null;
					try {
						temppath = saveBitmap(tempmadeimg);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Intent intent = new Intent(QRInImageActivity.this,
							ImageQRDetailActivity.class);
					intent.putExtra(PRO_VIEW_IMAGE, temppath);
					startActivity(intent);
				} else {
					Toast.makeText(QRInImageActivity.this, "请先选择图片",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		// 制作标签
		// 需要从远程获取标签内容
		mBtnmake.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (tomakeimg == null) {
					toMakeImgHeight = 0;
					toMakeImgWidth = 0;
					Toast.makeText(QRInImageActivity.this, "请先选择图片",
							Toast.LENGTH_SHORT).show();
					return;
				} else {
					toMakeImgHeight = tomakeimg.getHeight();
					toMakeImgWidth = tomakeimg.getWidth();
				}
				if(selectedQrType.equals("A")){
					Toast.makeText(QRInImageActivity.this, "暂未开放，请重新选择",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (tomakeimg.getWidth() < 128 || tomakeimg.getHeight() < 128) {
					Toast.makeText(QRInImageActivity.this, "图片尺寸过小，无法制作二维码",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (selectedQrType.equals("")) {
					Toast.makeText(QRInImageActivity.this, "请先选择标签类型",
							Toast.LENGTH_SHORT).show();
					return;
				}
				InternetUtil.get("Qrcode/rand_code/type/"+selectedQrType, null,
						new AsyncHttpResponseHandler() {

							@Override
							public void onStart() {
								// TODO Auto-generated method stub
								super.onStart();
								Toast.makeText(QRInImageActivity.this,
										"正在获取魔码……，请稍候", Toast.LENGTH_SHORT)
										.show();
							}

							@Override
							public void onSuccess(int arg0, Header[] arg1,
									byte[] arg2) {
								// TODO Auto-generated method stub
								String codevalue = new String(arg2);// 获取到的码值
								if (toMakeImgWidth > toMakeImgHeight) {
									QRSize = toMakeImgHeight / 8;
								} else {
									QRSize = toMakeImgWidth / 8;
								}
								if (QRSize < 64) {
									QRSize = 64;
								}
								Toast.makeText(QRInImageActivity.this,
										"正在添加魔码……，请稍候"+codevalue, Toast.LENGTH_SHORT)
										.show();
								try {
									Bitmap qrCodeBitmap = EncodingHandler
											.createQRCode("http://yiqi8.me/"+codevalue, QRSize);
									Bitmap tempmadeimg = createBitmapForQRmark(tomakeimg,
											qrCodeBitmap);
									String temppath  = null;
									try {
										temppath = saveBitmap(tempmadeimg);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									//					
									UploadFinalActivity.QRCODE = codevalue;
									
									Intent intent = new Intent(QRInImageActivity.this,
											ImageShareActivity.class);
									intent.putExtra(PRO_VIEW_IMAGE, temppath);
									intent.putExtra("QR_TYPE",selectedQrType);
									startActivity(intent);

								} catch (WriterException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									Toast.makeText(QRInImageActivity.this,
											"魔码生成错误，请检查网络并重试",
											Toast.LENGTH_SHORT).show();
								}
							}

							@Override
							public void onFailure(int arg0, Header[] arg1,
									byte[] arg2, Throwable arg3) {
								// TODO Auto-generated method stub
								Toast.makeText(QRInImageActivity.this,
										"获取失败，请检查网络连接", Toast.LENGTH_SHORT)
										.show();
							}
						});
			}
		});
	}

	/**
	 * 
	 * @title createQR
	 * @description 设置菜单弹出
	 * @author 赵祥麟(Jarvis)
	 * @date 2014-9-18
	 */
	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {
			View view = View
					.inflate(mContext, R.layout.item_popupwindows, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_ins));
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_bottom_in_2));

			setWidth(LayoutParams.FILL_PARENT);
			setHeight(LayoutParams.FILL_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();
			Button bt1 = (Button) view
					.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view
					.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view
					.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					photo();
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					/*
					 * try { // 选择照片的时候也一样，我们用Action为Intent.ACTION_GET_CONTENT，
					 * // 有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个 Intent intent =
					 * new Intent(); intent.setType("image/*");
					 * intent.setAction(Intent.ACTION_GET_CONTENT);
					 * startActivityForResult(intent, 2); } catch
					 * (ActivityNotFoundException e) {}
					 */
					Intent pickimgintent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(pickimgintent, RESULT_LOAD_IMAGE);
					dismiss();
				}
			});
			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});

		}
	}

	private static final int TAKE_PICTURE = 0x000000;
	private static final int RESULT_LOAD_IMAGE = 0x000001;

	public void photo() {
		
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File path = new File(IMAGE_FILE_PATH);
		if(!path.exists()){
			path.mkdirs();
		}
		File file = new File(Environment.getExternalStorageDirectory()
				+ "/MagicQR/", String.valueOf(System.currentTimeMillis())
				+ ".jpg");
		imagepath = file.getPath();
		Uri imageUri = Uri.fromFile(file);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	@SuppressLint("NewApi")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case TAKE_PICTURE:
				Bitmap bmp = BitmapFactory.decodeFile(imagepath);
				mselectimg.setImageBitmap(bmp);
				mselectimg.setBackground(null);
				tomakeimg = bmp;
				break;
			case RESULT_LOAD_IMAGE:
				if (null != data) {
					Uri selectedImage = data.getData();
					String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Cursor cursor = getContentResolver().query(selectedImage,
							filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					imagepath = cursor.getString(columnIndex);
					cursor.close();

					mselectimg.setImageBitmap(BitmapFactory
							.decodeFile(imagepath));
					mselectimg.setBackground(null);
					tomakeimg = BitmapFactory.decodeFile(imagepath);
					System.out.println("制作标签");
				}
				break;
			default:
				break;
			}
		} else {
			return;
		}
	}

	/**
	 * 水印
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createBitmapForQRmark(Bitmap src, Bitmap qrmark) {
		if (src == null) {
			return null;
		}
		int srcwidth = src.getWidth();
		int srcheight = src.getHeight();
		// create the new blank bitmap
		Bitmap newbtp = Bitmap.createBitmap(srcwidth, srcheight,
				Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas(newbtp);
		// draw src into
		cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
		// draw watermark into
		cv.drawBitmap(qrmark, srcwidth - QRSize - 22, srcheight - QRSize - 22,
				null);// 在src的右下角画入水印
		// save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		// store
		cv.restore();// 存储
		return newbtp;
	}

	/**
	 * 将Bitmap转换成指定大小
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap createBitmapBySize(Bitmap bitmap, int width, int height) {
		return Bitmap.createScaledBitmap(bitmap, width, height, true);
	}

	/**
	 * 保存图片为JPEG
	 * 
	 * @param bitmap
	 * @param path
	 */
	public static void saveJPGE_After(Bitmap bitmap, String path) {
		File file = new File(path);
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param bitmap
	 * @return String 图片文件路径
	 * @throws IOException
	 */
	public String saveBitmap(Bitmap bitmap) throws IOException{
		File path = new File(IMAGE_FILE_PATH);
		if(!path.exists()){
			path.mkdirs();
		}
		String image = IMAGE_FILE_PATH+"temp_img.jpg";
		File temp_image = new File(image);
		if(temp_image.exists()){
			temp_image.delete();
		}
		temp_image.createNewFile();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		bitmap.compress(CompressFormat.JPEG, 100, bos); 
		byte[] bitmapdata = bos.toByteArray(); 
		FileOutputStream fos = new FileOutputStream(temp_image);
		fos.write(bitmapdata);
		return image;
	}
}
