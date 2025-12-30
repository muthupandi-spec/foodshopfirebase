package com.foodpartner.app.view.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
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
import com.foodpartner.app.utility.SharedHelper
import com.foodpartner.app.view.fragment.CompletedOrderFragment
import com.foodpartner.app.view.fragment.HistoryFragment
import com.foodpartner.app.view.fragment.Homefragment
import com.foodpartner.app.view.fragment.ProfilepageFragment
import com.foodpartner.app.view.fragment.Shopfragment
import com.foodpartner.app.view.responsemodel.Shop
import com.foodpartner.app.viewModel.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.Observer

class HomeActivity :AppCompatActivity() {
    private var currentFragment: Fragment? = null
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var sharedHelper: SharedHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        sharedHelper = SharedHelper(application)

        bottomNavigation = findViewById(R.id.bottom_navigation)
getShopDetails()
        createNotificationChannel()
        // Default fragment
        switchFragment(Homefragment())

        bottomNavigation?.setOnNavigationItemSelectedListener() {
            when (it.itemId) {
                R.id.navHomeBtn -> switchFragment(Homefragment())
                R.id.shopbtn -> switchFragment(Shopfragment())
                R.id.navhistoryBtn -> switchFragment(CompletedOrderFragment())
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
    private fun getShopDetails() {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        db.collection("shops")
            .document(uid.toString())
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    val shop = Shop(
                        restaurantId = doc.getString("restaurantId") ?: "",
                        restaurantName = doc.getString("restaurantName") ?: "",
                        restaurantLat = doc.getString("restaurantLat") ?: "",
                        restaurantLng = doc.getString("restaurantLng") ?: "",
                        mobileNumber = doc.getString("mobileNumber") ?: "",
                        profileImage = doc.getString("profileImage") ?: "",
                        restaurantLandMark = doc.getString("restaurantLandMark") ?: "",
                        restaurantStreet = doc.getString("restaurantStreet") ?: "",
                        restaurantCity = doc.getString("restaurantCity") ?: "",
                        restaurantPinCode = doc.getString("restaurantPinCode") ?: "",
                        restaurantEmail = doc.getString("restaurantEmail") ?: "",
                        restaurantType = doc.getString("restaurantType") ?: "",
                        tradeId = doc.getString("tradeId") ?: ""
                    )
                    sharedHelper.putInUser("restaurantName", doc.getString("restaurantName") ?: "")
                    sharedHelper.putInUser("profileImage", doc.getString("profileImage") ?: "")
                    sharedHelper.putInUser("mobileNumber", doc.getString("mobileNumber") ?: "")

                    println("dataaa"+shop)
                } else {
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()

            }

    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "order_updates",
                "Order Updates",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Order notifications"

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }



}