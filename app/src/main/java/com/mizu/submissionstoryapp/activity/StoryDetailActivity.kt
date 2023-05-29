package com.mizu.submissionstoryapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mizu.submissionstoryapp.R
import com.mizu.submissionstoryapp.databinding.ActivityStoryDetailBinding
import java.text.SimpleDateFormat
import java.util.*

class StoryDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding : ActivityStoryDetailBinding
    private lateinit var googleMap: GoogleMap

    companion object {
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_PHOTO = "extra_photo"
        const val EXTRA_DESC = "extra_desc"
        const val EXTRA_TIME = "extra_time"
        const val EXTRA_LAT = "extra_lat"
        const val EXTRA_LON = "extra_lon"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fullName = intent.getStringExtra(EXTRA_NAME).toString().trim()
        val name = fullName.split(" ")[0]
        val photoUrl = intent.getStringExtra(EXTRA_PHOTO).toString().trim()
        val description = intent.getStringExtra(EXTRA_DESC).toString().trim()
        val createdAt = intent.getStringExtra(EXTRA_TIME).toString()
        val lat = intent.getDoubleExtra(EXTRA_LAT, 0.0)
        val lon = intent.getDoubleExtra(EXTRA_LON, 0.0)

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

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat(getString(R.string.date_format), Locale.getDefault())
        val date = inputFormat.parse(createdAt)
        val formattedCreatedAt = getString(R.string.date_created) + " " + outputFormat.format(date as Date)

        binding.tvPostTime.text = formattedCreatedAt

        if(lat != 0.0 && lon != 0.0) {
            val mapFragment =
                supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)

        }else {
            binding.noLocationFrame.visibility = View.VISIBLE
            binding.map.visibility = View.GONE
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.apply {
            isScrollGesturesEnabled = true
            isZoomGesturesEnabled = true
            isRotateGesturesEnabled = false
            isTiltGesturesEnabled = false
            isZoomControlsEnabled = true
            isMapToolbarEnabled = false
        }

        val lat = intent.getDoubleExtra(EXTRA_LAT, 0.0)
        val lon = intent.getDoubleExtra(EXTRA_LON, 0.0)
        val marker = LatLng(lat, lon)
        map.addMarker(MarkerOptions().position(marker))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker, 15f))

    }


}