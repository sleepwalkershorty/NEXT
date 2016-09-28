package de.ibh_impex.androidhelputils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class MyDialogFragment extends DialogFragment{
	
	private Context context;
	private String message, title, posButton, negButton, neuButton;
	private int layoutId;
	
	public MyDialogFragment(String title, String message, String posButton, String negButton, String neuButton)
	{
		this.title = title;
		this.message = message;
		this.posButton = posButton;
		this.neuButton = neuButton;
		this.negButton = negButton;
		this.layoutId = 0;
	}
	
	public MyDialogFragment(String title, int layoutId, String posButton, String negButton, String neuButton)
	{
		this.title = title;
		this.layoutId = layoutId;
		this.posButton = posButton;
		this.neuButton = neuButton;
		this.negButton = negButton;
		this.message = null;
	}
	
	public MyDialogFragment()
	{
		
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		context = getActivity().getApplicationContext();
		
		Builder builder = new Builder(context);
		
		if (layoutId != 0)
		{
			LayoutInflater infl = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View content = infl.inflate(layoutId, null);
			builder.setView(content);
		}
		
		if (message != null)
			builder.setMessage(message);
		
		if (title != null)
			builder.setTitle(title);
		
		if (posButton != null)
			builder.setPositiveButton(posButton, null);
		
		if (negButton != null)
			builder.setNegativeButton(negButton, null);
		
		if (neuButton != null)
			builder.setNeutralButton(neuButton, null);
		
		final AlertDialog dialogX = builder.create();
		return dialogX;
	}

}
