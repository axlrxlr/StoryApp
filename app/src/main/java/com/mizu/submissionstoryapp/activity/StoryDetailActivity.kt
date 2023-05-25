package com.mizu.submissionstoryapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.mizu.submissionstoryapp.R
import com.mizu.submissionstoryapp.databinding.ActivityStoryDetailBinding

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding : ActivityStoryDetailBinding

    companion object {
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_PHOTO = "extra_photo"
        const val EXTRA_DESC = "extra_desc"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fullName = intent.getStringExtra(EXTRA_NAME).toString().trim()
        val name = fullName.split(" ")[0]
        val photoUrl = intent.getStringExtra(EXTRA_PHOTO).toString().trim()
        val description = intent.getStringExtra(EXTRA_DESC).toString().trim()

        val titleColor = ContextCompat.getColor(this, R.color.black)
        val title = SpannableString("$name's Post")
        title.setSpan(ForegroundColorSpan(titleColor), 0, title.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        supportActionBar?.title = title

        Glide.with(this@StoryDetailActivity)
            .load(photoUrl)
            .centerCrop()
            .format(DecodeFormat.PREFER_RGB_565)
            .into(binding.ivPhotoDetail)

        binding.tvPostName.text = fullName
        binding.tvPostDescription.text = description
    }


}