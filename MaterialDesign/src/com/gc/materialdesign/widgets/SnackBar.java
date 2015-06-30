package com.gc.materialdesign.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.gc.materialdesign.R;

public class SnackBar extends Dialog{

	public static final int SHORT = 4 * 1000;
	public static final int LONG = 8 * 1000;
	String text;
	float textSize = 14;//Roboto Regular 14sp 
	String buttonText;
	View.OnClickListener onClickListener;
	Activity activity;
	View view;
	Button button;
	int backgroundSnackBar = Color.parseColor("#eb333333");
	private boolean dismissInProgress;
	
	OnHideListener onHideListener;
	// Timer
	private boolean mIndeterminate = false;
	private int mTimer = SHORT;
	
	// With action button
	public SnackBar(Activity activity, String text, String buttonText, View.OnClickListener onClickListener) {
		super(activity, android.R.style.Theme_Translucent);
		this.activity = activity;
		this.text = text;
		this.buttonText = buttonText;
		this.onClickListener = onClickListener;
	}
	
	// Only text
	public SnackBar(Activity activity, String text) {
		super(activity, android.R.style.Theme_Translucent);
		this.activity = activity;
		this.text = text;
	}
	
	@Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.snackbar);
	    setCanceledOnTouchOutside(false);
	    ((TextView)findViewById(R.id.text)).setText(text); 
	    ((TextView)findViewById(R.id.text)).setTextSize(textSize); //set textSize
		button = (Button) findViewById(R.id.buttonflat);
		if(text == null || onClickListener == null){
			button.setVisibility(View.GONE);
		}else{
			button.setText(buttonText);
			button.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dismiss();
					onClickListener.onClick(v);
				}
			});
		}
		view = findViewById(R.id.snackbar);
		view.setBackgroundColor(backgroundSnackBar);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final View snackbar = getWindow().getDecorView().findViewById(R.id.snackbar);
		int[] xy = new int[2];
		snackbar.getLocationInWindow(xy);
		RectF rect = new RectF(xy[0], xy[1], xy[0] + snackbar.getWidth(), xy[1] + snackbar.getHeight());
		if (!rect.contains(event.getX(), event.getY())&& !dismissInProgress) {
			dismiss();
		}
		return activity.dispatchTouchEvent(event);
	}

	@Override
	public void onBackPressed() {
	}
	
	@Override
	public void show() {
		super.show();
		view.setVisibility(View.VISIBLE);
		view.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.snackbar_show_animation));
		if (!mIndeterminate) {
		    dismissTimer.start();
		}
	}
	
	// Dismiss timer 
	Thread dismissTimer = new Thread(new Runnable() {
		
		@Override
		public void run() {
			try {
				Thread.sleep(mTimer);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			handler.sendMessage(new Message());
		}
	});
	
	Handler handler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			 if(onHideListener != null) {
				 onHideListener.onHide();
			 }
			dismiss();
			return false;
		}
	});
	
	/**
	 * @author Jack Tony
	 */
	@Override
	public void dismiss() {
		dismissInProgress = true;
		Animation anim = AnimationUtils.loadAnimation(activity, R.anim.snackbar_hide_animation);
		anim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				SnackBar.super.dismiss();
			}
		});
		view.startAnimation(anim);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if (keyCode == KeyEvent.KEYCODE_BACK )  {
			 dismiss();
		 }
		return super.onKeyDown(keyCode, event);
	}
	
	public void setMessageTextSize(float size) {
		textSize = size;
	}
	
	public void setIndeterminate(boolean indeterminate) {
        	mIndeterminate = indeterminate;
	}
	
	public boolean isIndeterminate() {
		return mIndeterminate;
	}

	public void setDismissTimer(int time) {
		mTimer = time;
	}
	
	public int getDismissTimer() {
		return mTimer;
	}
	
	/**
	 * Change background color of SnackBar
	 * @param color
	 */
	public void setBackgroundSnackBar(int color){
		backgroundSnackBar = color;
		if(view != null)
			view.setBackgroundColor(color);
	}

	/**
	 * This event start when snackbar dismish without push the button
	 * @author Navas
	 *
	 */
	public interface OnHideListener{
		public void onHide();
	}
	
	public void setOnhideListener(OnHideListener onHideListener){
		this.onHideListener = onHideListener;
	}
}
