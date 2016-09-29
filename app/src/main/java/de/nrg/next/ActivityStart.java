package de.nrg.next;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityStart extends Activity {

	private static TextView messageText;
	private static EditText eNickname, ePasswort;
	private static Context ctx;
	private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1001;

	private static String uploadFilePath = "/storage/sdcard/DCIM/Camera/";
	private static String uploadFileName = "IMG_20160519_030506.jpg";
	private static final String upLoadServerUri = "http://192.168.2.213:12345/upload.php";
	int serverResponseCode = 0;
	
	private static JSONObject userdata;

	private static ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_start);
		
		ctx = this;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to read the contacts
                }

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant
            }
		}

		messageText = (TextView) findViewById(R.id.textView1);
		Button bLogin = (Button) findViewById(R.id.bLogin);
		Button bUpload = (Button) findViewById(R.id.button2);
		Button bGeoCode = (Button) findViewById(R.id.button3);
		Button bReg = (Button) findViewById(R.id.bRegister);
		
		eNickname = (EditText) findViewById(R.id.eNickname);
		ePasswort = (EditText) findViewById(R.id.ePassword);
		
		bReg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (eNickname.getText().length() >= 3 && ePasswort.getText().length() >= 3)
				{
//					if (Utils.checkNetworkStatus(ctx))
						new RegTask(eNickname.getText().toString(), ePasswort.getText().toString()).execute((Void) null);
//					else
//						Toast.makeText(ctx, "Keine Internetverbindung!", Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(ctx, "Nickname und Passwort müssen mind. 3 Zeichen lang sein!", Toast.LENGTH_LONG).show();
			}
		});
		
		bLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new HighscoreTask("Rüdiger", "Huawei", "18161691", 32, 1).execute((Void) null);
