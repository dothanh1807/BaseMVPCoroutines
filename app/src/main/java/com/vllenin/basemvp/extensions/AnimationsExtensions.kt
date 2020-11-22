package com.vllenin.basemvp.extensions

import android.animation.Animator
import android.view.View
import androidx.core.content.ContextCompat

/**
 * Created by Vllenin on 8/17/20.
 */
fun View.bouncingAnimation(reduceBouncing: Boolean = false, callbackWhenEnded: () -> Unit = {}) {
    var valueFirst = 0.85f
    var valueSecond = 1.15f
    var valueThird = 0.9f
    val valueForth = 1f
    if (reduceBouncing) {
        valueFirst = 0.94f
        valueSecond = 1.05f
        valueThird = 0.97f
    }
    animate().scaleX(valueFirst).scaleY(valueFirst).setDuration(50)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                animate().scaleX(valueSecond).scaleY(valueSecond).setDuration(50)
                    .setListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {}
                        override fun onAnimationCancel(animation: Animator?) {}
                        override fun onAnimationStart(animation: Animator?) {}

                        override fun onAnimationEnd(animation: Animator?) {
                            animate().scaleX(valueThird).scaleY(valueThird).setDuration(50)
                                .setListener(object : Animator.AnimatorListener {
                                    override fun onAnimationRepeat(animation: Animator?) {}
                                    override fun onAnimationCancel(animation: Animator?) {}
                                    override fun onAnimationStart(animation: Animator?) {}

                                    override fun onAnimationEnd(animation: Animator?) {
                                        animate().scaleX(valueForth).scaleY(valueForth).setDuration(50)
                                            .setListener(object : Animator.AnimatorListener {
                                                override fun onAnimationRepeat(animation: Animator?) {}
                                                override fun onAnimationCancel(animation: Animator?) {}
                                                override fun onAnimationStart(animation: Animator?) {}

                                                override fun onAnimationEnd(animation: Animator?) {
                                                    animation?.cancel()
                                                    post {
                                                        callbackWhenEnded.invoke()
                                                    }
                                                }
                                            })
                                            .start()
                                    }
                                })
                                .start()
                        }
                    })
                    .start()
            }
        })
        .start()
}

fun View.changeBackgroundAnimation(defaultColor: Int, color: Int) {
    setBackgroundColor(ContextCompat.getColor(context, color))
    postDelayed({
        setBackgroundColor(ContextCompat.getColor(context, defaultColor))
    }, 40)
}

fun View.changeStateWhenClicked() {
    isSelected = true
    postDelayed({
        isSelected = false
    }, 40)
}

fun View.enterFormRightScreen() {
    //
}

fun View.exitToRightScreen() {
    //
}