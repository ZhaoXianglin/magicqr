package me.yiqi8.qrcode.decode;

import android.app.Activity;
import android.content.DialogInterface;

/**
 * 作者: 陈涛(1076559197@qq.com)
 * 
 * 时间: 2014年5月9日 下午12:24:51
 *
 * 版本: V_1.0.0
 *
 */
public final class FinishListener
    implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener, Runnable {

  private final Activity activityToFinish;

  public FinishListener(Activity activityToFinish) {
    this.activityToFinish = activityToFinish;
  }

  @Override
public void onCancel(DialogInterface dialogInterface) {
    run();
  }

  @Override
public void onClick(DialogInterface dialogInterface, int i) {
    run();
  }

  @Override
public void run() {
    activityToFinish.finish();
  }

}
