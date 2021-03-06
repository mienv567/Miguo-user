package com.fanwe.library.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.fanwe.library.SDLibrary;

/**
 * package包工具类
 * 
 * @author zhengjun
 * 
 */
public class SDPackageUtil
{
	static  String app_version_name ;

	public static void chmod(String permission, String path)
	{
		try
		{
			String command = "chmod " + permission + " " + path;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static Boolean isPackageExist(String packageName)
	{
		PackageManager manager = SDLibrary.getInstance().getApplication().getPackageManager();
		List<PackageInfo> pkgList = manager.getInstalledPackages(0);
		for (PackageInfo pi : pkgList)
		{
			if (pi.packageName.equalsIgnoreCase(packageName))
			{
				return true;
			}
		}
		return false;
	}

	public static PackageInfo getApkPackageInfo(String apkFilePath)
	{
		PackageManager pm = SDLibrary.getInstance().getApplication().getPackageManager();
		PackageInfo apkInfo = pm.getPackageArchiveInfo(apkFilePath, PackageManager.GET_META_DATA);
		return apkInfo;
	}

	public static PackageInfo getPackageInfo(String packageName)
	{
		PackageManager pm = SDLibrary.getInstance().getApplication().getPackageManager();
		PackageInfo apkInfo = null;
		try
		{
			apkInfo = pm.getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return apkInfo;
	}

	public static PackageInfo getCurrentPackageInfo()
	{
		return getPackageInfo(getPackageName());
	}

	public static Boolean installApkPackage(String apkFilePath) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse("file://" + apkFilePath), "application/vnd.android.package-archive");
		SDLibrary.getInstance().getApplication().startActivity(intent);
		return true;
	}

	public static boolean installApkPackage(Context context, String apkPath){
		if (context == null || TextUtils.isEmpty(apkPath)) {
			return false;
		}

		File file = new File(apkPath);
		Intent intent = new Intent(Intent.ACTION_VIEW);

		//判读版本是否在7.0以上
		if (Build.VERSION.SDK_INT >= 24) {
			//provider authorities
			Uri apkUri = FileProvider.getUriForFile(context, "com.fanwe.o2o.miguo.apkprovider", file);
			//Granting Temporary Permissions to a URI
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
			context.startActivity(intent);
			return true;
		}
		return installApkPackage(apkPath);
	}

	public static Bundle getMetaData(Context context)
	{
		try
		{
			ApplicationInfo info = SDLibrary.getInstance().getApplication().getPackageManager()
					.getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
			return info.metaData;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static String getPackageName()
	{
		return SDLibrary.getInstance().getApplication().getPackageName();
	}

	public static void startAPP(String appPackageName)
	{
		try
		{
			Intent intent = SDLibrary.getInstance().getApplication().getPackageManager().getLaunchIntentForPackage(appPackageName);
			SDLibrary.getInstance().getApplication().startActivity(intent);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void startCurrentApp()
	{
		startAPP(SDLibrary.getInstance().getApplication().getPackageName());
	}

	public static boolean isBackground()
	{
		ActivityManager activityManager = (ActivityManager) SDLibrary.getInstance().getApplication().getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses)
		{
			if (appProcess.processName.equals(getPackageName()))
			{
				if (appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
				{
					return true;
				} else
				{
					return false;
				}
			}
		}
		return false;
	}

	public static int getVersionCode()
	{
		PackageInfo pi = getCurrentPackageInfo();
		return pi.versionCode;
	}

	public static String getVersionName()
	{
		PackageInfo pi = getCurrentPackageInfo();
		app_version_name = pi.versionName;
		return pi.versionName;
	}

	public static String getApp_version_name() {
		return app_version_name;
	}

	public static void setApp_version_name(String app_version_name) {
		SDPackageUtil.app_version_name = app_version_name;
	}
}
