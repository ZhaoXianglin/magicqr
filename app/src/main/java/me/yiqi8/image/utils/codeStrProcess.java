package me.yiqi8.image.utils;
import java.net.HttpURLConnection;
import java.net.URL;

public class codeStrProcess {
	public static final int QR_STR_LENGTH = 28;//二维码中数据长度
	public static final String APP_BASE_URL = "yiqi8.me";//默认域名
	/**
	 * 字符串合法性检查
	 * @param result
	 * http://yiqi8.me/V0AAAA123456
	 * @return
	 */
	
	public static boolean checkStr(String result) { 
		result = result.trim();//去除空格
		// 判断字符串长度
		if (result.length() == QR_STR_LENGTH) {
			String sprelult[] = result.split("/");// 字符串以/拆分
			if(APP_BASE_URL.equals(sprelult[2])){
				//System.out.println("域名判断正确");
			}
			else{
				//System.out.println("域名判断失败");
				return false;
			}
			char[] strch =sprelult[3].toCharArray();//12位编号转为数组
			int sum=0;
			for(int i=6;i<12;i++)
				sum+=(strch[i]-'0');//计算校验码和
			if(strch[2]==sum%26+'A'){//判定校验码正确性
				//System.out.println("编码校验正确");
			}
			else{
				//System.out.println("编码校验失败");
				return false;
			}
			return true;
		} 
		else{
			return false;
		}
	}
	
	public static int checkEmpty(String result) {
		String sprelult[] = result.split("/");
		String number=sprelult[3];
        String phpPath = "http://api.yiqi8.me/index.php/Home/Qrcode/check/check_code/";
        //System.out.println(number);
		try{
			URL url = new URL(phpPath +number);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			byte data[] = new byte[512];
			int len = conn.getInputStream().read(data);
			if(len>0){
				String temp = new String(data,0,len).trim();
			    //System.out.println(temp);
				if(temp.equals("1")){
					//System.out.println("该二维码为空");
					return 1;
				}else if(temp.equals("2")){
					//System.out.println("该二维码不为空");
					return 2;
				}else if(temp.equals("data not exist")){
					//System.out.println("该二维码不存在");
					return 0;
				}
			}
			conn.getInputStream().close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	
}
