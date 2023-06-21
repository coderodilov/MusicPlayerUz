package uz.coders.musicplayeruz.ui


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import me.tankery.lib.circularseekbar.CircularSeekBar
import uz.coders.musicplayeruz.R
import uz.coders.musicplayeruz.databinding.FragmentMusicBinding
import uz.coders.musicplayeruz.model.Music
import uz.coders.musicplayeruz.service.PlayerService
import uz.coders.musicplayeruz.utils.MusicReaderHelper
import uz.coders.musicplayeruz.utils.SharedPreferencesHelper


class MusicFragment : Fragment(R.layout.fragment_music) {
    private var _binding: FragmentMusicBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<MusicFragmentArgs>()
    private lateinit var musicList: ArrayList<Music>

    private lateinit var audioManager: AudioManager
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    private var mediaPlayerService: PlayerService? = null
    private var isBound: Boolean = false
    private lateinit var viewModel: MusicPlayerViewModel

    private lateinit var mediaPlayer:MediaPlayer
    private var currentSongId = 0

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as PlayerService.MediaPlayerBinder
            mediaPlayerService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mediaPlayerService = null
            isBound = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMusicBinding.bind(view)

        musicList = MusicReaderHelper().getAllMusic(requireContext())
        audioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager

        viewModel = ViewModelProvider(this)[MusicPlayerViewModel::class.java]
        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        currentSongId = args.id

        startMusicPlayer()
        seekBarChangeHandler()
        loadButtonClicks()

    }

    private fun startMusicPlayer() {}

    private fun loadButtonClicks() {
        with(binding){
            btnPlayPause.setOnClickListener {}

            btnPrevious.setOnClickListener {}

            btnNext.setOnClickListener {}

        }
    }

    private fun seekBarChangeHandler() {
        binding.musicCircleSeekbar.setOnSeekBarChangeListener(object :
            CircularSeekBar.OnCircularSeekBarChangeListener {
            override fun onProgressChanged(
                circularSeekBar: CircularSeekBar?,
                progress: Float,
                fromUser: Boolean) {}

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


    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(requireContext(), PlayerService::class.java)
        requireContext().bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
