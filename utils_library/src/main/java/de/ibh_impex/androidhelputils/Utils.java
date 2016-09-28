package de.ibh_impex.androidhelputils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.TimeZone;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Environment;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.util.Log;

public class Utils {

	private static final String TAG = "Utils";
	public static final int MAUS_DATE_TYPE = 2;
	public static final int SHORT_DATE_TYPE = 1;
	public static final int LONG_DATE_TYPE = 0;
	public static final int ONLY_TIME_TYPE = 3;
	public static final int EXTENDED_ONLY_TIME = 4;
	public static final String SD_CARD = "sdCard";
	public static final String EXTERNAL_SD_CARD = Environment.getExternalStorageDirectory().toString(); //"externalSdCard"; //Environment.getExternalStorageDirectory().toString(); //"externalSdCard";
	public static final String EXTERNAL_SD_CARD_2 = "externalSdCard"; //"external_SD"; //"externalSdCard";

	private static final double radiusOfEarth = 6378137; //meters

	public static float getBatteryLevel(Context ctx)
	{
		Intent batteryIntent = ctx.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		if (level == -1 || scale == -1)
			return -1;
		else
			return ((float) level / (float) scale) * 100.0f;
	}

	public static String dateToStr(GregorianCalendar cal, int type)
	{
		if (cal != null)
		{
			int year = cal.get(GregorianCalendar.YEAR) - 2000;
			int month = cal.get(GregorianCalendar.MONTH) + 1;	//zero based
			int day = cal.get(GregorianCalendar.DAY_OF_MONTH);
			int hour = cal.get(GregorianCalendar.HOUR_OF_DAY);
			int minute = cal.get(GregorianCalendar.MINUTE);
			int second = cal.get(GregorianCalendar.SECOND);

			String date = "";
			switch (type)
			{
			case LONG_DATE_TYPE:
				date = (day > 9 ? String.valueOf(day) : ("0"+day)) + ".";
				date += (month > 9 ? String.valueOf(month) : ("0"+month)) + ".";
				date += (year > 9 ? String.valueOf(year) : ("0"+year)) + " ";
				date += (hour > 9 ? String.valueOf(hour) : ("0"+hour)) + ":";
				date += (minute > 9 ? String.valueOf(minute) : ("0"+minute)) + ":";
				date += (second > 9 ? String.valueOf(second) : ("0"+second));
				break;
			case SHORT_DATE_TYPE: 
				date = (day > 9 ? String.valueOf(day) : ("0"+day)) + ".";
				date += (month > 9 ? String.valueOf(month) : ("0"+month)) + ".";
				date += (year > 9 ? String.valueOf(year) : ("0"+year)) + " - ";
				date += (hour > 9 ? String.valueOf(hour) : ("0"+hour)) + ":";
				date += (minute > 9 ? String.valueOf(minute) : ("0"+minute));
				break;
			case MAUS_DATE_TYPE: 
				date = (year + 2000) + "_";
				date += (month > 9 ? String.valueOf(month) : ("0"+month)) + "_";
				date += (day > 9 ? String.valueOf(day) : ("0"+day)) + " ";
				date += (hour > 9 ? String.valueOf(hour) : ("0"+hour)) + "_";
				date += (minute > 9 ? String.valueOf(minute) : ("0"+minute)) + "_";
				date += (second > 9 ? String.valueOf(second) : ("0"+second));
				break;
			case ONLY_TIME_TYPE:
				date = (hour > 9 ? String.valueOf(hour) : ("0"+hour)) + ":";
				date += (minute > 9 ? String.valueOf(minute) : ("0"+minute));
				break;
			case EXTENDED_ONLY_TIME:
				date = (hour > 9 ? String.valueOf(hour) : ("0"+hour)) + ":";
				date += (minute > 9 ? String.valueOf(minute) : ("0"+minute)) + ":";
				date += (second > 9 ? String.valueOf(second) : ("0"+second));
				break;
			}
			return date;
		}
		else
			return null;
	}

