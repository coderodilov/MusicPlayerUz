package uz.coders.musicplayeruz.adapter


import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.coders.musicplayeruz.databinding.MusicItemBinding
import uz.coders.musicplayeruz.model.Music
import uz.coders.musicplayeruz.utils.MusicReaderHelper


/* 
* Created by Coder Odilov on 17/06/2023
*/

class MusicListAdapter(private val musicList: ArrayList<Music>, private val context: Context):RecyclerView.Adapter<MusicListAdapter.ViewHolder>() {

    private lateinit var onItemClicked: OnItemClicked

    inner class ViewHolder(private val binding:MusicItemBinding):RecyclerView.ViewHolder(binding.root){
        fun onBind(music: Music){
            binding.tvSongName.text = music.songName
            binding.tvSongDate.text = music.artist
            MusicReaderHelper().loadAlbumArt(context, Uri.parse(music.filePath), binding.imageSong)

            binding.root.setOnClickListener{
                onItemClicked.setOnItemClickListener(adapterPosition)
            }

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = MusicItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(musicList[position])
    }

    fun interface OnItemClicked{
        fun setOnItemClickListener(position: Int)
    }

    fun setOnItemClickListener(listener:OnItemClicked){
        onItemClicked = listener
    }
}

