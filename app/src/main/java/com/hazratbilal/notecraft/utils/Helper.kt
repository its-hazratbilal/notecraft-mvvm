package com.hazratbilal.notecraft.utils

import android.text.TextUtils
import android.util.Patterns


class Helper {
    companion object {
        fun isValidEmail(email: String): Boolean {
            return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }

}