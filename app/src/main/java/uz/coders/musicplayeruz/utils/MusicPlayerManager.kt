package uz.coders.musicplayeruz.utils

import android.content.Context
import android.media.MediaPlayer
import uz.coders.musicplayeruz.model.Music
import java.io.IOException

/* 
* Created by Coder Odilov on 20/06/2023
*/

class MusicPlayerManager {
    companion object{
        private lateinit var mediaPlayer: MediaPlayer
        private lateinit var musicList : ArrayList<Music>

        private var currentSongId = 0

        fun createMusicPlayer(context: Context){
            musicList = MusicReaderHelper().getAllMusic(context)
            mediaPlayer = MediaPlayer()

        }

        fun update(songId:Int){
            this.currentSongId = songId
            if (isPlaying()){
                mediaPlayer.stop()
                mediaPlayer.reset()
            }
            try {
                mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(musicList[songId].filePath)
                mediaPlayer.prepare()
                mediaPlayer.start()
            }catch (e:IOException){
                e.printStackTrace()
            }
        }

        fun play(){
            if (mediaPlayer.isPlaying.not()) mediaPlayer.start()
        }

        fun pause(){
            if (mediaPlayer.isPlaying) mediaPlayer.pause()
        }

        fun prevMusic(){
            if (currentSongId != 0){
                update(--currentSongId)
            } else{
                currentSongId = musicList.lastIndex
                update(currentSongId)
            }
        }

        fun nextMusic(){
            if (currentSongId < musicList.lastIndex){
                update(++currentSongId)
            } else{
                currentSongId = 0
                update(currentSongId)
            }
        }

        fun isPlaying():Boolean{
            return mediaPlayer.isPlaying
        }

        fun resetPlayer(){
            if(mediaPlayer.isPlaying){
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.release()
            }
        }
    }

}

