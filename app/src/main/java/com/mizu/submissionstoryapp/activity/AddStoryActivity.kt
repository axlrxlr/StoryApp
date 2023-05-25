package com.mizu.submissionstoryapp.activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.ContextThemeWrapper
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.mizu.submissionstoryapp.R
import com.mizu.submissionstoryapp.activity.viewmodel.AddStoryViewModel
import com.mizu.submissionstoryapp.databinding.ActivityAddStoryBinding
import com.mizu.submissionstoryapp.reduceFileImage
import com.mizu.submissionstoryapp.rotateFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

@Suppress("DEPRECATION")
class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var getFile: File? = null
    private lateinit var token: String
    private val viewModel by viewModels<AddStoryViewModel>()

    companion object{

        const val CAMERA_X_RESULT = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = intent.getStringExtra(StoryListActivity.USER_TOKEN).toString()


        val titleColor = ContextCompat.getColor(this, R.color.black)
        val title = SpannableString(getString(R.string.post_story))
        title.setSpan(ForegroundColorSpan(titleColor), 0, title.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        supportActionBar?.title = title

        val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("picture", File::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("picture")
        } as? File

        val isBackCamera = intent.getBooleanExtra("isBackCamera", true)
        val isImported = intent.getBooleanExtra("isImported", false)

        if (!isImported){
            myFile?.let { file ->
                getFile = file
                rotateFile(myFile, isBackCamera)
                binding.ivImagePreview.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        } else {
            myFile?.let { file ->
                getFile = file
                val exif = ExifInterface(file.path)

                val rotationAngle = when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)){
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }

                val bitmap = BitmapFactory.decodeFile(file.path)
                val rotatedBitmap = if (rotationAngle != 0) {
                    val matrix = Matrix().apply { postRotate(rotationAngle.toFloat()) }
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                } else {
                    bitmap
                }
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
                binding.ivImagePreview.setImageBitmap(rotatedBitmap)
            }

        }
        binding.btnUpload.setOnClickListener {

            binding.etDescription.clearFocus()
            uploadImage()
        }

        viewModel.isLoading.observe(this){
            showLoading(it)
        }

        viewModel.postResult.observe(this) {
            val backToList = Intent(this@AddStoryActivity, StoryListActivity::class.java)
            backToList.putExtra(StoryListActivity.USER_TOKEN, token)
            startActivity(backToList)
            finish()
        }

    }

    private fun showLoading(it: Boolean) {
        if (it) {
            binding.btnUpload.setBackgroundColor(resources.getColor(R.color.light_grey))
            binding.pbLoading.visibility = View.VISIBLE
        }else{
            binding.btnUpload.setBackgroundColor(resources.getColor(R.color.black))
            binding.pbLoading.visibility = View.GONE
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        binding.etDescription.clearFocus()
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.CustomAlertDialogStyle))
            .setTitle(getString(R.string.discard))
            .setMessage(getString(R.string.discard_desc))
            .setPositiveButton(R.string.btn_yes) { _, _ ->
                val backToCam = Intent(this@AddStoryActivity, CameraActivity::class.java)
                backToCam.putExtra(StoryListActivity.USER_TOKEN, token)
                startActivity(backToCam)
                finish()
            }
            .setNegativeButton(R.string.btn_no, null)
            .show()
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val descText = binding.etDescription.text.toString().trim()
            val description = descText.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            if (descText.isEmpty()){
                AlertDialog.Builder(ContextThemeWrapper(this, R.style.CustomAlertDialogStyle))
                    .setTitle("Error")
                    .setMessage(R.string.desc_empty)
                    .setPositiveButton(R.string.btn_ok) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }else {
                viewModel.postStory(token, imageMultipart, description)
            }
        }
    }
}