	public static String toHexString(String input)
	{
		String res = "";
		for (int i=0;i<input.length();i++)
		{
			String h = Integer.toHexString(input.charAt(i)).toUpperCase(Locale.GERMANY);
			if (h.length() < 2)
				h = "0"+h;
			res += h + " ";
		}
		return res;
	}

	public static byte[] toByteArray(String input)
	{
		byte[] res = new byte[input.length()];
		for (int i=0;i<input.length();i++)
		{
			res[i] = (byte) input.charAt(i);
		}
		return res;
	}

	public static String byteArrayToString(short[] input)
	{
		String res = "";
		for (int i=0;i<input.length;i++)
		{
			res += String.valueOf(Character.toChars(input[i]));
		}
		return res;
	}

	public static double calculateDistance(Location loc1, Location loc2)
	{
		//		double cosg = Math.sin(loc1.getLatitude()) * Math.sin(loc2.getLatitude()) +
		//				Math.cos(loc1.getLatitude()) * Math.cos(loc2.getLatitude()) * 
		//				Math.cos(loc2.getLongitude() - loc1.getLongitude());
		//		
		//		double distance = 6378.388 * Math.acos(cosg);
		//		
		//		//Runden
		//		double factor = Math.pow(10, 3);
		//		distance = Math.round(distance * factor) / factor;
		//		return distance;

		double dLat = (loc1.getLatitude() + loc2.getLatitude()) / 2 * 0.01745;
		double dx = 111.3 * Math.cos(dLat) * (loc1.getLongitude() - loc2.getLongitude());
		double dy = 111.3 * (loc1.getLatitude() - loc2.getLatitude());

		double distance = Math.sqrt(dx * dx + dy * dy);

		//runden
		double factor = Math.pow(10, 3);
		distance = Math.round(distance * factor) / factor;
		return distance;

		//		double dLon = loc2.getLongitude() - loc1.getLongitude();
		//		double dLat = loc2.getLatitude() - loc1.getLatitude();
		//		
		//		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(loc1.getLatitude()) * Math.cos(loc2.getLatitude()) *
		//				(Math.sin(dLon / 2) * Math.sin(dLon / 2));
		//		double c = 2 * Math.asin(Math.min(1.0, Math.sqrt(a)));
		//		double distance = radiusOfEarth * c;
		//		
		//		//runden
		//		double factor = Math.pow(10, 3);
		//		distance = Math.round(distance * factor) / factor;
		//		return distance;
	}

	public static String encodeString(String data)
	{
		String alphabetS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,; ";

		Locale l = new Locale("DE");
		data = data.toUpperCase(l);
		String key = "XC93.F9AS,";

		StringBuilder sb = new StringBuilder();

		for (int i=0;i<data.length();i++)
		{
			if (alphabetS.indexOf(data.charAt(i)) > -1)
			{
				int index = alphabetS.indexOf(data.charAt(i)) + alphabetS.indexOf(key.charAt(i % key.length()));
				if (index >= alphabetS.length()) 
				{
					index -= (alphabetS.length());
				}
				Character c = alphabetS.charAt(index);
				sb.append(c);
			}
			else
			{
				//Log.e(TAG, "Error encoding String");
				return "";
			}
		}

		return sb.toString();
	}

	public static String decodeString(String data)
	{
		String alphabetS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,; ";

		Locale l = new Locale("DE");
		data = data.toUpperCase(l);
		String key = "XC93.F9AS,";

		StringBuilder sb = new StringBuilder();

		for (int i=0;i<data.length();i++)
		{
			if (alphabetS.indexOf(data.charAt(i)) > -1)
			{
				int index = alphabetS.indexOf(data.charAt(i)) - alphabetS.indexOf(key.charAt(i % key.length()));
				if (index < 0) 
				{
					index += (alphabetS.length());
				}
				Character c = alphabetS.charAt(index);
				sb.append(c);
			}
			else
			{
				//Log.e(TAG, "Error decoding String");
				return "";
			}
		}

		return sb.toString();
	}

