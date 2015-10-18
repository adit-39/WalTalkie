package com.walmart.easycart;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;

public class ChatHeadService extends Service {

	private WindowManager windowManager;
	private ImageView chatHead;

	@Override
	public IBinder onBind(Intent intent) {
		// Not used
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		chatHead = new ImageView(this);
		chatHead.setImageResource(R.drawable.head);
		chatHead.setMaxHeight(5);
		chatHead.setMaxWidth(5);

		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 0;
		params.y = 200;

		windowManager.addView(chatHead, params);

		chatHead.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						MainActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}
		});

		OnTouchListener recButtonOnTouchListener = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				WindowManager.LayoutParams params = new WindowManager.LayoutParams(
						WindowManager.LayoutParams.WRAP_CONTENT,
						WindowManager.LayoutParams.WRAP_CONTENT,
						WindowManager.LayoutParams.TYPE_PHONE,
						WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
						PixelFormat.TRANSLUCENT);

				params.gravity = Gravity.TOP | Gravity.LEFT;

				int recButtonLastX = 0, recButtonFirstY = 0, recButtonFirstX = 0, recButtonLastY = 0;
				boolean touchconsumedbyMove = false;
				switch (event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:
					recButtonLastX = (int) event.getRawX();
					recButtonLastY = (int) event.getRawY();
					recButtonFirstX = recButtonLastX;
					recButtonFirstY = recButtonLastY;
					break;
				case MotionEvent.ACTION_UP:
					new Thread(new Runnable() {
						public void run() {
							try {
								Thread.sleep(300);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							chatHead.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent i = new Intent(
											getApplicationContext(),
											MainActivity.class);
									i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									startActivity(i);
								}
							});
						}
					}).start();
					break;
				case MotionEvent.ACTION_MOVE:
					int deltaX = (int) event.getRawX() - recButtonLastX;
					int deltaY = (int) event.getRawY() - recButtonLastY;
					recButtonLastX = (int) event.getRawX();
					recButtonLastY = (int) event.getRawY();
					if (Math.abs(deltaX) >= 5 || Math.abs(deltaY) >= 5) {
						if (event.getPointerCount() == 1) {
							params.x = (int) event.getRawX() - 2;
							params.y = (int) event.getRawY() + 2;
							touchconsumedbyMove = true;
							windowManager.updateViewLayout(chatHead, params);
							chatHead.setOnClickListener(null);
						} else {
							touchconsumedbyMove = false;
						}
					} else {
						touchconsumedbyMove = false;
					}
					break;
				default:
					break;
				}

				return touchconsumedbyMove;
			}
		};
		chatHead.setOnTouchListener(recButtonOnTouchListener);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (chatHead != null)
			windowManager.removeView(chatHead);
	}

}
