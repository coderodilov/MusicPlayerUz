package uz.coders.musicplayeruz.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.MediaPlayer
import uz.coders.musicplayeruz.utils.MusicPlayerManager

/* 
* Created by Coder Odilov on 20/06/2023
*/

class AppClass:Application() {
    companion object{
        const val CHANNEL_1_ID = "ChannelId1"
        const val ACTION_NEXT = "Next"
        const val ACTION_PREVIOUS = "Previous"
        const val ACTION_PLAY = "Play"
        val song = MediaPlayer()
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        MusicPlayerManager.createMusicPlayer(applicationContext)
    }

    private fun createNotificationChannel() {

        val channel1 = NotificationChannel(CHANNEL_1_ID, "Channel1", NotificationManager.IMPORTANCE_LOW)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        channel1.description = "Channel 1"
        channel1.setSound(null, null)

        notificationManager.createNotificationChannel(channel1)

    }

}