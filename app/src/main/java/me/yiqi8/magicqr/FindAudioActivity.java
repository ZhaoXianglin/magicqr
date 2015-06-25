package me.yiqi8.magicqr;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.app.Activity;
import android.content.Intent;
public class FindAudioActivity extends Activity {
	ListView lv =null;
	List<Map<String,String>> list=null;
	List<String> pathlist =null;
	File file = null;
	Map<String,String> map = null;
	private SimpleAdapter simpleAdapter = null; // 进行数据的转换操作
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_audio);
		lv = (ListView)super.findViewById(R.id.audio_musiclist);
		file = Environment.getExternalStorageDirectory();
		list = new ArrayList<Map<String,String>>();
		pathlist = new ArrayList<String>();
		searchmusic(file);
		this.simpleAdapter = new SimpleAdapter(this,this.list,R.layout.musiclist,
												new String[]{"music_type_image","music_name"},
												new int[]{R.id.music_type_image,R.id.music_name});
		this.lv.setAdapter(simpleAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0,View arg1, int position,
					long id) {
				String path = pathlist.get(position);
				Intent it = FindAudioActivity.this.getIntent();
				it.putExtra("ab_path", path);
				FindAudioActivity.this.setResult(RESULT_OK, FindAudioActivity.this.getIntent());
				FindAudioActivity.this.finish();
			}
		});
	}

	public void searchmusic(File file){	// 递归调用
		if(file == null||file.listFiles()==null){
			return;
		}
		for(File filex:file.listFiles()){
			if(filex.isDirectory())  //如果是目錄，將目錄作為參數再次調用方法
			{
				searchmusic(filex); 
			}
			else{
				String filename = filex.getName();
				if(filename.endsWith(".mp3")){
					map = new HashMap<String,String>();
					map.put("music_type_image",String.valueOf(R.drawable.audio_icon_mp3));
					map.put("music_name", filename);
					list.add(map);
					pathlist.add(filex.getAbsolutePath());
				}
			}
		}
	}
}
