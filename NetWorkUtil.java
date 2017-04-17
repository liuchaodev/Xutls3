package com.welink.worker.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetWorkUtil {
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService("connectivity");
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService("connectivity");
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(1);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public static boolean is3G(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService("connectivity");
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if ((activeNetInfo != null) && (activeNetInfo.getType() == 0)) {
			return true;
		}
		return false;
	}

	public static boolean is2G(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService("connectivity");
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if ((activeNetInfo != null)
				&& ((activeNetInfo.getSubtype() == 2)
						|| (activeNetInfo.getSubtype() == 1) || (activeNetInfo
						.getSubtype() == 4))) {
			return true;
		}
		return false;
	}

	public static boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService("connectivity");
			NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(0);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public static boolean isWifiEnabled(Context context) {
		ConnectivityManager mgrConn = (ConnectivityManager) context
				.getSystemService("connectivity");
		TelephonyManager mgrTel = (TelephonyManager) context
				.getSystemService("phone");
		return ((mgrConn.getActiveNetworkInfo() != null) && (mgrConn
				.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED))
				|| (mgrTel.getNetworkType() == 3);
	}

	public static int getConnectedType(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService("connectivity");
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if ((mNetworkInfo != null) && (mNetworkInfo.isAvailable())) {
				return mNetworkInfo.getType();
			}
		}
		return -1;
	}

	public static String GetHostIp() {
		try {
			Enumeration en = NetworkInterface.getNetworkInterfaces();
			Enumeration ipAddr;
			for (; en.hasMoreElements(); ipAddr.hasMoreElements()) {
				NetworkInterface intf = (NetworkInterface) en.nextElement();
				ipAddr = intf.getInetAddresses();
				InetAddress inetAddress = (InetAddress) ipAddr.nextElement();
				if (!inetAddress.isLoopbackAddress())
					return inetAddress.getHostAddress();
			}
		} catch (SocketException localSocketException) {
		} catch (Exception localException) {
		}
		return null;
	}

	public static String getIMEI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService("phone");
		return telephonyManager.getDeviceId();
	}

	public static boolean isNetworkAvailable(Context activity)
	{
		Context context = activity.getApplicationContext();
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager == null)
		{
			return false;
		}
		else
		{
			// 获取NetworkInfo对象
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

			if (networkInfo != null && networkInfo.length > 0)
			{
				for (int i = 0; i < networkInfo.length; i++)
				{
					System.out.println(i + "===状态===" + networkInfo[i].getState());
					System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
					// 判断当前网络状态是否为连接状态
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}
				}
			}
		}
		return false;
	}
}