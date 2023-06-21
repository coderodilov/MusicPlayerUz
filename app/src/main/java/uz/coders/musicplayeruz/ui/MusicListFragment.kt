package uz.coders.musicplayeruz.ui

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import uz.coders.musicplayeruz.R
import uz.coders.musicplayeruz.adapter.MusicListAdapter
import uz.coders.musicplayeruz.databinding.AskPermissionDialogBinding
import uz.coders.musicplayeruz.databinding.FragmentMusicListBinding
import uz.coders.musicplayeruz.model.Music
import uz.coders.musicplayeruz.utils.MusicReaderHelper

class MusicListFragment : Fragment(R.layout.fragment_music_list) {
    private var _binding: FragmentMusicListBinding? = null
    private val binding get() = _binding!!

    private lateinit var musicListAdapter: MusicListAdapter
    private lateinit var musicList: ArrayList<Music>

    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var myActivityResultLauncher: ActivityResultLauncher<Intent>

    private lateinit var askPermissionDialog:Dialog
    private var isStoragePermissionDenied = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMusicListBinding.bind(view)
        askPermissionDialog = Dialog(requireContext())

        loadResultLaunchers()
        if (isStoragePermissionGranted()) loadMusicListRv() else  requestStoragePermission()

    }

    private fun loadResultLaunchers() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    loadMusicListRv()
                } else {
                    isStoragePermissionDenied = true
                    showPermissionDialog()
                }
            }

        myActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (isStoragePermissionGranted()) {
                    askPermissionDialog.dismiss()
                    loadMusicListRv()
                }
            }

    }

    private fun showPermissionDialog() {
        val dialogBinding =
            AskPermissionDialogBinding.inflate(LayoutInflater.from(requireActivity()))
        askPermissionDialog.setContentView(dialogBinding.root)
        askPermissionDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        askPermissionDialog.setCancelable(false)

        dialogBinding.btnAskPermission.setOnClickListener {
            if (isStoragePermissionDenied){
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                myActivityResultLauncher.launch(intent)
            } else{
                askPermissionDialog.dismiss()
            }
        }

        dialogBinding.tvCloseApp.setOnClickListener{
            requireActivity().finish()
        }

        askPermissionDialog.show()
    }

    private fun loadMusicListRv() {
        musicList = MusicReaderHelper().getAllMusic(requireContext())
        musicListAdapter = MusicListAdapter(musicList, requireContext())
        binding.rvMusic.adapter = musicListAdapter

        musicListAdapter.setOnItemClickListener{
            val direction = MusicListFragmentDirections.actionMusicListFragmentToSongFragment(it)
            findNavController().navigate(direction)
        }
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun isStoragePermissionGranted():Boolean{
        return ContextCompat.checkSelfPermission(requireContext(),
           Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}