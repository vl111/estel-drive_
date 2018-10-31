package com.esteldrive.esteldrive.behavior

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.util.AttributeSet
import android.view.View
import android.support.v4.view.ViewCompat
import com.github.clans.fab.FloatingActionMenu


class ShrinkBehavior(context: Context, attrs: AttributeSet) : CoordinatorLayout.Behavior<FloatingActionMenu>(context, attrs) {
    override fun layoutDependsOn(parent: CoordinatorLayout?, child: FloatingActionMenu?, dependency: View?): Boolean {
        return dependency is Snackbar.SnackbarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout?, child: FloatingActionMenu?, dependency: View?): Boolean {
        val translationY = getFabTranslationYForSnackbar(parent!!, child!!)
        val percentComplete = -translationY / dependency!!.getHeight()
        val scaleFactor = 1 - percentComplete

        child!!.setScaleX(scaleFactor)
        child!!.setScaleY(scaleFactor)
        return false
    }

    private fun getFabTranslationYForSnackbar(parent: CoordinatorLayout,
                                              fab: FloatingActionMenu): Float {
        var minOffset = 0f
        val dependencies = parent.getDependencies(fab)
        var i = 0
        val z = dependencies.size
        while (i < z) {
            val view = dependencies[i]
            if (view is Snackbar.SnackbarLayout && parent.doViewsOverlap(fab, view)) {
                minOffset = Math.min(minOffset,
                        ViewCompat.getTranslationY(view) - view.getHeight())
            }
            i++
        }

        return minOffset
    }
}