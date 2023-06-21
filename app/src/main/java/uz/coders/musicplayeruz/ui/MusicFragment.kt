package uz.coders.musicplayeruz.ui


import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import me.tankery.lib.circularseekbar.CircularSeekBar
import uz.coders.musicplayeruz.R
import uz.coders.musicplayeruz.app.AppClass
import uz.coders.musicplayeruz.databinding.FragmentMusicBinding
import uz.coders.musicplayeruz.model.Music
import uz.coders.musicplayeruz.service.PlayerService
import uz.coders.musicplayeruz.utils.MusicReaderHelper
import uz.coders.musicplayeruz.utils.SharedPreferencesHelper
import java.io.IOException

class MusicFragment : Fragment(R.layout.fragment_music) {
    private var _binding: FragmentMusicBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<MusicFragmentArgs>()
    private lateinit var musicList: ArrayList<Music>

    private var currentSongId = 0
    private var musicSeekBarMax = 0f
    private val song = AppClass.song
    private var musicSeekbarCurrentPosition = 0
    private var isLooping = false

    private lateinit var audioManager: AudioManager
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMusicBinding.bind(view)

        musicList = MusicReaderHelper().getAllMusic(requireContext())
        audioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        binding.seekBarVolume.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        isLooping = sharedPreferencesHelper.getBoolean("isLooping", false)

        startMusicPlayer()
        seekBarChangeHandler()
        setMusicControllerButtonClicks()

    }

    private fun startMusicPlayer() {
        currentSongId = args.id
        musicSeekBarMax = musicList[currentSongId].duration.toFloat()
        binding.musicCircleSeekbar.max = musicSeekBarMax

        updateSong(currentSongId)
        updateUI(currentSongId)
        requireActivity().startService(Intent(requireContext(), PlayerService::class.java))

    }

    private fun setMusicControllerButtonClicks() {
        with(binding) {
            btnPlayPause.setOnClickListener { playPauseSong(song) }

            btnPrevious.setOnClickListener { previousSong() }

            btnNext.setOnClickListener { nextSong() }

            btnRepeat.setOnClickListener {
                if (isLooping.not()){
                    isLooping = true
                    sharedPreferencesHelper.saveBoolean("isLooping", true)
                    song.isLooping = true
                    binding.btnRepeat.setImageResource(R.drawable.ic_repeat_active)
                } else {
                    isLooping = false
                    sharedPreferencesHelper.saveBoolean("isLooping", false)
                    song.isLooping = false
                    binding.btnRepeat.setImageResource(R.drawable.ic_repeat)
                }
            }

        }
    }


    private fun playPauseSong(song: MediaPlayer) {
        if (song.isPlaying) {
            song.pause()
            binding.btnPlayPause.setImageResource(R.drawable.ic_play)
        } else {
            song.start()
            binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
            updateProgress()
        }

    }

    private fun previousSong() {
        if (currentSongId != 0) currentSongId--
        else currentSongId = musicList.lastIndex

        updateSong(currentSongId)
        updateUI(currentSongId)
    }

    private fun nextSong() {
        if (currentSongId != musicList.lastIndex) currentSongId++
        else currentSongId = 0

        updateSong(currentSongId)
        updateUI(currentSongId)
    }

    private fun updateSong(currentSongId: Int) {
        song.stop()
        song.reset()
        song.isLooping = isLooping
        try {
            song.setDataSource(musicList[currentSongId].filePath)
            song.prepare()
            song.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun updateUI(currentSongId: Int) {
        with(binding) {
            MusicReaderHelper().loadAlbumArt(
                requireContext(),
                Uri.parse(musicList[currentSongId].filePath),
                imageSongCover
            )
            tvSongName.text = musicList[currentSongId].songName
            tvCurrentSongPosition.text =
                (currentSongId.plus(1)).toString().plus("/").plus(musicList.size)

            musicCircleSeekbar.max = musicList[currentSongId].duration.toFloat()

            if (song.isPlaying) btnPlayPause.setImageResource(R.drawable.ic_pause)
            else btnPlayPause.setImageResource(R.drawable.ic_play)

            if (isLooping) btnRepeat.setImageResource(R.drawable.ic_repeat_active)
            else btnRepeat.setImageResource(R.drawable.ic_repeat)

            updateProgress()

            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            seekBarVolume.progress = currentVolume
            tvVolumeInfo.text = currentVolume.toString()
        }
    }

    private fun updateProgress() {
        val thread = Thread {
            Thread.sleep(50)
            try {
                while (song.isPlaying) {
                    Thread.sleep(950)
                    try {
                        requireActivity().runOnUiThread {
                            musicSeekbarCurrentPosition = song.currentPosition
                            musicSeekBarMax = song.duration.toFloat() - musicSeekbarCurrentPosition

                            binding.tvCurrentDuration.text = timeFormatter(musicSeekbarCurrentPosition)
                            binding.tvLessDuration.text = timeFormatter(musicSeekBarMax.toInt())
                            binding.musicCircleSeekbar.progress = musicSeekbarCurrentPosition.toFloat()

                            if (!isLooping && binding.tvLessDuration.text.equals("00:00")) nextSong()

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    private fun seekBarChangeHandler() {
        binding.musicCircleSeekbar.setOnSeekBarChangeListener(object :
            CircularSeekBar.OnCircularSeekBarChangeListener {
            override fun onProgressChanged(
                circularSeekBar: CircularSeekBar?,
                progress: Float,
                fromUser: Boolean
            ) {
                if (fromUser){
                    binding.musicCircleSeekbar.progress = progress
                    song.seekTo(progress.toInt())
                }
            }

            override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {}
            override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {}
        })


        binding.seekBarVolume.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val volume = (progress.toFloat() / seekBar.max * maxVolume).toInt()
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)

                binding.tvVolumeInfo.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}

        })
    }

    private fun timeFormatter(time: Int): String {
        val timeFormatResult = StringBuilder()
        val min = time / 1000 / 60
        val sec = time / 1000 % 60

        if (min < 10) {
            timeFormatResult.append("0").append(min).append(":")
        } else {
            timeFormatResult.append(min).append(":")
        }

        if (sec < 10) {
            timeFormatResult.append("0").append(sec)
        } else {
            timeFormatResult.append(sec)
        }

        return timeFormatResult.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
