package com.codeoregonapp.patrickleonard.tempestatibus.notifications;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.database.CachedNotificationDataSource;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.WeatherAlert;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Notification helper class.
 * Created by Patrick Leonard on 7/11/2016.
 */
public class SevereWeatherNotificationHelper  {

    public static final String TAG = SevereWeatherNotificationHelper.class.getSimpleName();
    List<WeatherAlert> mIncomingAlerts;
    List<WeatherAlert> mCurrentAlerts;
    NotificationManager mNotificationManager;
    CachedNotificationDataSource mCachedNotificationDataSource;
    Context mContext;

    public List<WeatherAlert> getIncomingAlerts() {
        return mIncomingAlerts;
    }

    public void setIncomingAlerts(List<WeatherAlert> mIncomingAlerts) {
        this.mIncomingAlerts = mIncomingAlerts;
    }

    public List<WeatherAlert> getCurrentAlerts() {
        return mCurrentAlerts;
    }

    public void setCurrentAlerts(List<WeatherAlert> mCurrentAlerts) {
        this.mCurrentAlerts = mCurrentAlerts;
    }

    public CachedNotificationDataSource getCachedNotificationDataSource() {
        return mCachedNotificationDataSource;
    }

    public void setCachedNotificationDataSource(CachedNotificationDataSource cachedNotificationDataSource) {
        this.mCachedNotificationDataSource = cachedNotificationDataSource;
    }

    public SevereWeatherNotificationHelper(Context context) {
        mContext = context;
        mNotificationManager =(NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        setCachedNotificationDataSource(new CachedNotificationDataSource(mContext));
        setCurrentAlerts(getCachedNotificationDataSource().readNotifications());
    }

    public void processAlerts() {
        if(getIncomingAlerts() != null &&
            !getIncomingAlerts().isEmpty() &&
            !getCurrentAlerts().isEmpty())
            {
                checkIncomingAgainstCurrent();
            }
        else if(getCurrentAlerts().isEmpty()) {
            for(int i=0;i<getIncomingAlerts().size();++i) {
                WeatherAlert incoming = getIncomingAlerts().get(i);
                addIncomingToCurrent(incoming);
            }
        }
        getIncomingAlerts().clear();
        removeExpiredNotifications();
    }

    private void checkIncomingAgainstCurrent() {
        boolean matched;
        for(int i=0;i<getIncomingAlerts().size();++i) {
            WeatherAlert incoming = getIncomingAlerts().get(i);
            matched = false;
            for(int j=0;j<getCurrentAlerts().size();++j) {
                if(getCurrentAlerts().get(j).equals(incoming)) {
                    matched = true;
                    break;
                }
            }
            if(!matched) {
                addIncomingToCurrent(incoming);
            }
        }
    }

    private void addIncomingToCurrent(WeatherAlert incoming) {
        incoming.setUUID(UUID.randomUUID().toString());
        incoming.setID(UUID.randomUUID().hashCode());
        sendNotification(incoming);
        getCurrentAlerts().add(incoming);
        getCachedNotificationDataSource().createNotificationRecord(incoming);
    }

    private void sendNotification(WeatherAlert incoming) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setContentInfo("Weather Alert")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(incoming.getTitle())
                .setContentText("Tap to view alert online.");
        builder = setClickIntent(incoming, builder);
        mNotificationManager.notify(incoming.getUUID(), incoming.getID(), builder.build());
    }

    private NotificationCompat.Builder setClickIntent(WeatherAlert incoming, NotificationCompat.Builder builder) {
        Intent alertIntent = new Intent(Intent.ACTION_VIEW);
        alertIntent.setData(Uri.parse(incoming.getUriString()));
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,0,alertIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        return builder;
    }

    private void removeExpiredNotifications() {
        Iterator<WeatherAlert> iterator = getCurrentAlerts().iterator();
        while(iterator.hasNext()) {
            WeatherAlert alert = iterator.next();
            Log.d(SevereWeatherNotificationHelper.TAG,"Testing alert: " + alert.getUUID() + " ID:" + alert.getID());
            Log.d(SevereWeatherNotificationHelper.TAG,"Expires:" + alert.getExpires());
            if((System.currentTimeMillis()/1000) >= alert.getExpires() ) {
                getCachedNotificationDataSource().deleteNotificationRecord(alert.getUUID(),alert.getID());
                iterator.remove();
            }
        }
    }
}
