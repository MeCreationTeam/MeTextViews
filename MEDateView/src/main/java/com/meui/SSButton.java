package com.meui;
import android.widget.*;
import android.content.*;
import android.util.*;
import android.view.*;

public class SSButton extends Button
{
	public SSButton(final Context c){
		super(c);
		init();
	}
	public SSButton(final Context c,final AttributeSet attr){
		super(c,attr);
		init();
	}
	public SSButton(final Context c,final AttributeSet attr,final int defStyle){
		super(c,attr,defStyle);
		init();
	}
	
	private void init(){
		this.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(final View p1){
				getContext().startActivity(new Intent().setClassName("com.cyanogenmod.screenshot","com.cyanogenmod.screenshot.ScreenshotActivity").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			}
		});
		this.setOnLongClickListener(new View.OnLongClickListener(){
			@Override
			public boolean onLongClick(final View p1){
				getContext().startActivity(new Intent().setClassName("com.meui.RomCtrl","com.meui.RomCtrl.SSPref").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			
				return true;
			}
		});
		
	}
}
