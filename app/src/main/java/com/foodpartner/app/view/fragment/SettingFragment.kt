package com.foodpartner.app.view.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.ViewDataBinding
import com.app.washeruser.repository.Status
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentLoginBinding
import com.foodpartner.app.databinding.FragmentOtpBinding
import com.foodpartner.app.databinding.FragmentProfilefragmentBinding
import com.foodpartner.app.databinding.FragmentProfilepagefrgamentBinding
import com.foodpartner.app.databinding.FragmentSettingfragmentBinding
import com.foodpartner.app.network.Constant
import com.foodpartner.app.view.activity.SplashActivity
import com.foodpartner.app.view.responsemodel.UserregisterResponseModel
import com.foodpartner.app.viewModel.HomeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Timer
import kotlin.concurrent.schedule

class SettingFragment : BaseFragment<FragmentSettingfragmentBinding>() {
    private val homeViewModel by viewModel<HomeViewModel>()

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        init()
        this.mViewDataBinding.apply {


backBtn.setOnClickListener{
    fragmentManagers!!.popBackStackImmediate()
}
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_settingfragment
    private fun init() {
        val dialog = BottomSheetDialog(requireContext())
        val btmView = layoutInflater.inflate(R.layout.logout, null)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val cancelBtn = btmView.findViewById<AppCompatButton>(R.id.logCancelBtn)
        val confirmBtn = btmView.findViewById<AppCompatButton>(R.id.logOutBtn)
        val header = btmView.findViewById<TextView>(R.id.logTxtView1)
        val disc = btmView.findViewById<AppCompatTextView>(R.id.logTxtView2)

        this.mViewDataBinding.logoutcontainer.setOnClickListener {
            header.text = requireContext().getString(R.string.logout)
            disc.text = requireContext().getString(R.string.logout)

            confirmBtn.setOnClickListener {
                sharedHelper.clearUser()
                sharedHelper.clearCache()
                FirebaseAuth.getInstance().signOut()
                Constant.restaurantcategory=""

                dialog.dismiss()
                setIntent(SplashActivity::class.java,2)
                showToast("logout successfully")

            }

            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }

            dialog.setContentView(btmView)
            dialog.show()
        }


        this.mViewDataBinding.backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }
    }

}