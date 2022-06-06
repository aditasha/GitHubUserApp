package com.dicoding.githubuserappnavigationandapi.ui.favorite

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuserappnavigationandapi.R
import com.dicoding.githubuserappnavigationandapi.adapter.FavoriteActivityAdapter
import com.dicoding.githubuserappnavigationandapi.databinding.ActivityFavoriteUserBinding
import com.dicoding.githubuserappnavigationandapi.response.UserItem
import com.dicoding.githubuserappnavigationandapi.themeswitcher.SettingPreferences
import com.dicoding.githubuserappnavigationandapi.ui.detail.DetailUserActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteUserBinding

    private val favoriteAdapter by lazy { FavoriteActivityAdapter() }
    private lateinit var factory: FavoriteViewModelFactory
    private val viewModel: FavoriteViewModel by viewModels { factory }

    private lateinit var pref: SettingPreferences
    private var isDark = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = FavoriteViewModelFactory.getInstance(this@FavoriteActivity)

        val mainLayoutManager = LinearLayoutManager(this@FavoriteActivity)
        val itemDecoration = DividerItemDecoration(this@FavoriteActivity, mainLayoutManager.orientation)

        binding.rvUser.apply {
            adapter = favoriteAdapter
            setHasFixedSize(true)
            layoutManager = mainLayoutManager
            addItemDecoration(itemDecoration)
        }

        pref = SettingPreferences.getInstance(dataStore)
        viewModel.getTheme(pref).observe(this@FavoriteActivity) { darkMode ->
            if (darkMode) {
                isDark = true
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                isDark = false
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        populateView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.theme, menu)
        if (isDark) {
            menu.getItem(0).setIcon(R.drawable.ic_day_mode)
        } else {
            menu.getItem(0).setIcon(R.drawable.ic_night_mode)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_theme -> {
                viewModel.saveTheme(pref, !isDark)
                if (isDark) {
                    Toast.makeText(
                        this@FavoriteActivity,
                        "Changed to light theme",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@FavoriteActivity,
                        "Changed to night theme",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun populateView() {
        viewModel.favorite.observe(this@FavoriteActivity) { favorite ->
            if (favorite.isEmpty()) {
                val text = "You haven't add any user to your favorite list"
                binding.error.text = text
                binding.error.visibility = View.VISIBLE
            } else {
                favoriteAdapter.addData(favorite)
                binding.error.visibility = View.GONE
            }
        }

        favoriteAdapter.setOnItemClickCallback(object: FavoriteActivityAdapter.OnItemClickCallback {
            override fun onItemClicked(user: UserItem) {
                val intent = Intent(this@FavoriteActivity, DetailUserActivity::class.java)
                intent.putExtra(DetailUserActivity.EXTRA_USER, user)
                intent.putExtra(DetailUserActivity.TAG, "Favorite")
                startActivity(intent)
            }
        })
    }
}