	public static Map<String, File> getAllStorageLocations()
	{
		Map<String, File> map = new HashMap<String, File>(10);

		List<String> mMounts = new ArrayList<String>(10);
		List<String> mVold = new ArrayList<String>(10);
		mMounts.add("/mnt/sdcard");
		mVold.add("/mnt/sdcard");

		try
		{
			File mountFile = new File("/proc/mounts");
			if (mountFile.exists())
			{
				Scanner scanner = new Scanner(mountFile);
				while (scanner.hasNext())
				{
					String line = scanner.nextLine();
					if (line.startsWith("/dev/block/vold/"))
					{
						String[] lineElements = line.split(" ");
						String element = lineElements[1];

						if (!element.equals("/mnt/sdcard"))
						{
							mMounts.add(element);
						}
					}
				}
				if (scanner != null)
					scanner.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		try
		{
			File voldFile = new File("/system/etc/vold.fstab");
			if (voldFile.exists())
			{
				Scanner scanner = new Scanner(voldFile);
				while (scanner.hasNext())
				{
					String line = scanner.nextLine();
					if (line.startsWith("dev_mount"))
					{
						String[] lineElements = line.split(" ");
						String element = lineElements[2];

						if (element.contains(":"))
							element = element.substring(0, element.indexOf(":"));
						if (!element.equals("/mnt/sdcard"))
						{
							mVold.add(element);
						}
					}
				}
				if (scanner != null)
					scanner.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		for (int i=0; i<mMounts.size();i++)
		{
			String mount = mMounts.get(i);
			if (!mVold.contains(mount))
				mMounts.remove(i--);
		}
		mVold.clear();

		List<String> mountHash = new ArrayList<String>(10);

		for (String mount : mMounts)
		{
			File root = new File(mount);
			if (root.exists() && root.isDirectory() && root.canWrite())
			{
				File[] list = root.listFiles();
				String hash = "[";
				if (list != null)
				{
					for (File f : list)
					{
						hash += f.getName().hashCode()+":"+f.length()+", ";
					}
				}
				hash += "]";
				if (!mountHash.contains(hash))
				{
					String key = SD_CARD + "_" + map.size();
					if (map.size() == 0)
					{
						key = SD_CARD;
					}
					else if (map.size() == 1)
					{
						key = EXTERNAL_SD_CARD;
					}
					mountHash.add(hash);
					map.put(key, root);
				}
			}
		}

		mMounts.clear();

		if (map.isEmpty())
		{
			map.put(SD_CARD, Environment.getExternalStorageDirectory());
		}
		return map;
	}

	public static boolean checkNetworkStatus(Context context)
	{
		boolean status = false;
		try
		{
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
			{
				status = true;
			}
			//			else
			//			{
			//				netInfo = cm.getNetworkInfo(1);
			//				if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
			//				{
			//					status = true;
			//				}
			//			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return status;
	}

	private static String sha1Hash(String toHash)
	{
		String hash = null;
		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			byte[] bytes = toHash.getBytes("UTF-8");
			digest.update(bytes, 0, bytes.length);
			bytes = digest.digest();
			StringBuilder sb = new StringBuilder();
			for (byte b : bytes)
			{
				sb.append(String.format("%02X", b));
			}
			hash = sb.toString();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return hash;
	}

	public static String encodeDeviceID(String deviceID)
	{
		String hash = sha1Hash(deviceID + "XC93.F9AS,");
		StringBuilder b = new StringBuilder();
		for (int i=0;i<hash.length();i++)
		{
			if (i % 2 == 0)
				b.append(hash.substring(i, i+1));
		}
		return b.toString();
	}

	public static String encodeDeviceIDCustom(String deviceID, String appendix)
	{
		String hash = sha1Hash(deviceID + appendix);
		StringBuilder b = new StringBuilder();
		for (int i=0;i<hash.length();i++)
		{
			if (i % 2 == 0)
				b.append(hash.substring(i, i+1));
		}
		return b.toString();
	}

	public static String generatePromotionKey(String deviceID, String appendix)
	{
		String hash = sha1Hash(deviceID + "PromotionKey" + appendix);
		StringBuilder b = new StringBuilder();
		for (int i=0;i<hash.length();i++)
		{
			if (i % 2 == 0)
				b.append(hash.substring(i, i+1));
		}
		return b.toString();
	}

	public static float convertPixelsToDp(float px, Context context)
	{
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		float dp = px / (dm.densityDpi / 160f);
		return dp;
	}

	public static float convertDpToPixels(float dp, Context context)
	{
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		float pix = dp * (dm.densityDpi / 160f);
		return pix;
	}

	public static double generateGaussian(Random r, double center, double deviance)
	{
		double res = r.nextGaussian()*deviance + center;
		return res;
	}

	public static double generateGaussian(Random r, double center, double deviance, double min, double max)
	{
		double res = r.nextGaussian()*deviance + center;
		while (res < min || res > max)
		{
			if (res < min)
				res = min + Math.abs(res);
			else
				res = max - (res - max);
		}
		return res;
	}

	public static boolean isValidIP4Address(String ip)
	{
		try
		{
			if (ip == null || ip.isEmpty())
				return false;

			if (ip.endsWith("."))
				return false;

			String[] parts = ip.split("\\.");
			if (parts.length != 4)
				return false;

			for (String s : parts)
			{
				int i = Integer.parseInt(s);
				if (i < 0 || i > 255)
					return false;
			}

			return true;
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}

	public static void sendSMS(Context context, String phoneNumber, String message)
	{
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY))
		{
			PendingIntent piSent = PendingIntent.getBroadcast(context, 0, new Intent("SMS_SENT"), 0);
			PendingIntent piDelivered = PendingIntent.getBroadcast(context, 0, new Intent("SMS_DELIVERED"), 0);

			ArrayList<PendingIntent> piSentA = new ArrayList<PendingIntent>(1);
			piSentA.add(piSent);

			ArrayList<PendingIntent> piDeliveredA = new ArrayList<PendingIntent>(1);
			piSentA.add(piDelivered);

			SmsManager sm = SmsManager.getDefault();
			ArrayList<String> parts = sm.divideMessage(message);
			sm.sendMultipartTextMessage(phoneNumber, null, parts, piSentA, piDeliveredA);
			//sm.sendTextMessage(phoneNumber, null, message, piSent, piDelivered);
		}
	}

	public static class NetworkListener extends BroadcastReceiver
	{
		private OnNetworkStateChangeListener listener;
		private boolean networkOK;

		public NetworkListener(Activity act)
		{
			if (act instanceof OnNetworkStateChangeListener)
				listener = (OnNetworkStateChangeListener) act;
			else
				throw new ClassCastException(act.toString() + " must implement NetworkListener.OnNetworkStateChangeListener !");
		}

		public interface OnNetworkStateChangeListener
		{
			public void onNetworkStateChanged(boolean networkStateOK);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getExtras() != null)
			{
				NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
				if (ni != null && ni.getState()==NetworkInfo.State.CONNECTED)
				{
					networkOK = true;
					listener.onNetworkStateChanged(networkOK);
				}
				else if (ni != null && ni.getState()==NetworkInfo.State.DISCONNECTED)
				{
					networkOK = false;
					listener.onNetworkStateChanged(networkOK);
				}
			}
		}

		public void update()
		{
			listener.onNetworkStateChanged(networkOK);
		}

	};

	public static class Constants {
		//Math params
		public static final double latMin = -85.0511;
		public static final double latMax = 85.0511;
		public static final double radiusOfEarth = 6378137; //meters
		public static final double circumferenceOfEarth = 2.0 * Math.PI * radiusOfEarth;
		public static final double halfCircumferenceOfEarth =  circumferenceOfEarth / 2;
		public static final double metersPerMile = 1609.344;
		public static final double metersPerFeet = 0.3048;
		public static final double kmToMilesPerHour = 0.62137119223733;
	}

	public static boolean isServiceRunning(Class<?> serviceClass, Context ctx)
	{
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : am.getRunningServices(Integer.MAX_VALUE))
		{
			if (serviceClass.getName().equals(service.service.getClassName()))
				return true;
		}
		return false;
	}

	public static GregorianCalendar CalculateUTCTime(GregorianCalendar calendar){
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		return calendar;

	}

	public static GregorianCalendar CalculateLocalTime(GregorianCalendar calendar){

		@SuppressWarnings("unused")
		String test1 = Utils.dateToStr(calendar, SHORT_DATE_TYPE);
		calendar.setTimeZone(TimeZone.getDefault());
		return calendar;

	}

	public static boolean isPackageInstalled(Context context, String nameOfPackage)
	{ 
		try {
			context.getPackageManager().getApplicationInfo(nameOfPackage,0);

			return true;
		}
		catch(PackageManager.NameNotFoundException e)
		{
			return false;
		}

	}
	public static Bitmap createPicPreviewFromPic (File f, Context ctx) throws IOException
	{	
		Bitmap b = null;
		int width = (int) Utils.convertPixelsToDp(ctx.getResources().getDisplayMetrics().widthPixels, ctx);
		int height = (int) Utils.convertPixelsToDp(ctx.getResources().getDisplayMetrics().heightPixels, ctx);

		final int IMAGE_MAX_SIZE = (int) Utils.convertDpToPixels((float) (Math.min(width, height) / 1.5), ctx);

		//Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;

		FileInputStream fis = new FileInputStream(f);
		BitmapFactory.decodeStream(fis, null, o);
		fis.close();


		int scale = 1;
		if(o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE)
		{
			scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
					(double) Math.max(o.outHeight, o.outWidth)) /Math.log(0.5)));
		}

