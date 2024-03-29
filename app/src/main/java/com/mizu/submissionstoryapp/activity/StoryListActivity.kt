package com.mizu.submissionstoryapp.activity

import android.Manifest.permission
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.ContextThemeWrapper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.mizu.submissionstoryapp.MainActivity
import com.mizu.submissionstoryapp.R
import com.mizu.submissionstoryapp.activity.viewmodel.LoginViewModel
import com.mizu.submissionstoryapp.activity.viewmodel.StoryListViewModel
import com.mizu.submissionstoryapp.activity.viewmodel.ViewModelFactory
import com.mizu.submissionstoryapp.activity.viewmodel.ViewModelFactoryPaging
import com.mizu.submissionstoryapp.adapter.LoadingStateAdapter
import com.mizu.submissionstoryapp.adapter.StoryListAdapter
import com.mizu.submissionstoryapp.api.ListStoryItem
import com.mizu.submissionstoryapp.databinding.ActivityStoryListBinding
import com.mizu.submissionstoryapp.datastore.LoginPreferences

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login")

@Suppress("DEPRECATION")
@OptIn(ExperimentalPagingApi::class)
class StoryListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryListBinding
    private lateinit var token: String
    private val storyListViewModel: StoryListViewModel by viewModels {
        ViewModelFactoryPaging(this)
    }


    companion object {
        const val USER_TOKEN = "user_token"
        private val REQUIRED_PERMISSIONS = arrayOf(permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.CustomAlertDialogStyle))
            .setTitle(R.string.exit_app)
            .setMessage(R.string.exit_validation)
            .setPositiveButton(R.string.btn_ok) { _, _ ->
                finish()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_story_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_reload -> {
                setStoryList(token)
                return true
            }
            R.id.action_map -> {
                val maps = Intent(this@StoryListActivity, MapsActivity::class.java)
                maps.putExtra(USER_TOKEN, token)
                startActivity(maps)
                return true
            }
            R.id.action_logout -> {

                val pref = LoginPreferences.getInstance(dataStore)
                val loginViewModel =
                    ViewModelProvider(this, ViewModelFactory(pref))[LoginViewModel::class.java]

                AlertDialog.Builder(ContextThemeWrapper(this, R.style.CustomAlertDialogStyle))
                    .setTitle(R.string.log_out)
                    .setMessage(R.string.log_out_validation)
                    .setPositiveButton(R.string.btn_yes) { _, _ ->
                        loginViewModel.removeSessionToken()
                        val intent = Intent(this@StoryListActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .setNegativeButton(R.string.btn_no, null)
                    .show()

                return true

            }

            R.id.action_localization -> {
                AlertDialog.Builder(ContextThemeWrapper(this, R.style.CustomAlertDialogStyle))
                    .setTitle(R.string.device_setting)
                    .setMessage(R.string.device_setting_validation)
                    .setPositiveButton(R.string.btn_continue) { _, _ ->
                        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    }
                    .setNegativeButton(R.string.btn_cancel, null)
                    .show()

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        token = intent.getStringExtra(USER_TOKEN).toString()

        val titleColor = ContextCompat.getColor(this, R.color.black)
        val title = SpannableString(getString(R.string.app_name))
        title.setSpan(
            ForegroundColorSpan(titleColor),
            0,
            title.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        supportActionBar?.title = title

        val pref = LoginPreferences.getInstance(dataStore)
        val loginViewModel = ViewModelProvider(this@StoryListActivity, ViewModelFactory(pref))[LoginViewModel::class.java]

        loginViewModel.getSessionToken().observe(this) {
            setStoryList(it)
        }

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnAddStory.setOnClickListener {
            val addStory = Intent(this@StoryListActivity, CameraActivity::class.java)
            addStory.putExtra(USER_TOKEN, token)
            startActivity(addStory)
            finish()
        }


    }

    @ExperimentalPagingApi
    private fun setStoryList(token: String) {
        binding.apply {
            val listStoryAdapter = StoryListAdapter()
            rvStoryList.layoutManager = LinearLayoutManager(this@StoryListActivity)
            rvStoryList.adapter = listStoryAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    listStoryAdapter.retry()
                }
            )

            storyListViewModel.getAllStory(token).observe(this@StoryListActivity) {
                listStoryAdapter.submitData(lifecycle, it)
            }

            listStoryAdapter.setOnItemClickCallback(object : StoryListAdapter.OnItemClickCallback {
                override fun onItemClicked(data: ListStoryItem) {
                    val moveToDetail =
                        Intent(this@StoryListActivity, StoryDetailActivity::class.java)
                    moveToDetail.putExtra(StoryDetailActivity.EXTRA_NAME, data.name)
                    moveToDetail.putExtra(StoryDetailActivity.EXTRA_PHOTO, data.photoUrl)
                    moveToDetail.putExtra(StoryDetailActivity.EXTRA_DESC, data.description)
                    moveToDetail.putExtra(StoryDetailActivity.EXTRA_TIME, data.createdAt)
                    moveToDetail.putExtra(StoryDetailActivity.EXTRA_LAT, data.lat)
                    moveToDetail.putExtra(StoryDetailActivity.EXTRA_LON, data.lon)
                    startActivity(moveToDetail)
                }
            })
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    R.string.no_permit,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

}