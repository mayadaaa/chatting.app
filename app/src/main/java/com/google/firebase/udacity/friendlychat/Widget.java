package com.google.firebase.udacity.friendlychat;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.IOException;
import java.io.InputStream;


public class Widget extends AppWidgetProvider {

    RemoteViews views;
    RemoteViews imageView;
    private static final String ACTION_BROADCASTWIIDGET = "ACTION_BROADCASTWIIDGET";

    private String getInformation(){
        DatabaseReference databaseReference =
                FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.orderByChild("email").equalTo("mayada.abdeen234@gmail.com")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            FriendlyMessage message = dataSnapshot.getChildren().iterator().next().getValue(FriendlyMessage.class);
                            views.setTextViewText(R.id.admin,message.getName()+'\n'+message.getName());
                        }catch (Exception e){}
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
        return "";
    }


    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId) {
        views = new RemoteViews(context.getPackageName(), R.layout.widget);

        Intent intentUpdate = new Intent(context, MainActivity.class);
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] idArray = new int[]{appWidgetId};
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentUpdate, 0);
        new LoadBitmap(views).execute("Group Chat");
        views.setOnClickPendingIntent(R.id.widget_title, pendingIntent);

        Intent secondIntent = new Intent(context, Widget.class);
        secondIntent.setAction(ACTION_BROADCASTWIIDGET);

        context.sendBroadcast(secondIntent);

        PendingIntent pendingUpdate = PendingIntent.getBroadcast(
                context, appWidgetId, intentUpdate,
                PendingIntent.FLAG_UPDATE_CURRENT);

        appWidgetManager.updateAppWidget(appWidgetId, views);


    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {super.onEnabled(context);}

    @Override
    public void onDisabled(Context context) {super.onDisabled(context);}

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_BROADCASTWIIDGET.equals(intent.getAction())) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setTextViewText(R.id.information, getInformation());
            ComponentName componentName = new ComponentName(context, Widget.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(componentName, views);
        }
    }
    public class LoadBitmap extends AsyncTask<String,Void,Bitmap> {
        private RemoteViews views;
        private String url = "http://findicons.com/files/icons/2101/ciceronian/59/photos.png";

        LoadBitmap(RemoteViews views){
            this.views = views;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                InputStream inputStream = new java.net.URL(url).openStream();
                Bitmap bitmap=BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


    }

}

