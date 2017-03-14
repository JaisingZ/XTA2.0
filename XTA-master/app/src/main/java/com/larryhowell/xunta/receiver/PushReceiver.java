package com.larryhowell.xunta.receiver;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.larryhowell.xunta.R;
import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.common.UtilBox;
import com.larryhowell.xunta.ui.BindListActivity;
import com.larryhowell.xunta.ui.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

public class PushReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            Config.device_token = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.i(TAG, Config.device_token);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
			try {
				processCustomMessage(context, bundle);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        	Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
        	Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
	}

	private void processCustomMessage(Context context, Bundle bundle) throws JSONException {
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);

		Intent intent = new Intent();

		JSONObject jsonObject = new JSONObject(message);
		String type = jsonObject.getString("type");

		if ("bind".equals(type)) {
			Config.bindMessage = message;
			intent = new Intent(context, BindListActivity.class);
		} else if ("confirm".equals(type)) {
			Config.confirmMessage = message;
			intent = new Intent(context, MainActivity.class);
		} else if ("location".equals(type)) {
			Config.locationMessage = message;
			intent = new Intent(context, MainActivity.class);
		}

		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.icon_small);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setLargeIcon(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.icon));
        builder.setContentTitle("寻ta");
        builder.setContentText("你有一条新消息");
		builder.setVibrate(new long[]{0L,100L,200L,300L});

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(
                        Activity.NOTIFICATION_SERVICE);
        notificationManager.notify(Integer.valueOf(UtilBox.getCurrentTime().substring(8)), builder.build());

		Log.i(TAG, bundle.getString(JPushInterface.EXTRA_MESSAGE));
	}
}
