package com.astrology.app.baseClass

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.Nullable
import androidx.fragment.app.FragmentManager
import com.foodpartner.app.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.disposables.CompositeDisposable

abstract class BaseBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var disposable: CompositeDisposable
    lateinit var activity: Activity
    var fragmentManagers: FragmentManager? = null
    lateinit var views: View

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        disposable = CompositeDisposable()
        activity = getActivity()!!
        fragmentManagers = fragmentManager!!
    }
    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (view!!.parent as View).setBackgroundColor(Color.TRANSPARENT)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        if (disposable.isDisposed) {
            disposable.clear()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    protected fun intent(intent: Intent, ins:Int){
        startActivity(intent)
        when(ins){
            1->{
                activity.finish()
            }
            2->{
                activity.finishAffinity()
            }
        }

    }
}
