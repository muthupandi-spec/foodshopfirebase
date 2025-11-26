package com.foodpartner.app.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseActivity.BaseActivity
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentSampleBinding

class SampleFragment : BaseFragment<FragmentSampleBinding>(){

    override fun initView(mViewDataBinding: ViewDataBinding?) {

    }
    override fun getLayoutId(): Int =R.layout.fragment_sample



}
