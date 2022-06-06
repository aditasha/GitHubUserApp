package com.dicoding.githubuserappnavigationandapi.ui.detail

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.dicoding.githubuserappnavigationandapi.GlideApp
import com.dicoding.githubuserappnavigationandapi.R
import com.dicoding.githubuserappnavigationandapi.adapter.SectionsPagerAdapter
import com.dicoding.githubuserappnavigationandapi.databinding.ActivityDetailUserBinding
import com.dicoding.githubuserappnavigationandapi.response.UserItem
import com.dicoding.githubuserappnavigationandapi.themeswitcher.SettingPreferences
import com.dicoding.githubuserappnavigationandapi.ui.favorite.FavoriteViewModel
import com.dicoding.githubuserappnavigationandapi.ui.favorite.FavoriteViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator
import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DetailUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailUserBinding

    private val sectionsPagerAdapter by lazy { SectionsPagerAdapter(this@DetailUserActivity) }
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var query: String
    private var countdown: CountDownTimer? = null

    private lateinit var factory: FavoriteViewModelFactory
    private val favoriteViewModel: FavoriteViewModel by viewModels { factory }

    private lateinit var pref: SettingPreferences
    private var isDark = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val from = intent.getStringExtra(TAG)
        if (from == "Follow") {
            setTheme(R.style.AnimStyleVertical)
        }
        super.onCreate(savedInstanceState)

        factory = FavoriteViewModelFactory.getInstance(this@DetailUserActivity)

        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getParcelableExtra<UserItem>(EXTRA_USER)

        data?.login?.let {
            query = it
            getDetail(it)
        }

        var fav = false
        if (data != null) {
            favoriteViewModel.getUser(data.login)
            favoriteViewModel.userFound.observe(this@DetailUserActivity) { found ->
                fav = found
                if (found) binding.fabFav.setImageResource(R.drawable.ic_favorite)
                else binding.fabFav.setImageResource(R.drawable.ic_favorite_border)
            }
        }

        binding.fabFav.setOnClickListener {
            if (data != null) {
                if (!fav) {
                    favoriteViewModel.addFavorite(data)
                    Toast.makeText(
                        this@DetailUserActivity,
                        "${data.login} has been added to favorite user",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.fabFav.setImageResource(R.drawable.ic_favorite)
                } else {
                    favoriteViewModel.deleteFavorite(data.login)
                    Toast.makeText(
                        this@DetailUserActivity,
                        "${data.login} has been removed from favorite user",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.fabFav.setImageResource(R.drawable.ic_favorite_border)
                }
                fav = !fav
            }
        }
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
                detailViewModel.saveTheme(pref, !isDark)
                if (isDark) {
                    Toast.makeText(
                        this@DetailUserActivity,
                        "Changed to light theme",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@DetailUserActivity,
                        "Changed to night theme",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getDetail(username: String) {
        val detailViewModel: DetailViewModel by viewModels { DetailViewModelFactory(username) }
        this.detailViewModel = detailViewModel
        getTheme(detailViewModel)

        detailViewModel.isLoading.observe(this@DetailUserActivity) {
            showLoading(it)
        }

        detailViewModel.isFailed.observe(this@DetailUserActivity) {
            showFailed(it, detailViewModel.errorText.value.toString())
        }

        detailViewModel.detailUser.observe(this@DetailUserActivity) { detailUser ->

            val color = ContextCompat.getColor(this, R.color.github_orange)

            val circularProgressDrawable = CircularProgressDrawable(this)
            circularProgressDrawable.setColorSchemeColors(color)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 15f
            circularProgressDrawable.start()

            GlideApp
                .with(this)
                .load(detailUser?.avatarUrl)
                .placeholder(circularProgressDrawable)
                .transform(CropCircleWithBorderTransformation(7, color))
                .into(binding.detailProfileImage)

            binding.apply {
                if (detailUser?.name != null) {
                    detailUsersName.text = detailUser.name
                } else {
                    detailUsersName.visibility = View.GONE
                }

                val stringUsername = "@${detailUser?.login}"
                val title = "${detailUser?.login}\'s Detail"
                setTitle(title)
                detailUsername.text = stringUsername

                val textFollowers = "Followers\n${detailUser?.followers}"
                val textFollowing = "Following\n${detailUser?.following}"
                followers.text = textFollowers
                following.text = textFollowing

                if (detailUser?.location != null) {
                    location.text = detailUser.location
                } else {
                    pin.visibility = View.GONE
                    location.visibility = View.GONE
                }

                if (detailUser?.company != null) {
                    company.text = detailUser.company
                } else {
                    building.visibility = View.GONE
                    company.visibility = View.GONE
                }

                nRepo.text = detailUser?.publicRepos.toString()

                detailUser?.let {
                    sectionsPagerAdapter.user = it
                    binding.viewPager.adapter = sectionsPagerAdapter
                }

                TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
                    tab.text = resources.getString(TAB_TITLES[position])
                }.attach()
            }
        }
    }

    private fun getTheme(viewModel: DetailViewModel) {
        pref = SettingPreferences.getInstance(dataStore)
        viewModel.getTheme(pref).observe(this@DetailUserActivity) { darkMode ->
            if (darkMode) {
                isDark = true
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                isDark = false
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.apply {
                progressBar.visibility = View.VISIBLE
                detailGroup.visibility = View.GONE
            }
            startTimer(true)

        } else {
            binding.apply {
                progressBar.visibility = View.GONE
                detailGroup.visibility = View.VISIBLE
            }
            startTimer(false)
        }
    }

    private fun showFailed(isFailed: Boolean, e: String) {
        if (isFailed) {
            binding.apply {
                detailGroup.visibility = View.GONE
                error.text = e
                setListenerRetry()
                error.visibility = View.VISIBLE
                retry.visibility = View.VISIBLE
            }
        } else {
            binding.apply {
                error.visibility = View.GONE
                detailGroup.visibility = View.VISIBLE
            }
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
                    setListenerRetry()
                    cancel()
                }
            }.start()
        } else {
            countdown?.cancel()
        }
    }

    private fun setListenerRetry() {
        binding.retry.setOnClickListener {
            detailViewModel.detailUser(query)
            binding.error.visibility = View.GONE
            binding.retry.visibility = View.GONE
        }
    }

    companion object {
        const val EXTRA_USER = "extra_user"
        const val TAG = "from"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.fragment_followers,
            R.string.fragment_following
        )
    }
}