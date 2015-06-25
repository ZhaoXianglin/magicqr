package me.yiqi8.qrcode.vedio;

import me.yiqi8.magicqr.CameraVideo;
import me.yiqi8.magicqr.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

public class VedioNextWindow extends PopupWindow {
	private Button next;
	private Button restart;
	private View mMenuView;
	private Button preview;

	public VedioNextWindow(final CameraVideo context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.vedio_next_step, null);
		next = (Button) mMenuView.findViewById(R.id.btn_vedio_next);
		restart = (Button) mMenuView.findViewById(R.id.btn_vedio_restart);
		//preview = (Button) mMenuView.findViewById(R.id.btn_vedio_preview);
		/*
		 * btn_cancel.setOnClickListener(new OnClickListener() {
		 * 
		 * public void onClick(View v) { //销毁弹出框 dismiss(); } });
		 */
		// 设置SelectPicPopupWindow的View
				this.setContentView(mMenuView);
				// 设置SelectPicPopupWindow弹出窗体的宽
				this.setWidth(LayoutParams.FILL_PARENT);
				// 设置SelectPicPopupWindow弹出窗体的高
				this.setHeight(LayoutParams.WRAP_CONTENT);
				// 设置SelectPicPopupWindow弹出窗体可点击
				this.setFocusable(true);
				// 设置SelectPicPopupWindow弹出窗体动画效果
				// this.setAnimationStyle(R.style.AnimBottom);
				// 实例化一个ColorDrawable颜色为半透明
				ColorDrawable dw = new ColorDrawable(0xb0000000);
				// 设置SelectPicPopupWindow弹出窗体的背景
				this.setBackgroundDrawable(dw);
				// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		restart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Message msg = new Message();
				msg.what = 1;
				context.myHandler.sendMessage(msg);
			}
		});
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Message msg = new Message();
				msg.what = 2;
				context.myHandler.sendMessage(msg);
			}
		});
	}

	public Button getPreview() {
		return this.preview;
	}

	public Button getNextBtn() {
		return this.next;
	}

	public Button getRestartBtn() {
		return this.restart;
	}

}

