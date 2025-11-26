package com.foodpartner.app.view.fragment

import androidx.databinding.ViewDataBinding
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentLoginBinding
import com.foodpartner.app.databinding.HistoryfragmentBinding
import com.foodpartner.app.view.adapter.viewpageradapter

class HistoryFragment: BaseFragment<HistoryfragmentBinding>(){
    private var viewPagerAdapter: viewpageradapter? = null

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        this.mViewDataBinding.apply {
            viewPagerAdapter = viewpageradapter(childFragmentManager)
            viewPagerAdapter!!.add(CancelOrderFragment(), getString(R.string.cancelbold))
            viewPagerAdapter!!.add(CompletedOrderFragment(), getString(R.string.completebold))

            viewpager.adapter = viewPagerAdapter
            tabs.setupWithViewPager(viewpager)

            // ✅ Keep both fragments loaded
            viewpager.offscreenPageLimit = 1
        }

    }
    override fun getLayoutId(): Int = R.layout.historyfragment



}