		//Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		fis = new FileInputStream(f);
		b = BitmapFactory.decodeStream(fis, null, o2);	
		//		Log.e(TAG, String.valueOf(b.getByteCount()));
		fis.close();

		return b;

	}
	public static void copy(File src, File dst) throws IOException
	{
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		byte [] buf = new byte[1024];
		int len;
		while ((len=in.read(buf))>0)
			out.write(buf, 0 , len);
		in.close();
		out.close();
	}
	
	public static void copyFileFromInputStream(InputStream in, File dst) throws IOException
	{
		try
		{
			OutputStream out = new FileOutputStream(dst);

			byte [] buf = new byte[1024];
			int len;
			while ((len=in.read(buf))>0)
				out.write(buf, 0 , len);
			in.close();
			out.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	//	public static byte checkForAvailableStorageLocations()
	//	{
	//		Map<String, File> map = Utils.getAllStorageLocations();
	//
	//		boolean foundExtStorage = false;
	//		
	//		if (null != map.get(Utils.EXTERNAL_SD_CARD))
	//		{
	//			foundExtStorage = true;
	//		}
	//		
	//		if (null != map.get(Utils.EXTERNAL_SD_CARD_2))
	//		{
	//			foundExtStorage = true;
	//		}
	//		
	//		if (mapSources.size() == 0)
	//		{
	//			//no mapSources found until here
	//
	//			//Mounts
	//			File fMnt = new File("/mnt/");
	//			if (fMnt.exists())
	//			{
	//				String dirList[] = fMnt.list();
	//				
	//				for (int i=0;i<dirList.length;i++)
	//				{
	////					Toast.makeText(context, "mount"+String.valueOf(i)+": "+dirList[i], Toast.LENGTH_LONG).show();
	//					File sd = new File("/mnt/"+dirList[i]);
	//					boolean usable = sd.exists() && sd.canRead() && sd.isDirectory() && sd.canWrite();
	//					
	//					if (usable)
	//					{
	//						//Toast.makeText(context, "Storage="+sd.getName(), Toast.LENGTH_SHORT).show();
	//						File[] files = sd.listFiles();
	//
	//						for (File f : files)
	//						{
	//							if (f.isDirectory() && f.getName().equals("IBH"))
	//							{
	//								File[] files2 = f.listFiles();
	//								for (File f2 : files2)
	//								{
	//									if (f2.isDirectory() && f2.getName().equals("TrackingClient"))
	//									{
	//										File[] filesX = f2.listFiles();
	//										for (File fX : filesX)
	//										{
	//											if (fX.isDirectory() && fX.getName().equals("Maps"))
	//											{
	//												//found Path
	//												for (File fMap : fX.listFiles())
	//												{
	//													if (fMap.isFile() && fMap.getName().endsWith(".gemf"))
	//														mapSources.add(new MapSource(fMap));
	//												}
	//												if (mapSources.size() > 0)
	//													return 0;
	//											}
	//										}
	//									}
	//								}
	//							}
	//						}
	//					}
	//				}
	//			}
	//			
	//			//Storage
	//			fMnt = new File("/storage/");
	//			if (fMnt.exists())
	//			{
	//				String dirList[] = fMnt.list();
	//				
	//				for (int i=0;i<dirList.length;i++)
	//				{
	////					Toast.makeText(context, "storage"+String.valueOf(i)+": "+dirList[i], Toast.LENGTH_LONG).show();
	//					File sd = new File("/storage/" + dirList[i]);
	//					boolean usable = sd.exists() && sd.canRead() && sd.isDirectory() && (sd.listFiles().length > 0);
	//					
	//					if (usable)
	//					{
	//						//Toast.makeText(context, "Storage="+sd.getName(), Toast.LENGTH_SHORT).show();
	//						File[] files = sd.listFiles();
	//
	//						for (File f : files)
	//						{
	//							if (f.isDirectory() && f.getName().equals("IBH"))
	//							{
	//								File[] files2 = f.listFiles();
	//								for (File f2 : files2)
	//								{
	//									if (f2.isDirectory() && f2.getName().equals("TrackingClient"))
	//									{
	//										File[] filesX = f2.listFiles();
	//										for (File fX : filesX)
	//										{
	//											if (fX.isDirectory() && fX.getName().equals("Maps"))
	//											{
	//												//found Path
	//												for (File fMap : fX.listFiles())
	//												{
	//													if (fMap.isFile() && fMap.getName().endsWith(".gemf"))
	//														mapSources.add(new MapSource(fMap));
	//												}
	//												if (mapSources.size() > 0)
	//													return 0;
	//											}
	//										}
	//									}
	//								}
	//							}
	//						}
	//					}
	//				}
	//			}
	//		}
	//		return 0;
	//	}

	public static boolean setErrorLogToFile(String filename, String folder)
	{
		Map<String, File> map = Utils.getAllStorageLocations();
		if (null != map.get(Utils.EXTERNAL_SD_CARD))
		{
			File f = new File(map.get(Utils.EXTERNAL_SD_CARD).getAbsolutePath() + "/IBH/"+ folder + "/");
			f.mkdirs();

			f = new File(map.get(Utils.EXTERNAL_SD_CARD).getAbsolutePath() + "/IBH/"+ folder + "/"+filename);
			if (f.exists() && f.isDirectory())
				f.delete();
			if (f != null)
			{
				try {
					System.setErr(new PrintStream(new FileOutputStream(f)));
					Log.d(TAG, "ErrorLog changed to file: "+f.getPath());
					return true;
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		}

		if (null != map.get(Utils.EXTERNAL_SD_CARD_2))
		{
			File f = new File(map.get(Utils.EXTERNAL_SD_CARD_2).getAbsolutePath() + "/IBH/"+ folder + "/");
			f.mkdirs();

			f = new File(map.get(Utils.EXTERNAL_SD_CARD_2).getAbsolutePath() + "/IBH/"+ folder + "/"+filename);
			if (f.exists() && f.isDirectory())
				f.delete();
			if (f != null)
			{
				try {
					System.setErr(new PrintStream(new FileOutputStream(f)));
					Log.d(TAG, "ErrorLog changed to file: "+f.getPath());
					return true;
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		}

		//No External sd found until here, more checks necessary
		File fMnt = new File("/mnt/");
		if (fMnt.exists())
		{
			String dirList[] = fMnt.list();

			for (int i=0;i<dirList.length;i++)
			{
				//				Toast.makeText(context, "mount"+String.valueOf(i)+": "+dirList[i], Toast.LENGTH_LONG).show();
				File sd = new File("/mnt/"+dirList[i]);
				boolean usable = sd.exists() && sd.canRead() && sd.isDirectory() && sd.canWrite();

				if (usable)
				{
					File f = new File(sd.getAbsolutePath() + "/IBH/"+ folder);
					f.mkdirs();

					f = new File(sd.getAbsolutePath() + "/IBH/"+ folder + "/"+filename);
					if (f.exists() && f.isDirectory())
						f.delete();
					if (f != null)
					{
						try {
							System.setErr(new PrintStream(new FileOutputStream(f)));
							Log.d(TAG, "ErrorLog changed to file: "+f.getPath());
							return true;
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}

		//Storage
		fMnt = new File("/storage/");
		if (fMnt.exists())
		{
			String dirList[] = fMnt.list();

			for (int i=0;i<dirList.length;i++)
			{
				//				Toast.makeText(context, "storage"+String.valueOf(i)+": "+dirList[i], Toast.LENGTH_LONG).show();
				File sd = new File("/storage/" + dirList[i]);
				boolean usable = sd.exists() && sd.canRead() && sd.isDirectory() && sd.canWrite();

				if (usable)
				{
					File f = new File(sd.getAbsolutePath() + "/IBH/"+ folder);
					f.mkdirs();

					f = new File(sd.getAbsolutePath() + "/IBH/"+ folder + "/"+filename);
					if (f.exists() && f.isDirectory())
						f.delete();
					if (f != null)
					{
						try {
							System.setErr(new PrintStream(new FileOutputStream(f)));
							Log.d(TAG, "ErrorLog changed to file: "+f.getPath());
							return true;
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}

		if (null != map.get(Utils.SD_CARD))
		{
			File f = new File(map.get(Utils.SD_CARD).getAbsolutePath() + "/IBH/"+ folder + "/");
			f.mkdirs();

			f = new File(map.get(Utils.SD_CARD).getAbsolutePath() + "/IBH/"+ folder + "/"+filename);
			if (f != null)
			{
				try {
					System.setErr(new PrintStream(new FileOutputStream(f)));
					Log.d(TAG, "ErrorLog changed to file: "+f.getPath());
					return true;
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		}
		return false;
	}

	public static boolean deleteFileRecursive(File f)
	{
		if (f.isDirectory())
			for (File child : f.listFiles())
				deleteFileRecursive(child);

		return f.delete();
	}

	public static Bitmap rotateBitmap(Bitmap source, float degrees)
	{
		Matrix matrix = new Matrix();
		matrix.postRotate(degrees);

		Bitmap bitmap = null;
		int height;
		int width;
		
		height = source.getHeight();
		width = source.getWidth();
		
		while (bitmap == null)
		{
			try
			{
				bitmap =  Bitmap.createBitmap(source,0,0,width,height, matrix, true);
			}
			catch (OutOfMemoryError e)
			{
				height /= 2;
				width /= 2;
				e.printStackTrace();
			}
		}

		return bitmap;

	}
}
