package uz.coders.musicplayeruz.utils

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModelProvider
import uz.coders.musicplayeruz.model.Music
import uz.coders.musicplayeruz.ui.MusicPlayerViewModel
import java.io.IOException
import kotlin.coroutines.coroutineContext

/* 
* Created by Coder Odilov on 20/06/2023
*/

class MusicPlayerManager {
    companion object{
        private lateinit var mediaPlayer: MediaPlayer
        private lateinit var musicList : ArrayList<Music>

        private var currentSongId = 0
        private val musicPlayerViewModel = MusicPlayerViewModel()

        fun createMusicPlayer(context: Context){
            musicList = MusicReaderHelper().getAllMusic(context)
            mediaPlayer = MediaPlayer()
        }

        fun update(songId:Int){
            musicPlayerViewModel.updateSongId(songId)
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
            musicPlayerViewModel.updateIsPlaying(mediaPlayer.isPlaying)
        }

        fun pause(){
            if (mediaPlayer.isPlaying) mediaPlayer.pause()
            musicPlayerViewModel.updateIsPlaying(mediaPlayer.isPlaying)
        }

        fun prevMusic(){
            if (currentSongId != 0){
                update(--currentSongId)
                musicPlayerViewModel.updateSongId(--currentSongId)
            } else{
                currentSongId = musicList.lastIndex
                update(currentSongId)
                musicPlayerViewModel.updateSongId(currentSongId)
            }
        }

        fun nextMusic(){
            if (currentSongId < musicList.lastIndex){
                update(++currentSongId)
                musicPlayerViewModel.updateSongId(++currentSongId)
            } else{
                currentSongId = 0
                update(currentSongId)
                musicPlayerViewModel.updateSongId(currentSongId)
            }
        }

        fun isPlaying():Boolean{
            return mediaPlayer.isPlaying
        }
    }

}

