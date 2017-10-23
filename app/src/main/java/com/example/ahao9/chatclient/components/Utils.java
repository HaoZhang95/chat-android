package com.example.ahao9.chatclient.components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
	public static void showLongToast(Context context, String pMsg) {
		Toast.makeText(context, pMsg, Toast.LENGTH_LONG).show();
	}

	public static void showShortToast(Context context, String pMsg) {
		Toast.makeText(context, pMsg, Toast.LENGTH_SHORT).show();
	}

	public static String getTime(){
		Long timeStamp = System.currentTimeMillis();  //获取当前时间戳
		SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
		//SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp))));   // 时间戳转换成时间

		return time;
	}

	public static void start_Activity(Context context,Class<? extends Activity> nextActivity) {
		Intent intent = new Intent();
		intent.setClass(context, nextActivity);
		context.startActivity(intent);
	}

	public static void start_Activity(Context context,Class<? extends Activity> nextActivity,String message) {
		Intent intent = new Intent();
		intent.setClass(context, nextActivity);

		intent.putExtra(message,message);
		context.startActivity(intent);
	}
}
