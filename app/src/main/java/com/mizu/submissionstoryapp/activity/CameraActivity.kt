package com.mizu.submissionstoryapp.activity

import android.app.AlertDialog
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.ContextThemeWrapper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.mizu.submissionstoryapp.R
import com.mizu.submissionstoryapp.createFile
import com.mizu.submissionstoryapp.databinding.ActivityCameraBinding
import com.mizu.submissionstoryapp.uriToFile

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private lateinit var token: String
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startCamera()

        token = intent.getStringExtra(StoryListActivity.USER_TOKEN).toString()

        val titleColor = ContextCompat.getColor(this, R.color.black)
        val title = SpannableString(getString(R.string.take_photo))
        title.setSpan(ForegroundColorSpan(titleColor), 0, title.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        supportActionBar?.title = title

        binding.btnSwitchCamera.setOnClickListener {
            cameraSelector =
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA

            startCamera()
        }

        binding.btnCapture.setOnClickListener{
            takePhoto()
        }

        binding.btnGallery.setOnClickListener{
            startGallery()
        }
    }



    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: java.lang.Exception){
                Toast.makeText(
                    this@CameraActivity,
                    R.string.camera_failed,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto(){
        val imageCapture = imageCapture?: return

        val photoFile = createFile(application)

        val captureButton = binding.btnCapture
        val captureEffectAnimation = AnimationUtils.loadAnimation(this, R.anim.capture_effect)
        captureButton.startAnimation(captureEffectAnimation)

        val flashOverlay = binding.flashOverlay
        val flashAnimation = AnimationUtils.loadAnimation(this, R.anim.flash_animation)
        flashAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                flashOverlay.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation?) {
                flashOverlay.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {
                // Not needed, but required for AnimationListener
            }
        })
        flashOverlay.startAnimation(flashAnimation)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object: ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(
                        this@CameraActivity,
                        R.string.capture_success,
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this@CameraActivity, AddStoryActivity::class.java)
                    intent.putExtra("picture", photoFile)
                    intent.putExtra(
                        "isBackCamera",
                        cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                    )
                    intent.putExtra(StoryListActivity.USER_TOKEN, token)
                    setResult(AddStoryActivity.CAMERA_X_RESULT, intent)
                    startActivity(intent)
                    finish()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        R.string.capture_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.CustomAlertDialogStyle))
            .setTitle(R.string.cancel_story)
            .setMessage(R.string.cancel_story_desc)
            .setPositiveButton(R.string.btn_yes) { _, _ ->
                val backToList = Intent(this@CameraActivity, StoryListActivity::class.java)
                backToList.putExtra(StoryListActivity.USER_TOKEN, token)
                startActivity(backToList)
                finish()
            }
            .setNegativeButton(R.string.btn_no, null)
            .show()
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }



    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri


            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@CameraActivity)
                val previewIntent = Intent(this@CameraActivity, AddStoryActivity::class.java)
                previewIntent.putExtra("picture", myFile)
                previewIntent.putExtra(StoryListActivity.USER_TOKEN, token)
                previewIntent.putExtra("isImported", true)
                startActivity(previewIntent)
                finish()
            }
        }
    }
}

