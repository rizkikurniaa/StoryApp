package com.kikulabs.storyapp.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kikulabs.storyapp.R
import com.kikulabs.storyapp.adapter.ListStoriesAdapter
import com.kikulabs.storyapp.data.model.UserPreference
import com.kikulabs.storyapp.databinding.ActivityMainBinding
import com.kikulabs.storyapp.ui.login.LoginActivity
import com.kikulabs.storyapp.ui.story.AddStoryActivity
import com.kikulabs.storyapp.utils.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var listStoriesAdapter: ListStoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listStoriesAdapter = ListStoriesAdapter()

        initViewModel()
        setRecyclerView()
        initListener()
    }

    private fun initViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                mainViewModel.getAllStories(user.token)
                mainViewModel.listStories.observe(this) {
                    if (it != null) {
                        listStoriesAdapter.setStory(it)
                    }
                }
                mainViewModel.isLoading.observe(this) { showLoading(it) }
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun setRecyclerView() {
        binding.apply {
            rvStories.layoutManager = LinearLayoutManager(this@MainActivity)
            rvStories.setHasFixedSize(true)
            rvStories.adapter = listStoriesAdapter
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.menu_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.menu_logout -> {
                mainViewModel.logout()
                true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun initListener() {
        binding.fabAddStory.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab_add_story -> {
                val intent = Intent(this, AddStoryActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }
}