package com.dicoding.githubuserappnavigationandapi.adapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dicoding.githubuserappnavigationandapi.response.DetailResponse
import com.dicoding.githubuserappnavigationandapi.ui.follow.FollowFragment

class SectionsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    var user: DetailResponse? = null

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = FollowFragment()
        fragment.arguments = Bundle().apply {
            putInt(FollowFragment.ARG_SECTION_NUMBER, position)
            putParcelable(FollowFragment.ARG_USER, user)
        }
        return fragment
    }
}
