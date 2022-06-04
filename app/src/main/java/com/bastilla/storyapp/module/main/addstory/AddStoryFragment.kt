package com.bastilla.storyapp.module.main.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.bastilla.storyapp.R
import com.bastilla.storyapp.databinding.FragmentAddStoryBinding
import com.bastilla.storyapp.module.main.MainActivity
import com.bastilla.storyapp.module.sign.SignViewModel
import com.bastilla.storyapp.module.sign.ViewModelFactory
import com.bastilla.storyapp.utils.compressFileImage
import com.bastilla.storyapp.utils.rotateBitmap
import com.bastilla.storyapp.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryFragment : Fragment() {

    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var factory: ViewModelFactory
    private val viewModel: AddStoryViewModel by activityViewModels { factory }
    private val viewModelSign: SignViewModel by activityViewModels { factory }
    private lateinit var navItems: View
    private lateinit var result: Bitmap
    private var getFile: File? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var location: Location


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        factory = ViewModelFactory.getInstance(requireActivity())
        navItems = requireActivity().findViewById(R.id.nav_items)
        navItems.visibility = View.GONE
        binding.imageResult.setOnClickListener { startCameraX() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCameraX() }

        getPermission()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getMyLastLocation()
        viewModel.isLoading.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                loading(it)
            }
        }
        viewModel.error.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { error ->
                if (!error) {
                    startActivity(Intent(activity, MainActivity::class.java))
                }
            }
        }
        viewModel.message.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(activity, "Upload Failed", Toast.LENGTH_LONG).show()
            }
        }

        binding.addStory.setOnClickListener {
            val description = binding.edtDescription.text.toString()
            val lat = location.latitude.toFloat()
            val lon = location.longitude.toFloat()
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            getMyLastLocation()
            if (description.isNotEmpty()) {
                loading(true)
                addStory(description, lat, lon)
            } else {
                Toast.makeText(activity, "Fill the Description!", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun startCameraX() {
        val intent = Intent(activity, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun addStory(description: String, lat: Float, lon: Float) {
        if (getFile != null) {
            val file = compressFileImage(getFile as File)
            val requestDescription = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            viewModelSign.getUserToken().observe(viewLifecycleOwner) {
                viewModel.uploadStory(imageMultipart, requestDescription, it, lat, lon)
            }
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val file = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            getFile = file
            result = rotateBitmap(
                BitmapFactory.decodeFile(file.path),
                isBackCamera
            )
            binding.addStory.isEnabled = true
            binding.imageResult.setImageBitmap(result)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedFiles: Uri = it.data?.data as Uri
            val files = uriToFile(selectedFiles, requireActivity())
            getFile = files
            binding.addStory.isEnabled = true
            binding.imageResult.setImageURI(selectedFiles)
        }
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.addStory.isEnabled = false
            binding.tvStatus.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.addStory.isEnabled = true
            binding.tvStatus.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }



    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { locs: Location? ->
                if (locs != null) {
                    location = locs
                } else {
                    Toast.makeText(
                        activity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                }
            }
        }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getPermission() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        navItems.visibility = View.VISIBLE
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        const val CAMERA_X_RESULT = 200
    }

}