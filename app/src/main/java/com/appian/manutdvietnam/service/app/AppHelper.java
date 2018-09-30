package com.appian.manutdvietnam.service.app;

import android.content.Context;
import android.content.Intent;

import com.appian.manutdvietnam.data.app.Language;

public final class AppHelper {

    public static void loadAppConfig(final Context context) {
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(AppService.ACTION_LOAD_APP_CONFIG);
        context.startService(intent);
    }

    public static void refreshDeviceToken(final Context context) {
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(AppService.ACTION_REFRESH_DEVICE_TOKEN);
        context.startService(intent);
    }

    public static void changeLanguage(final Context context, String language) {
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(AppService.ACTION_CHANGE_LANGUAGE);
        intent.putExtra(Language.EXTRA_LANGUAGE, language);
        context.startService(intent);
    }

    public static void followEventMatch(final Context context, String event, boolean isSubscribe) {
        Intent intent = new Intent(context, AppService.class);
        intent.putExtra(AppService.KEY_EVENT_MATCH_TYPE, event);
        intent.putExtra(AppService.KEY_EVENT_MATCH_VALUE, isSubscribe);
        intent.setAction(AppService.ACTION_SETTING_EVENT_MATCH);
        context.startService(intent);
    }

    public static void followAll(final Context context, boolean isOn) {
        Intent intent = new Intent(context, AppService.class);
        intent.putExtra(AppService.KEY_NOTIFICATION_VALUE, isOn);
        intent.setAction(AppService.ACTION_CHANGE_NOTIFICATION);
        context.startService(intent);
    }

    public static void followNews(final Context context, boolean isOn) {
        Intent intent = new Intent(context, AppService.class);
        intent.putExtra(AppService.KEY_NOTIFICATION_VALUE, isOn);
        intent.setAction(AppService.ACTION_FOLLOW_NEWS);
        context.startService(intent);
    }
}
