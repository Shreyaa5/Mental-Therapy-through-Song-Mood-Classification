package com.example.myexample.utilis

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.animation.AnimationUtils
import com.example.myexample.R

class VibrateView1 {
    companion object {
        fun vibrate(context: Context, view: View) {
            val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        100, VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            }
            val animation = AnimationUtils.loadAnimation(context, R.anim.vibrate)
            view.startAnimation(animation)
        }
    }
}
