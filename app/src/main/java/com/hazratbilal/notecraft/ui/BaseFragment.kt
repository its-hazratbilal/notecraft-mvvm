package com.hazratbilal.notecraft.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hazratbilal.notecraft.R
import com.hazratbilal.notecraft.databinding.LoadingBinding


abstract class BaseFragment : Fragment() {

    private var loading: Dialog? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initLoading(context)
    }

    fun initLoading(context: Context) {
        try {
            val binding = LoadingBinding.inflate(LayoutInflater.from(requireContext()))
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
            dialog.setContentView(binding.root)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            Glide
                .with(context)
                .load(R.drawable.logo)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_error)
                .into(binding.logoImage)

            loading = dialog
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun showLoading() {
        try {
            if (loading != null && !loading!!.isShowing) {
                loading!!.show()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun hideLoading() {
        try {
            if (loading != null && loading!!.isShowing) {
                loading!!.dismiss()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}