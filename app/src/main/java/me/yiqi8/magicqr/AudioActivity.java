package me.yiqi8.magicqr;

import java.io.File;
import java.io.IOException;
import me.yiqi8.magicqr.R;
import me.yiqi8.qrcode.audio.*;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
/******************************************************************
 * 这是一个临时的为音频标签添加音频内容的的Activity，暂时可由“menu-临时”跳转至此，界面布局文件是activity_audio.xml
 * 它的功能分为录音，选择本地音乐，播放组成
 * 录制部分模仿微信，格式为amr，录制过程中有动画效果，时长限制可随时根据需要添加，具体在recordThread部分有注释
 * 选择本地文件功能完全原创，点击本地添加的按钮后会跳转到另一个界面，界面内会列出内存卡中所有符合要求的音频文件
 * 由于此activity意在为标签添加内容而非欣赏，所以播放按钮是单次无暂停功能的
 * 此代码会在服务器端接口提供完成之后添加“完成”按钮，上传文件，结束activity
 * *******************************************************************/
public class AudioActivity extends Activity {
	ProgressBar progress = null;
	ImageView play = null;
	ImageButton addlocal = null;
	ImageButton addrecord = null;
	ImageView mIvRecVolume = null;
	Intent it =null;//跳转和接收FindAudioActivity的信息
	int src = NORESOURCE;
	String localpath = null;//保存本地路径
	private Dialog mRecordDialog;
	private AudioRecorder mAudioRecorder;
	private TextView mTvRecordDialogTxt;
	private static final int MAX_RECORD_TIME=180;
	MediaPlayer mMediaPlayer;
	ProgressChanger pc = null;
	private static final int MIN_RECORD_TIME = 1; // 最短录制时间，单位秒，0为无时间限制
    private static final int RECORD_OFF = 0; //不在录音
    private static final int RECORD_ON = 1; // 正在录音
    private static final int ISRECORD = 10;
    private static final int ISLOCAL = 15;
    private static final int NORESOURCE = 20;
    private static final String RECORD_FILENAME = "record0033"; // 录音文件名
	private int recordState = 0; //录音状态״̬
    private float recodeTime = 0.0f; //录音时长
    private double voiceValue = 0.0; // 录音的音量
    private boolean playState = false; // 录音的播放状
    private boolean moveState = false; //手指是否移动
    private float downY;
	private Thread mRecordThread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio);
		progress = (ProgressBar)super.findViewById(R.id.audio_progressBar1);
		play = (ImageView)super.findViewById(R.id.audio_imageButton1);
		addrecord = (ImageButton)super.findViewById(R.id.audio_recode);
		Button back = (Button)super.findViewById(R.id.audio_record_back);
		TextView next = (TextView)super.findViewById(R.id.audio_record_next);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent(AudioActivity.this,UploadFinalActivity.class);
				it.putExtra("type", UploadFinalActivity.TYPE_MUC);
				it.putExtra("path", Environment.getExternalStorageDirectory()+
		                "/MagicQR/" + RECORD_FILENAME + ".aac"); // 跳转至上传界面
				startActivity(it);
				finish();
			}
		});
		play.setOnClickListener(new OnClickListener() {

			@Override
            public void onClick(View v) {
				//播放录音
				if(src == ISRECORD){
	                if (!playState) {
	                    mMediaPlayer = new MediaPlayer();
	                    try {
	                        mMediaPlayer.setDataSource(getAmrPath());
	                        mMediaPlayer.prepare();
	                        playState = true;
	                        mMediaPlayer.start();
	                        pc = new ProgressChanger();
	                        pc.start();
	                        Toast.makeText(getApplicationContext(), "正在播放录音",
                       		     Toast.LENGTH_SHORT).show();
	                        // 开始播放
	                        mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
	
	                            @Override
	                            public void onCompletion(MediaPlayer mp) {
	                                if (playState) {
	                                    playState = false;
	                                }
	                            }
	                        });
	                    } catch (IllegalArgumentException e) {
	                        e.printStackTrace();
	                    } catch (IllegalStateException e) {
	                        e.printStackTrace();
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }
	
	                } else {
	                    if (mMediaPlayer.isPlaying()) {
	                        mMediaPlayer.stop();
	                        playState = false;
	                    } else {
	                        playState = false;
	                    }
	                }
				}
				//播放本地文件
				/*if(src == ISLOCAL){
					Toast.makeText(getApplicationContext(), "正在播放本地文件",
               		     Toast.LENGTH_SHORT).show();
					if (!playState) {
	                    mMediaPlayer = new MediaPlayer();
	                    try {
	                        mMediaPlayer.setDataSource(localpath);
	                        mMediaPlayer.prepare();
	                        playState = true;
	                        mMediaPlayer.start();
	                        pc = new ProgressChanger();
	                        pc.start();
	                        mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
	
	                            @Override
	                            public void onCompletion(MediaPlayer mp) {
	                                if (playState) {
	                                    playState = false;
	                                }
	                            }
	                        });
	                    } catch (IllegalArgumentException e) {
	                        e.printStackTrace();
	                    } catch (IllegalStateException e) {
	                        e.printStackTrace();
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }
	
	                } else {
	                    if (mMediaPlayer.isPlaying()) {
	                        mMediaPlayer.stop();
	                        playState = false;
	                    } else {
	                        playState = false;
	                    }
	                }
				}*/
				if(src == NORESOURCE){
					Toast.makeText(getApplicationContext(), "未添加资源",
               		     Toast.LENGTH_SHORT).show();
				}
            }//onclik
			
			
			
        });
		
		/*录音键的监听设置
		 * 录音中途可取消，录音时长限制尚未添加
		 */
		addrecord.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()){
				case MotionEvent.ACTION_DOWN: //按下，删除原文件，录音启动
                    if (recordState != RECORD_ON) {
                        downY = event.getY();
                        deleteOldFile();
                        mAudioRecorder = new AudioRecorder(RECORD_FILENAME);
                        recordState = RECORD_ON; 
                        try {
                            mAudioRecorder.start();
                            recordTimethread();
                            showVoiceDialog(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
				case MotionEvent.ACTION_MOVE: //用户手指移动
                    float moveY = event.getY();
                    if (moveY - downY > 50) {
                        moveState = true;
                        showVoiceDialog(1);
                    }
                    if (moveY - downY < 20) {
                        moveState = false;
                        showVoiceDialog(0);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP: // 抬起
                    if (recordState == RECORD_ON) {
                        recordState = RECORD_OFF;
                        if (mRecordDialog.isShowing()) {
                            mRecordDialog.dismiss();
                        }
                        try {
                            mAudioRecorder.stop();
                            mRecordThread.interrupt();
                            voiceValue = 0.0;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (!moveState) {
                            if (recodeTime < MIN_RECORD_TIME) {
                            	Toast.makeText(getApplicationContext(), "时间过短录音失败",
                            		     Toast.LENGTH_SHORT).show();
                            }else if(recodeTime > MAX_RECORD_TIME){
                            	Toast.makeText(getApplicationContext(), "录音时间过长请重新录制",
                           		     Toast.LENGTH_SHORT).show();
                            }else{
                            	src = ISRECORD;
                            } 
                        }
                        moveState = false;
                    }
                    break;	
				}
				return false;
			}
		});
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK){
			this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(resultCode){
		case  RESULT_OK:
		case RESULT_CANCELED:
			Toast.makeText(getApplicationContext(), "没有选择歌曲",
       		     Toast.LENGTH_SHORT).show();
			break;
		default:break;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	void deleteOldFile() {
        File file = new File(Environment.getExternalStorageDirectory(),
                "MagicQR/" + RECORD_FILENAME + ".aac");
        if (file.exists()) {
            file.delete();
        }
    }
	private String getAmrPath() {
        File file = new File(Environment.getExternalStorageDirectory(),
                "MagicQR/" + RECORD_FILENAME + ".aac");
        return file.getAbsolutePath();
    }
	
	void recordTimethread() {
        mRecordThread = new Thread(recordThread);
        mRecordThread.start();
    }
	
	void setDialogImage() {
        if (voiceValue < 600.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_01);
        } else if (voiceValue > 600.0 && voiceValue < 1000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_02);
        } else if (voiceValue > 1000.0 && voiceValue < 1200.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_03);
        } else if (voiceValue > 1200.0 && voiceValue < 1400.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_04);
        } else if (voiceValue > 1400.0 && voiceValue < 1600.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_05);
        } else if (voiceValue > 1600.0 && voiceValue < 1800.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_06);
        } else if (voiceValue > 1800.0 && voiceValue < 2000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_07);
        } else if (voiceValue > 2000.0 && voiceValue < 3000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_08);
        } else if (voiceValue > 3000.0 && voiceValue < 4000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_09);
        } else if (voiceValue > 4000.0 && voiceValue < 6000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_10);
        } else if (voiceValue > 6000.0 && voiceValue < 8000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_11);
        } else if (voiceValue > 8000.0 && voiceValue < 10000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_12);
        } else if (voiceValue > 10000.0 && voiceValue < 12000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_13);
        } else if (voiceValue > 12000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_14);
        }
    }
	
	void showVoiceDialog(int flag) {
        if (mRecordDialog == null) {
            mRecordDialog = new Dialog(AudioActivity.this, R.style.DialogStyle);
            mRecordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mRecordDialog.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mRecordDialog.setContentView(R.layout.record_dialog);
            mIvRecVolume = (ImageView) mRecordDialog.findViewById(R.id.record_dialog_img);
            mTvRecordDialogTxt = (TextView) mRecordDialog.findViewById(R.id.record_dialog_txt);
        }
        switch (flag) {
        case 1:
            mIvRecVolume.setImageResource(R.drawable.record_cancel);
            mTvRecordDialogTxt.setText("松开手指可取消录音");
            break;

        default:
            mIvRecVolume.setImageResource(R.drawable.record_animate_01);
            mTvRecordDialogTxt.setText("向下滑动可取消录音");
            break;
        }
        mTvRecordDialogTxt.setTextSize(14);
        mRecordDialog.show();
    }
	private Runnable recordThread = new Runnable() {

        @Override
        public void run() {
            recodeTime = 0.0f;
            while (recordState == RECORD_ON) {
                // 限制录音时长
                 //if (recodeTime >= MAX_RECORD_TIME && MAX_RECORD_TIME != 0) {
                // imgHandle.sendEmptyMessage(0);
               //  } else
                {
                    try {
                        Thread.sleep(150);
                        recodeTime += 0.15;
                        // 获取音量，更新dialog
                        if (!moveState) {
                            voiceValue = mAudioRecorder.getAmplitude();
                            recordHandler.sendEmptyMessage(1);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };
    @SuppressLint("HandlerLeak")
	public Handler recordHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            setDialogImage();
        }
    };
    class ProgressChanger extends Thread{//这是用于控制进度条的线程
    	@Override
		public void run(){
    		AudioActivity.this.progress.setMax(AudioActivity.this.mMediaPlayer.getDuration());
    		while(playState){
    			AudioActivity.this.progress.setProgress(AudioActivity.this.mMediaPlayer.getCurrentPosition());
    			try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    } 
}

