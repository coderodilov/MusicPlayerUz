package uz.coders.musicplayeruz.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/* 
* Created by Coder Odilov on 21/06/2023
*/

class MusicPlayerViewModel:ViewModel() {
    private var _currentSongId = MutableLiveData<Int>()
    val currentSongId:LiveData<Int> get() = _currentSongId

    private var _isPlaying = MutableLiveData<Boolean>()
    val isPlaying:LiveData<Boolean> get() = _isPlaying

    fun updateSongId(songId:Int){
        _currentSongId.postValue(songId)
    }

    fun updateIsPlaying(isPlaying:Boolean){
        _isPlaying.postValue(isPlaying)
    }

}