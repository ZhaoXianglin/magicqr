package me.yiqi8.image.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MylabOperate {
	private static final String TABLENAME ="tab";
	private SQLiteDatabase db=null;
	public MylabOperate(SQLiteDatabase db){
		this.db = db;
	}
	public void insert(String code,String url,String title,String content){
		String sql = "INSERT INTO "+TABLENAME + "(code,url,title,content)VALUES('"+code+"','"+url+"','"+title+"','"+content+"')";
		this.db.execSQL(sql);
		this.db.close();
	}
	public void delete(String code){
		String sql="DELETE FROM " + TABLENAME + " WHERE code='" +code+"'";
		this.db.execSQL(sql);
		this.db.close();
	}
	public Set<String> findAll(){
		Set<String> all = new TreeSet<String>();
		String sql = "SELECT title FROM tab";
		Cursor result = this.db.rawQuery(sql, null);
		for(result.moveToFirst();!result.isAfterLast();result.moveToNext()){
			all.add(result.getString(0));
		}
		this.db.close();
		return all;
	}
	public Map<String,String> findBytitle(String title){
		Map<String, String> map = new HashMap<String,String>();
		String sql = "SELECT code,url,content FROM tab where title='"+title+"'";
		Cursor result = this.db.rawQuery(sql, null);
		result.moveToFirst();
		map.put("code", result.getString(0));
		map.put("url", result.getString(1));
		map.put("content", result.getString(2));
		return map;
	}
}