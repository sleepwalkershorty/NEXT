package de.nrg.next;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

public class MyDialogFragment extends DialogFragment{

	private static Context context;
	private static int which = -1;
	private static String[] inputArray = null;

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putStringArray("inputArray", inputArray);
	}

	public static MyDialogFragment getInstance(Context ctx, int whichInt)
	{
		context = ctx;
		which = whichInt;
		MyDialogFragment frag = new MyDialogFragment();
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new Builder(context);
		
		switch (which)
		{
		case 0:
			//Take picture or galery?
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setTitle("Cam or galery");
			builder.setMessage("Would you like to take a picture or to choose one out of the galery?");
			builder.setPositiveButton("Take picture", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {
//						Uri picUri = Uri.fromFile(new File(MyApplication.pictureFolder+"/pic_temp.png"));
						
						Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//						captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
						getActivity().startActivityForResult(captureIntent, MyApplication.REQUEST_CAMERA);
					} catch (ActivityNotFoundException e) {
						String errorMessage = "Your device doesn't support camera";
						Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
					}

				}
			});
			builder.setNegativeButton("Galery", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent photoGallery = new Intent(Intent.ACTION_PICK);
					photoGallery.setType("image/*");
					getActivity().startActivityForResult(photoGallery, MyApplication.REQUEST_GALLERY);
				}
			});
			break;
		}

		final AlertDialog dialogX = builder.create();
		return dialogX;
	}
}
