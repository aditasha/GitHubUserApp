package com.dicoding.githubuserappnavigationandapi.ui.follow

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuserappnavigationandapi.adapter.FragmentAdapter
import com.dicoding.githubuserappnavigationandapi.databinding.FragmentFollowBinding
import com.dicoding.githubuserappnavigationandapi.response.DetailResponse
import com.dicoding.githubuserappnavigationandapi.response.UserItem
import com.dicoding.githubuserappnavigationandapi.ui.detail.DetailUserActivity

class FollowFragment : Fragment() {
    private lateinit var binding: FragmentFollowBinding

    private val fragmentLayoutManager = LinearLayoutManager(activity)
    private val fragmentAdapter by lazy { FragmentAdapter() }
    private var countdown: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemDecoration = DividerItemDecoration(activity, fragmentLayoutManager.orientation)

        binding.rvUserFragment.apply {
            adapter = fragmentAdapter
            layoutManager = fragmentLayoutManager
            setHasFixedSize(true)
            addItemDecoration(itemDecoration)
        }

        val index = arguments?.getInt(ARG_SECTION_NUMBER, 0)
        val argUser = arguments?.getParcelable<DetailResponse>(ARG_USER)

        if (argUser != null && index != null) {
            populateFollow(argUser, index)
        }
    }

    private fun populateFollow(detailUser: DetailResponse, index: Int) {
        val followViewModel: FollowViewModel by viewModels { FollowViewModelFactory(detailUser.login) }
        followViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        if (index == 0) {
            followViewModel.userFollowers.observe(viewLifecycleOwner) { followers ->
                if (followers != null) {
                    if (followers.isEmpty()) {
                        val text = "User \"${detailUser.login}\" didn't have any follower"
                        binding.error.visibility = View.VISIBLE
                        binding.error.text = text
                    } else {
                        binding.error.visibility = View.GONE
                        displayFollow(followers)
                    }
                }
            }
        } else {
            followViewModel.userFollowing.observe(viewLifecycleOwner) { following ->
                if (following != null) {
                    if (following.isEmpty()) {
                        val text = "User \"${detailUser.login}\" didn't have any following"
                        binding.error.visibility = View.VISIBLE
                        binding.error.text = text
                    } else {
                        binding.error.visibility = View.GONE
                        displayFollow(following)
                    }
                }
            }
        }
    }

    private fun displayFollow(user: ArrayList<UserItem>) {
        fragmentAdapter.addFollow(user)

        fragmentAdapter.setOnItemClickCallback(object : FragmentAdapter.OnItemClickCallback {
            override fun onItemClicked(user: UserItem) {
                val intent = Intent(activity, DetailUserActivity::class.java)
                intent.putExtra(DetailUserActivity.EXTRA_USER, user)
                intent.putExtra(DetailUserActivity.TAG, "Follow")
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

    private fun startTimer(start: Boolean) {
        countdown?.cancel()
        if (start) {
            countdown = object : CountDownTimer(20000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    if (millisUntilFinished < 10000) {
                        binding.error.visibility = View.VISIBLE
                        val text =
                            "Long loading time,\nwaiting for " + millisUntilFinished / 1000 + " second"
                        binding.error.text = text
                    }
                }

                override fun onFinish() {
                    val text = "Data cannot be loaded,\n check your internet connection"
                    binding.error.text = text
                    cancel()
                }
            }.start()
        } else {
            countdown?.cancel()
        }
    }

    companion object {
        const val ARG_SECTION_NUMBER = "section_number"
        const val ARG_USER = "user"
    }
}