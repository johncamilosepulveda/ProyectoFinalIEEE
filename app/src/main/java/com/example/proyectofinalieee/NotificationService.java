package com.example.proyectofinalieee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import com.google.firebase.database.FirebaseDatabase;

public class NotificationService extends Service {


    NotificationManager manager;
    FirebaseDatabase db;
    int idNotificacion = 1;

    public static final String CHANNEL_ID = "aplicaciones20182";
    public static final String CHANNEL_NAME = "Aplicaciones-2018-2";
    public static final int CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;

    @Override
    public void onCreate() {
        //Esto corre en el hilo principal
        crearNotificacion();

        //Si quieren descargar algo o hacer un network process
        //Entonces iniciamos un HILO!!!!!!!!!!!

    }

    private void crearNotificacion() {
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Manejar notificaciones en OREO
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //Se tiene que definir un Notficacion Channel
            //Crear 3 constantes: CHANNEL_ID, CHANNEL_NAME, CHANNEL_IMPORTANCE
            NotificationChannel canal = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, CHANNEL_IMPORTANCE);
            manager.createNotificationChannel(canal);
        }
        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(this, CHANNEL_ID)
                .setContentTitle("Logout")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("Se ha cerrado sesión")
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify(idNotificacion, builder.build());

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }



}
