package de.nrg.next;

import java.io.File;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import de.ibh_impex.androidhelputils.Utils;

public class MyApplication extends Application {

	private static final String TAG = MyApplication.class.getSimpleName();

	private static MyApplication singleton;
	public static final boolean DEBUGMODE = true;
	
	public static String appFolder;
	public static String pictureFolder;
	public static String previewFolder;
	public static String databaseFolder;
	public static final String ERROR_FOLDER = "Messenger/ErrorLog";
	
	public static final int REQUEST_GALLERY = 1001;
	public static final int REQUEST_CAMERA = 1002;
	public static final int REQUEST_GPS = 1003;
	public static final int REQUEST_PICTURE = 1004;
	
	private static Context ctx;
	
	public MyApplication getInstance()
	{
		return singleton;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
	}
	
	public static void setupExternalStorage()
	{
		Map<String, File> map = Utils.getAllStorageLocations();
		if (null != map.get(Utils.EXTERNAL_SD_CARD))
		{
			File f = new File(map.get(Utils.EXTERNAL_SD_CARD).getAbsolutePath() +  "/IBH/Messenger/pictures/");
			f.mkdirs();
			pictureFolder = f.getPath();

			f = new File(map.get(Utils.EXTERNAL_SD_CARD).getAbsolutePath() +  "/IBH/Messenger/previews/");
			f.mkdirs();
			previewFolder = f.getPath();
			
			appFolder = map.get(Utils.EXTERNAL_SD_CARD).getAbsolutePath() +  "/IBH/Messenger/";
			return;
		}

		if (null != map.get(Utils.EXTERNAL_SD_CARD_2))
		{
			File f = new File(map.get(Utils.EXTERNAL_SD_CARD_2).getAbsolutePath() +  "/IBH/Messenger/pictures/");
			f.mkdirs();
			pictureFolder = f.getPath();

			f = new File(map.get(Utils.EXTERNAL_SD_CARD_2).getAbsolutePath() +  "/IBH/Messenger/previews/");
			f.mkdirs();
			previewFolder = f.getPath();
			
			appFolder = map.get(Utils.EXTERNAL_SD_CARD_2).getAbsolutePath() +  "/IBH/Messenger/";
			return;
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
					File f = new File(sd.getAbsolutePath() +  "/IBH/Messenger/pictures/");
					f.mkdirs();
					pictureFolder = f.getPath();

					f = new File(sd.getAbsolutePath() +  "/IBH/Messenger/previews/");
					f.mkdirs();
					previewFolder = f.getPath();
					
					appFolder = sd.getAbsolutePath() +  "/IBH/Messenger/";

					return;
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
					File f = new File(sd.getAbsolutePath() +  "/IBH/Messenger/pictures/");
					f.mkdirs();
					pictureFolder = f.getPath();

					f = new File(sd.getAbsolutePath() +  "/IBH/Messenger/previews/");
					f.mkdirs();
					previewFolder = f.getPath();
					
					appFolder = sd.getAbsolutePath() +  "/IBH/Messenger/";

					return;
				}
			}
		}

		if (null != map.get(Utils.SD_CARD))
		{
			File f = new File(map.get(Utils.SD_CARD).getAbsolutePath() +  "/IBH/Messenger/pictures/");
			f.mkdirs();
			pictureFolder = f.getPath();

			f = new File(map.get(Utils.SD_CARD).getAbsolutePath() +  "/IBH/Messenger/previews/");
			f.mkdirs();
			previewFolder = f.getPath();
			
			appFolder = map.get(Utils.SD_CARD).getAbsolutePath() +  "/IBH/Messenger/";
		}
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		ctx = this;
		setupExternalStorage();
		MyApplication.setErrorLog(this.getClass());
	}
	
	@Override
	public void onLowMemory()
	{
		super.onLowMemory();
		Log.d(TAG, "Low Memory");
	}

	@Override
	public void onTerminate()
	{
		super.onTerminate();
	}
	
	public static void setErrorLog(Class c)
	{
		String errorFilename = c.getSimpleName()+".log";
		if (Utils.setErrorLogToFile(errorFilename, MyApplication.ERROR_FOLDER))
		{
			if (MyApplication.DEBUGMODE)
				Log.d(TAG, "ErrorLog changed to file: "+MyApplication.ERROR_FOLDER+"/"+errorFilename);
		}
		else
		{
			if (MyApplication.DEBUGMODE)
				Log.d(TAG, "Error setting errorLog to extern file");
		}		
	}
}
