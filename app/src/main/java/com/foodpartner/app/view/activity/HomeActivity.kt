package com.foodpartner.app.view.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.app.washeruser.repository.Status
import com.foodpartner.app.R
import com.foodpartner.app.ResponseMOdel.HomeMOdel
import com.foodpartner.app.appControl.AppController
import com.foodpartner.app.baseClass.BaseActivity.BaseActivity
import com.foodpartner.app.databinding.ActivityHomeBinding
import com.foodpartner.app.databinding.ActivityMainBinding
import com.foodpartner.app.network.Response
import com.foodpartner.app.view.fragment.HistoryFragment
import com.foodpartner.app.view.fragment.Homefragment
import com.foodpartner.app.view.fragment.ProfilepageFragment
import com.foodpartner.app.view.fragment.Shopfragment
import com.foodpartner.app.viewModel.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.Observer

class HomeActivity :AppCompatActivity() {
    private var currentFragment: Fragment? = null
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bottomNavigation = findViewById(R.id.bottom_navigation)

        // Default fragment
        switchFragment(Homefragment())

        bottomNavigation?.setOnNavigationItemSelectedListener() {
            when (it.itemId) {
                R.id.navHomeBtn -> switchFragment(Homefragment())
                R.id.shopbtn -> switchFragment(Shopfragment())
                R.id.navhistoryBtn -> switchFragment(HistoryFragment())
                R.id.navProfBtn -> switchFragment(ProfilepageFragment())
                else -> false
            }
        }
    }

    private fun switchFragment(fragment: Fragment): Boolean {
        if (fragment::class == currentFragment?.javaClass) return true

        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)

        if (currentFragment != null) {
            transaction.hide(currentFragment!!)
        }

        if (!fragment.isAdded) {
            transaction.add(R.id.homeContainer, fragment, fragment.javaClass.simpleName)
        } else {
            transaction.show(fragment)
        }

        transaction.commitAllowingStateLoss()
        currentFragment = fragment
        return true
    }
}