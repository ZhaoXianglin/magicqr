package me.yiqi8.qrcode.string;

/**
* @title 解码内容检查类
* @description 
* @author 赵祥麟(Jarvis)
* @date 2014-8-14
*/
public class codeStrProcess {
	public static final int QR_STR_LENGTH = 18;//二维码中数据长度
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
				System.out.print("判断正确");
			}
			for(int i = 0;i<sprelult.length;i++){
				
				System.out.println(sprelult[i]);
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 远程校验标签合法性
	 */
	public static boolean checkTagUse(){
		return false;
	}
	
	/**
	 * 根据字符串发起http请求
	 */
	public static void sendHttpRequire(){
		
	}
	
	/**
	 * 根据字符串启动Acivity
	 */
	public static void requireActivity(){
		
	}
}