//				if (eNickname.getText().length() >= 3 && ePasswort.getText().length() >= 3)
//				{
////					if (Utils.checkNetworkStatus(ctx))
//						new LoginTask(eNickname.getText().toString(), ePasswort.getText().toString()).execute((Void) null);
////					else
////						Toast.makeText(ctx, "Keine Internetverbindung!", Toast.LENGTH_SHORT).show();
//				}
//				else
//					Toast.makeText(ctx, "Nickname und Passwort müssen mind. 3 Zeichen lang sein!", Toast.LENGTH_LONG).show();
			}
		});
		
		bUpload.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MyDialogFragment dialog = MyDialogFragment.getInstance(ctx, 0);
				dialog.show(getFragmentManager(), "myDialogFragment");
			}
		});
		
		bGeoCode.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Geocoder gCoder = new Geocoder(ctx);
				try {
					ArrayList<Address> addressList = (ArrayList<Address>) gCoder.getFromLocation(51.74933943, 12.12310699, 3);
					if (addressList != null && addressList.size() > 0)
					{
						//There is an address
						Toast.makeText(ctx, addressList.get(0).getLocality(), Toast.LENGTH_SHORT).show();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_start, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public String getRealPathFromUri(Uri uri)
	{
		String[] projection = {MediaStore.Images.Media.DATA};
		@SuppressWarnings("deprecation")
		Cursor cursor = managedQuery(uri, projection, null, null, null); 
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	@Override
	public void onActivityResult(final int requestCode, int resultCode, final Intent data) {

		switch (requestCode)
		{
		case MyApplication.REQUEST_CAMERA:
			if (resultCode == Activity.RESULT_OK)
			{
				//				Intent i = new Intent(this,ActivityPicture.class);
				//				startActivityForResult(i, REQUEST_PICTURE);
			}
			break;
		case MyApplication.REQUEST_GALLERY:
			if (resultCode == Activity.RESULT_OK)
			{
				Uri selectedImage = data.getData();
				final String s = getRealPathFromUri(selectedImage);


				Bitmap bitmap = BitmapFactory.decodeFile(s);
				if (bitmap != null)
				{
					if (bitmap.getHeight() > 1600 || bitmap.getWidth() > 1600)
					{							
						if (bitmap.getWidth() >= bitmap.getHeight())
						{
							double factor = bitmap.getWidth() / 1600.0;
							bitmap = Bitmap.createScaledBitmap(bitmap, 1600, (int) (bitmap.getHeight() / factor), true);
						}
						else
						{
							double factor = bitmap.getHeight() / 1600.0;
							bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / factor), 1600, true);
						}
					}

					FileOutputStream fos;
					try {
						fos = new FileOutputStream(new File(s));
						bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
						fos.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					bitmap.recycle();

					dialog = ProgressDialog.show(ActivityStart.this, "", "Uploading file...", true);

					File file = new File(s);
					uploadFilePath = file.getPath().substring(0, file.getPath().lastIndexOf("/") + 1);
					uploadFileName = file.getName();

					new Thread(new Runnable() 
					{
						public void run() {

							runOnUiThread(new Runnable() {
								public void run() {
									messageText.setText("uploading started.....");
								}
							});                      

							uploadFile(uploadFilePath + "" + uploadFileName);

						}
					}).start();      
				}

				//				startSendingPicture(s);
			}

			break;


			//				File srcPicture = new File(s);
			//				if (srcPicture.exists())
			//				{
			//					try {
			//						Utils.copy(srcPicture, new File(MyApplication.pictureFolder + "/pic_temp.png"));
			//						Bitmap bitmap = BitmapFactory.decodeFile(MyApplication.pictureFolder + "/pic_temp.png");
			//						if (bitmap != null)
			//						{
			//							if (bitmap.getHeight() > 1600 || bitmap.getWidth() > 1600)
			//							{							
			//								if (bitmap.getWidth() >= bitmap.getHeight())
			//								{
			//									double factor = bitmap.getWidth() / 1600.0;
			//									bitmap = Bitmap.createScaledBitmap(bitmap, 1600, (int) (bitmap.getHeight() / factor), true);
			//								}
			//								else
			//								{
			//									double factor = bitmap.getHeight() / 1600.0;
			//									bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / factor), 1600, true);
			//								}
			//							}
			//
			//							FileOutputStream fos = new FileOutputStream(new File(MyApplication.pictureFolder + "/pic_temp.png"));
			//							bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
			//							fos.close();
			//							bitmap.recycle();
			//
			//							Intent i = new Intent(this,ActivityPicture.class);
			//							startActivityForResult(i, REQUEST_PICTURE);
			//						}
			//						else
			//							Toast.makeText(getBaseContext(), "An error occured!" , Toast.LENGTH_LONG).show();
			//					} catch (IOException e) {
			//						Toast.makeText(getBaseContext(), "An error occured!" , Toast.LENGTH_LONG).show();
			//						e.printStackTrace();
			//					}
			//				}
			//				else
			//					Toast.makeText(getBaseContext(), "An error occured!" , Toast.LENGTH_LONG).show();
			//			}
			//			break;
			//		case MyApplication.REQUEST_PICTURE:
			//			if (resultCode == Activity.RESULT_OK)
			//			{
			//				//Picture sending
			//				if (data != null)
			//				{
			//					//Sending picture
			//					Uri picUri = Uri.fromFile(new File(MyApplication.pictureFolder + "/pic_temp.png"));
			//					if (picUri != null)
			//					{
			//						String desc = data.getStringExtra("Description");
			//
			//						try {
			//							//compression
			//							Bitmap bitmap = BitmapFactory.decodeFile(MyApplication.pictureFolder + "/pic_temp.png");
			//							if (bitmap != null)
			//							{
			//								if (bitmap.getHeight() > 1600 || bitmap.getWidth() > 1600)
			//								{							
			//									if (bitmap.getWidth() >= bitmap.getHeight())
			//									{
			//										double factor = bitmap.getWidth() / 1600.0;
			//										bitmap = Bitmap.createScaledBitmap(bitmap, 1600, (int) (bitmap.getHeight() / factor), true);
			//									}
			//									else
			//									{
			//										double factor = bitmap.getHeight() / 1600.0;
			//										bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / factor), 1600, true);
			//									}
			//								}
			//
			//								FileOutputStream fos = new FileOutputStream(new File(MyApplication.pictureFolder + "/pic_temp.png"));
			//								bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
			//								fos.close();
			//								bitmap.recycle();
			//
			//								if (MyApplication.createAndSendChatMessage(picUri, desc) == false)
			//									Toast.makeText(getBaseContext(), "An error occured!" , Toast.LENGTH_LONG).show();
			//								else
			//									FragmentUserChat.handler.sendEmptyMessage(0);
			//							}
			//							else
			//								Toast.makeText(getBaseContext(), "An error occured!" , Toast.LENGTH_LONG).show();
			//						} catch (IOException e) {
			//							Toast.makeText(getBaseContext(), "An error occured!" , Toast.LENGTH_LONG).show();
			//							e.printStackTrace();
			//						}
			//					}
			//					else
			//					{
			//						//Delete temp pic
			//						File tempPic = new File(MyApplication.pictureFolder+"/pic_temp.png");
			//						if (tempPic.exists())
			//							tempPic.delete();
			//						Toast.makeText(getBaseContext(), "An error occured!" , Toast.LENGTH_LONG).show();
			//					}
			//				}
			//			}
			//			else
			//			{
			//				//Delete temp pic
			//				File tempPic = new File(MyApplication.pictureFolder+"/pic_temp.png");
			//				if (tempPic.exists())
			//					tempPic.delete();
			//				Toast.makeText(getBaseContext(), "Canceled" , Toast.LENGTH_SHORT).show();
			//			}
			//			break;
		}
	}

	public int uploadFile(String sourceFileUri) 
	{
		String fileName = sourceFileUri;

		HttpURLConnection conn = null;
		DataOutputStream dos = null;  
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024; 
		File sourceFile = new File(sourceFileUri); 

		if (!sourceFile.isFile()) {

			dialog.dismiss(); 

			Log.e("uploadFile", "Source File not exist :"
					+uploadFilePath + "" + uploadFileName);

			runOnUiThread(new Runnable() {
				public void run() {
					messageText.setText("Source File not exist :"
							+uploadFilePath + "" + uploadFileName);
				}
			}); 

			return 0;

		}
		else
		{
			try { 

				// open a URL connection to the Servlet
				FileInputStream fileInputStream = new FileInputStream(sourceFile);
				URL url = new URL(upLoadServerUri);

				// Open a HTTP  connection to  the URL
				conn = (HttpURLConnection) url.openConnection(); 
				conn.setConnectTimeout(10000);
				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
				conn.setRequestProperty("uploaded_file", fileName); 

				dos = new DataOutputStream(conn.getOutputStream());

				dos.writeBytes(twoHyphens + boundary + lineEnd); 
				dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+fileName + "\"" + lineEnd);

				dos.writeBytes(lineEnd);

				// create a buffer of  maximum size
				bytesAvailable = fileInputStream.available(); 

				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// read file and write it into form...
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);  

				while (bytesRead > 0) {

					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);   

				}

				// send multipart form data necesssary after file data...
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				// Responses from the server (code and message)
				serverResponseCode = conn.getResponseCode();
				String serverResponseMessage = conn.getResponseMessage();

				Log.i("uploadFile", "HTTP Response is : " 
						+ serverResponseMessage + ": " + serverResponseCode);

				if(serverResponseCode == 200)
				{
					final HttpURLConnection conn2 = conn;
					runOnUiThread(new Runnable() {
						public void run() {

							String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
									+" http://10.0.2.2/workspace/testserver/uploads/"
									+uploadFileName;

							messageText.setText(msg);
							Toast.makeText(ActivityStart.this, "File Upload Complete.", 
									Toast.LENGTH_SHORT).show();

							String response = "";
							String line;

							BufferedReader reader = null;
							try {
								reader = new BufferedReader(new InputStreamReader(conn2.getInputStream()));
								while ((line = reader.readLine()) != null)
								{
									response += line;
								}
								Toast.makeText(ActivityStart.this, response, 
										Toast.LENGTH_LONG).show();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							finally
							{
								try
								{
									if (reader != null)
										reader.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

						}
					});                
				}    

				//close the streams //
				fileInputStream.close();
				dos.flush();
				dos.close();
				conn.disconnect();

			} catch (MalformedURLException ex) {

				dialog.dismiss();  
				ex.printStackTrace();

				runOnUiThread(new Runnable() {
					public void run() {
						messageText.setText("MalformedURLException Exception : check script url.");
						Toast.makeText(ActivityStart.this, "MalformedURLException", 
								Toast.LENGTH_SHORT).show();
					}
				});

				Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
			} catch (Exception e) {

				dialog.dismiss();  
				e.printStackTrace();

				runOnUiThread(new Runnable() {
					public void run() {
						messageText.setText("Got Exception : see logcat ");
						Toast.makeText(ActivityStart.this, "Got Exception : see logcat ", 
								Toast.LENGTH_SHORT).show();
					}
				});
				Log.e("Upload file Exc", "Exception : "
						+ e.getMessage(), e);  
			}
			dialog.dismiss();       
			return serverResponseCode; 

		} // End else block 
	} 
	
	public static Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 0:
				Toast.makeText(ctx, msg.obj.toString(), Toast.LENGTH_LONG).show();
				break;
			}
		}
	};

	private static String getQuery(ArrayList<Pair<String,String>> params)
	{
		StringBuilder sb = new StringBuilder();
		boolean first = true;

		for (Pair<String,String> pair : params)
		{
			if (first)
				first = false;
			else
				sb.append("&");

			try {
				sb.append(URLEncoder.encode(pair.first, "UTF-8"));
				sb.append("=");
				sb.append(URLEncoder.encode(pair.second, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}
	
	private class LoginTask extends AsyncTask<Void, Void, Void>
	{
		private ProgressDialog asyncDialog = new ProgressDialog(ctx);
		private String nick, pass;
		
		public LoginTask(String nick, String pass)
		{
			this.nick = nick;
			this.pass = pass;
		}
		
		@Override
		protected void onPreExecute()
		{
			asyncDialog.setCancelable(false);
			asyncDialog.setMessage("Verbindung zum Server aufbauen ...");
			asyncDialog.setTitle("Login");
			asyncDialog.setIcon(android.R.drawable.ic_dialog_info);
			asyncDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... paramsX) 
		{
			userdata = null;
			URL url;
			try {
				url = new URL("http://10.0.2.2/workspace/testserver/login.php");
				HttpURLConnection con = (HttpURLConnection) url.openConnection();

				con.setReadTimeout(10000);
				con.setConnectTimeout(15000);
				con.setRequestMethod("POST");
				con.setDoInput(true);
				con.setDoOutput(true);

				ArrayList<Pair<String, String>> params = new ArrayList<Pair<String,String>>(1);
				params.add(new Pair<String, String>("username",nick));
				params.add(new Pair<String, String>("password",pass));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));

				writer.write(getQuery(params));
				writer.flush();
				writer.close();

				int responseCode = con.getResponseCode();
				if (responseCode == HttpsURLConnection.HTTP_OK)
				{
					String response = "";
					String line;
					BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
					while ((line = reader.readLine()) != null)
						response += line;
					
					try {
						userdata = new JSONObject(response);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			if (asyncDialog != null && asyncDialog.isShowing())
				asyncDialog.dismiss();
			super.onPostExecute(result);
			
			try {
				if (userdata != null && userdata.getInt("success") == 1)
				{
					Toast.makeText(ctx, "Login erfolgreich!", Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(ctx, "Login war nicht erfolgreich!", Toast.LENGTH_SHORT).show();
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(ctx, "Login war nicht erfolgreich!", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class RegTask extends AsyncTask<Void, Void, Void>
	{
		private ProgressDialog asyncDialog = new ProgressDialog(ctx);
		private String nick, pass;
		
		public RegTask(String nick, String pass)
		{
			this.nick = nick;
			this.pass = pass;
		}
		
		@Override
		protected void onPreExecute()
		{
			asyncDialog.setCancelable(false);
			asyncDialog.setMessage("Verbindung zum Server aufbauen ...");
			asyncDialog.setTitle("Registrierung");
			asyncDialog.setIcon(android.R.drawable.ic_dialog_info);
			asyncDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... paramsX) 
		{
			userdata = null;
			URL url;
			try {
				url = new URL("http://10.0.2.2/workspace/testserver/registry.php");
				HttpURLConnection con = (HttpURLConnection) url.openConnection();

				con.setReadTimeout(10000);
				con.setConnectTimeout(15000);
				con.setRequestMethod("POST");
				con.setDoInput(true);
				con.setDoOutput(true);

				ArrayList<Pair<String, String>> params = new ArrayList<Pair<String,String>>(1);
				params.add(new Pair<String, String>("username",nick));
				params.add(new Pair<String, String>("password",pass));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));

				writer.write(getQuery(params));
				writer.flush();
				writer.close();

				int responseCode = con.getResponseCode();
				if (responseCode == HttpsURLConnection.HTTP_OK)
				{
					String response = "";
					String line;
					BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
					while ((line = reader.readLine()) != null)
						response += line;
					
					try {
						userdata = new JSONObject(response);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			if (asyncDialog != null && asyncDialog.isShowing())
				asyncDialog.dismiss();
			super.onPostExecute(result);
			
			try {
				if (userdata != null && userdata.getInt("success") == 1)
				{
					Toast.makeText(ctx, "Registrierung erfolgreich!", Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(ctx, "Registrierung war nicht erfolgreich!", Toast.LENGTH_SHORT).show();
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(ctx, "Registrierung war nicht erfolgreich!", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class HighscoreTask extends AsyncTask<Void, Void, Void>
	{
		private ProgressDialog asyncDialog = new ProgressDialog(ctx);
		private String name, device, imei;
		private int country, gender;

		public HighscoreTask(String name, String device, String imei, int country, int gender)
		{
			this.name = name;
			this.device = device;
			this.imei = imei;
			this.country = country;
			this.gender = gender;
		}

		@Override
		protected void onPreExecute()
		{
			asyncDialog.setCancelable(false);
			asyncDialog.setMessage("Verbindung zum Server aufbauen ...");
			asyncDialog.setTitle("Upload");
			asyncDialog.setIcon(android.R.drawable.ic_dialog_info);
			asyncDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... paramsX)
		{
			userdata = null;
			URL url;
			try {
				url = new URL("http://192.168.2.213:12345/db_insert.php");
				HttpURLConnection con = (HttpURLConnection) url.openConnection();

				con.setReadTimeout(10000);
				con.setConnectTimeout(15000);
				con.setRequestMethod("POST");
				con.setDoInput(true);
				con.setDoOutput(true);

				ArrayList<Pair<String, String>> params = new ArrayList<Pair<String,String>>(1);
				params.add(new Pair<String, String>("name",name));
				params.add(new Pair<String, String>("device",device));
				params.add(new Pair<String, String>("imei",imei));
				params.add(new Pair<String, String>("country",String.valueOf(country)));
				params.add(new Pair<String, String>("gender",String.valueOf(gender)));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));

				writer.write(getQuery(params));
				writer.flush();
				writer.close();

				int responseCode = con.getResponseCode();
				if (responseCode == HttpsURLConnection.HTTP_OK)
				{
					String response = "";
					String line;
					BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
					while ((line = reader.readLine()) != null)
						response += line;

					try {
						userdata = new JSONObject(response);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			if (asyncDialog != null && asyncDialog.isShowing())
				asyncDialog.dismiss();
			super.onPostExecute(result);

			try {
				if (userdata != null && userdata.getInt("success") == 1)
				{
					Toast.makeText(ctx, "Registrierung erfolgreich!", Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(ctx, "Registrierung war nicht erfolgreich!", Toast.LENGTH_SHORT).show();
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(ctx, "Registrierung war nicht erfolgreich!", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
