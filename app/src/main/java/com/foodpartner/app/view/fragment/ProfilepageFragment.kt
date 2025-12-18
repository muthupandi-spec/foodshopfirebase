package com.foodpartner.app.view.fragment

import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentLoginBinding
import com.foodpartner.app.databinding.FragmentOtpBinding
import com.foodpartner.app.databinding.FragmentProfilepagefrgamentBinding

class ProfilepageFragment : BaseFragment<FragmentProfilepagefrgamentBinding>() {

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        this.mViewDataBinding.apply {
            username.text=sharedHelper.getFromUser("resname")
            phoneno.text=sharedHelper.getFromUser("resmobno")
            profilecontainer.setOnClickListener {
                loadFragment(ShopeditFragment(), android.R.id.content, "homepage", true)
            }
            businesscontainer.setOnClickListener {
                loadFragment(BusinessUpdateFragment(), android.R.id.content, "homepage", true)
            }
            bankcontainer.setOnClickListener {
                loadFragment(BankUpdateFragment(), android.R.id.content, "homepage", true)
            }
            shopcontainer.setOnClickListener {
                loadFragment(ShopeditFragment(), android.R.id.content, "homepage", true)
            }
            accountcontainer.setOnClickListener {
                loadFragment(SettingFragment(), android.R.id.content, "homepage", true)
            }
            Glide.with(activitys).load(sharedHelper.getFromUser("profileImage")).into(profileimg)
username.text=sharedHelper.getFromUser("restaurantName")
phoneno.text=sharedHelper.getFromUser("mobileNumber")


        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_profilepagefrgament


}