@file:Suppress("NAME_SHADOWING")

package uz.coders.musicplayeruz.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import com.bumptech.glide.Glide
import uz.coders.musicplayeruz.R
import uz.coders.musicplayeruz.model.Music
import java.io.File

/* 
* Created by Coder Odilov on 17/06/2023
*/

class MusicReaderHelper {

    fun getAllMusic(context: Context): ArrayList<Music> {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
        )
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER

        val cursor = context.contentResolver.query(uri, projection, selection, null, sortOrder)

        val musicList = ArrayList<Music>()

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val songName = cursor.getString(with(cursor) { getColumnIndex(MediaStore.Audio.Media.TITLE) })
                val artist = cursor.getString(with(cursor) { getColumnIndex(MediaStore.Audio.Media.ARTIST) })
                val duration =
                    cursor.getLong(with(cursor) { getColumnIndex(MediaStore.Audio.Media.DURATION) })
                val dateAdded =
                    cursor.getString(with(cursor) { getColumnIndex(MediaStore.Audio.Media.DATE_ADDED) })
                val filePath = cursor.getString(with(cursor) { getColumnIndex(MediaStore.Audio.Media.DATA) })
                val albumId = cursor.getLong(with(cursor) { getColumnIndex(MediaStore.Audio.Media.ALBUM_ID) })

                val music = Music(songName, artist, albumId.toString() ,duration, dateAdded, filePath)
                val file = File(music.filePath)
                if (file.exists()) musicList.add(music)


            } while (cursor.moveToNext())
            cursor.close()
        }
        return musicList
    }

    fun loadAlbumArt(context: Context, musicUri: Uri, imageView: ImageView) {
        val projection = arrayOf(MediaStore.Audio.Media.ALBUM_ID)
        val selection = "${MediaStore.Audio.Media.DATA} = ?"
        val selectionArgs = arrayOf(musicUri.path)
        val cursor: Cursor? = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        cursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                val albumId =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                val albumArtUri = Uri.parse("content://media/external/audio/albumart/$albumId")

                Glide.with(context).load(albumArtUri).placeholder(R.drawable.default_album_art)
                    .into(imageView)

            } else {
                Glide.with(context)
                    .load(R.drawable.default_album_art)
                    .into(imageView)
            }

        }
    }

}

