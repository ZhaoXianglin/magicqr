package me.yiqi8.magicqr;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.slidingmenu.lib.SlidingMenu;
import com.tencent.stat.StatConfig;
import com.tencent.stat.common.StatLogger;

public class MainActivity extends BaseActivity {

	private long exitTime = 0;
	private static StatLogger logger = new StatLogger("MagicQR");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 腾讯云分析
		android.os.Debug.startMethodTracing("MTA");
		// 打开debug开关，可查看mta上报日志或错误
		// 发布时，请务必要删除本行或设为false
		StatConfig.setDebugEnable(true);
		// 获取MTA MID等信息
		// 用户自定义UserId
		// StatConfig.setCustomUserId(this, "1234");
		java.util.UUID.randomUUID();

		initActivity();

		
	}

	/**
	 * 初始化侧边栏
	 */
	public void initSlidingMenu() {
		// configure the SlidingMenu
		SlidingMenu menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		// 设置触摸屏幕的模式
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);

		// 设置滑动菜单视图的宽度
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// 设置渐入渐出效果的值
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		// 为侧滑菜单设置布局
		menu.setMenu(R.layout.leftmenu);

	}

	/*
     * 初始化
     */
	private void initActivity() {
		// initSlidingMenu();//初始化侧边栏

		RelativeLayout mscanQR = (RelativeLayout) findViewById(R.id.main_btn_scanQR);
		RelativeLayout mmakeQR = (RelativeLayout) findViewById(R.id.main_btn_makeQR);
		RelativeLayout maboutMe = (RelativeLayout) findViewById(R.id.main_btn_aboutme);
		RelativeLayout mhelp = (RelativeLayout) findViewById(R.id.main_btn_help);

		mscanQR.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this, CaptureActivity.class);
				startActivity(i);
			}

		});

		mmakeQR.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this,
						QRInImageActivity.class);
				startActivity(i);
			}
		});
		maboutMe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this, SaveDataActivity.class);
				startActivity(i);
			}
		});
		mhelp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this, ExplainActivity.class);
				startActivity(i);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 腾讯云分析
	 */
	/**
	 * 腾讯云分析
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		StatService.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		StatService.onResume(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
