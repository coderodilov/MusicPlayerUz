package uz.coders.musicplayeruz.model

/* 
* Created by Coder Odilov on 17/06/2023
*/
data class Music(
    val songName:String,
    val artist:String,
    val album:String,
    val duration:Long = 0L,
    val dateAdded:String,
    val filePath :String,
    var isPlaying:Boolean = false
)
