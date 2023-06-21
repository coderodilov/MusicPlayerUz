package uz.coders.musicplayeruz.service


import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import uz.coders.musicplayeruz.MainActivity
import uz.coders.musicplayeruz.R
import uz.coders.musicplayeruz.model.Music
import uz.coders.musicplayeruz.receiver.NotificationReceiver
import uz.coders.musicplayeruz.app.AppClass
import uz.coders.musicplayeruz.utils.MusicReaderHelper

/* 
* Created by Coder Odilov on 18/06/2023
*/

class PlayerService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var musicList: ArrayList<Music>
    private lateinit var mediaSession: MediaSessionCompat

    private var currentSongPosition = 0

    private val binder = MediaPlayerBinder()

    inner class MediaPlayerBinder : Binder() {
        fun getService(): PlayerService {
            return this@PlayerService
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()

        mediaPlayer = MediaPlayer()
        mediaPlayer.isLooping = false
        musicList = MusicReaderHelper().getAllMusic(this)
        mediaSession = MediaSessionCompat(this, "MyMediaSession")

        startForeground(1, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }


    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val prevIntent =
            Intent(this, NotificationReceiver::class.java).setAction(AppClass.ACTION_PREVIOUS)
        val prevPendingIntent =
            PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_IMMUTABLE)

        val nextIntent =
            Intent(this, NotificationReceiver::class.java).setAction(AppClass.ACTION_NEXT)
        val nextPendingIntent =
            PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE)

        val playIntent =
            Intent(this, NotificationReceiver::class.java).setAction(AppClass.ACTION_PLAY)
        val playPendingIntent =
            PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_IMMUTABLE)

        val largeIcon = BitmapFactory.decodeResource(this.resources, R.drawable.song_logo)

        val notification = NotificationCompat.Builder(this, AppClass.CHANNEL_2_ID)
            .setContentTitle(musicList[currentSongPosition].songName)
            .setContentText(musicList[currentSongPosition].artist)
            .setSmallIcon(R.drawable.song_logo)
            .setLargeIcon(largeIcon)
            .addAction(R.drawable.ic_prev, "Previous", prevPendingIntent)
            .addAction(R.drawable.ic_play, "Play", playPendingIntent)
            .addAction(R.drawable.ic_next, "Next", nextPendingIntent)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationManager.IMPORTANCE_LOW)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)

        return notification.build()
    }

    override fun onDestroy() {
        mediaPlayer.stop()
        super.onDestroy()
    }

}
