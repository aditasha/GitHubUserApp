package com.dicoding.githubuserappnavigationandapi.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.getSystemService
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuserappnavigationandapi.R
import com.dicoding.githubuserappnavigationandapi.adapter.MainActivityAdapter
import com.dicoding.githubuserappnavigationandapi.databinding.ActivityMainBinding
import com.dicoding.githubuserappnavigationandapi.response.UserItem
import com.dicoding.githubuserappnavigationandapi.themeswitcher.SettingPreferences
import com.dicoding.githubuserappnavigationandapi.ui.detail.DetailUserActivity
import com.dicoding.githubuserappnavigationandapi.ui.favorite.FavoriteActivity
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel>()
    private val mainAdapter by lazy { MainActivityAdapter() }

    private lateinit var search: String
    private var countdown: CountDownTimer? = null
    private lateinit var pref: SettingPreferences
    private var isDark = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = SettingPreferences.getInstance(dataStore)
        mainViewModel.getTheme(pref).observe(this@MainActivity) { darkMode ->
            if (darkMode) {
                isDark = true
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                isDark = false
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        val mainLayoutManager = LinearLayoutManager(this@MainActivity)
        val itemDecoration = DividerItemDecoration(this@MainActivity, mainLayoutManager.orientation)

        binding.rvUser.apply {
            adapter = mainAdapter
            setHasFixedSize(true)
            layoutManager = mainLayoutManager
            addItemDecoration(itemDecoration)
        }

        setListener()
        populateView()

        mainViewModel.isLoading.observe(this@MainActivity) {
            showLoading(it)
        }

        mainViewModel.isFailed.observe(this@MainActivity) {
            showFailed(it, mainViewModel.errorText.value.toString())
        }

        binding.search.setOnFocusChangeListener { view, b ->
            if (!b) {
                currentFocus?.clearFocus()
                view.clearFocus()
                val imm = getSystemService<InputMethodManager>()
                imm?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        if (isDark) {
            menu.getItem(1).setIcon(R.drawable.ic_day_mode)
        } else {
            menu.getItem(1).setIcon(R.drawable.ic_night_mode)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_favorite -> {
                val intent = Intent(this@MainActivity, FavoriteActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_theme -> {
                mainViewModel.saveTheme(pref, !isDark)
                if (isDark) {
                    Toast.makeText(
                        this@MainActivity,
                        "Changed to light theme",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Changed to night theme",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setListener() {
        val queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    binding.error.visibility = View.GONE
                    search = query
                    mainViewModel.getUser(search)
                    mainAdapter.clearData()
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null) {
                    search = query
                }
                return true
            }
        }

        binding.search.setOnQueryTextListener(queryTextListener)
    }

    private fun populateView() {
        mainViewModel.listUser.observe(this@MainActivity) { userList ->
            mainAdapter.addData(userList)
            setOnClickData()
        }

        mainViewModel.searchUser.observe(this@MainActivity) { userList ->
            Log.d("test", "story list observe called")
            if (userList != null) {
                if (userList.isEmpty()) {
                    val text = "Username \"$search\" cannot be found"
                    binding.error.visibility = View.VISIBLE
                    binding.error.text = text
                } else {
                    mainAdapter.addData(userList)
                    setOnClickData()
                }
            }
        }
    }

    private fun setOnClickData() {
        mainAdapter.setOnItemClickCallback(object : MainActivityAdapter.OnItemClickCallback {
            override fun onItemClicked(user: UserItem) {
                val intent = Intent(this@MainActivity, DetailUserActivity::class.java)
                intent.putExtra(DetailUserActivity.EXTRA_USER, user)
                intent.putExtra(DetailUserActivity.TAG, "main")
                startActivity(intent)
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            startTimer(true)
        } else {
            binding.progressBar.visibility = View.GONE
            startTimer(false)
        }
    }

    private fun showFailed(isFailed: Boolean, e: String) {
        if (isFailed) {
            binding.apply {
                error.text = e
                val query = mainViewModel.lastTry.value.toString()
                setListenerRetry(query)
                error.visibility = View.VISIBLE
                retry.visibility = View.VISIBLE
            }
        } else {
            binding.error.visibility = View.GONE
        }
    }

    private fun startTimer(start: Boolean) {
        countdown?.cancel()
        if (start) {
            countdown = object : CountDownTimer(19000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    if (millisUntilFinished < 10000) {
                        binding.error.visibility = View.VISIBLE
                        val text =
                            "Long loading time,\nwaiting for " + millisUntilFinished / 1000 + " second"
                        binding.error.text = text
                    }
                }

                override fun onFinish() {
                    binding.error.visibility = View.VISIBLE
                    val text = "Data cannot be loaded,\ntap to retry"
                    binding.error.text = text
                    binding.retry.visibility = View.VISIBLE
                    val query = mainViewModel.lastTry.value.toString()
                    setListenerRetry(query)
                    cancel()
                }
            }.start()
        } else {
            countdown?.cancel()
        }
    }

    private fun setListenerRetry(query: String) {
        binding.retry.setOnClickListener {
            when (query) {
                "load_user" -> {
                    lifecycleScope.launch { mainViewModel.loadUser() }
                    binding.error.visibility = View.GONE
                    binding.retry.visibility = View.GONE
                }
                "get_user" -> {
                    mainViewModel.getUser(search)
                    binding.error.visibility = View.GONE
                    binding.retry.visibility = View.GONE
                }
            }
        }
    }
}