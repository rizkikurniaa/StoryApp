package com.kikulabs.storyapp.ui.story

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.kikulabs.storyapp.R
import com.kikulabs.storyapp.data.model.UserPreference
import com.kikulabs.storyapp.databinding.ActivityAddStoryBinding
import com.kikulabs.storyapp.ui.main.MainActivity
import com.kikulabs.storyapp.utils.ViewModelFactory
import com.kikulabs.storyapp.utils.createTempFile
import com.kikulabs.storyapp.utils.reduceFileImage
import com.kikulabs.storyapp.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddStoryActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var token: String
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.not_get_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        initListener()
        initViewModel()
    }

    private fun initListener() {
        binding.btnFromCamera.setOnClickListener(this)
        binding.btnFromGallery.setOnClickListener(this)
        binding.btnUpload.setOnClickListener(this)
    }

    private fun initViewModel() {
        addStoryViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddStoryViewModel::class.java]

        addStoryViewModel.getUser().observe(this) { user ->
            token = user.token
        }


        addStoryViewModel.isLoading.observe(this) { showLoading(it) }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.kikulabs.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile

            val result = BitmapFactory.decodeFile(myFile.path)

            binding.ivPreview.setImageBitmap(result)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)

            getFile = myFile

            binding.ivPreview.setImageURI(selectedImg)
        }

    }

    private fun uploadStory() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            val description =
                binding.tietDesc.text.toString().toRequestBody("text/plain".toMediaType())


            addStoryViewModel.addStory(token, imageMultipart, description)

            addStoryViewModel.errorText.observe(this) {
                if (it == "false") {
                    Toast.makeText(this@AddStoryActivity,
                        getString(R.string.story_uploaded),
                        Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this@AddStoryActivity as Activity)
                            .toBundle()
                    )
                    finish()
                }
            }

        } else {
            Toast.makeText(this@AddStoryActivity,
                getString(R.string.add_picture_first),
                Toast.LENGTH_SHORT).show()
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_from_camera -> {
                startTakePhoto()
            }
            R.id.btn_from_gallery -> {
                startGallery()
            }
            R.id.btn_upload -> {
                uploadStory()
            }
        }
    }
}