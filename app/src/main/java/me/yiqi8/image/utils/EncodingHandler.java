package me.yiqi8.image.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.Color;

import java.util.Hashtable;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public final class EncodingHandler {
	private static final int BLACK = 0xff000000;
	private static final int WRITE = 0xffffffff;
	public static Bitmap createQRCode(String str, int widthAndHeight)
			throws WriterException {
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		BitMatrix matrix = new MultiFormatWriter().encode(str,
				BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		int[]background = new int[width*height];
		int[] pixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				background[y * width + x] = WRITE;
				if (matrix.get(x, y)) {
					pixels[y * width + x] = BLACK;
				}
			}
		}
	
		
		Bitmap newbtp = Bitmap.createBitmap(width, height,Config.ARGB_8888);// 创建一个新白底位图
		Canvas cv = new Canvas(newbtp);
		// draw src into
		int whiteColor = Color.argb(128, 255, 255, 255);
		cv.drawColor(whiteColor);// 设置画布背景为白色
		// draw watermark into
		cv.drawBitmap(pixels, 0, width, 0, 0, width, height, true,null);
		// save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		// store
		cv.restore();// 存储
		return newbtp;
	}
}
