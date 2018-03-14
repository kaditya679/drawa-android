package com.tomclaw.drawa.draw

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import com.jakewharton.rxrelay2.PublishRelay
import com.tomclaw.drawa.R
import com.tomclaw.drawa.draw.tools.SIZE_L
import com.tomclaw.drawa.draw.tools.SIZE_M
import com.tomclaw.drawa.draw.tools.SIZE_S
import com.tomclaw.drawa.draw.tools.SIZE_XL
import com.tomclaw.drawa.draw.tools.SIZE_XXL
import com.tomclaw.drawa.draw.tools.TYPE_BRUSH
import com.tomclaw.drawa.draw.tools.TYPE_ERASER
import com.tomclaw.drawa.draw.tools.TYPE_FILL
import com.tomclaw.drawa.draw.tools.TYPE_FLUFFY
import com.tomclaw.drawa.draw.tools.TYPE_MARKER
import com.tomclaw.drawa.draw.tools.TYPE_PENCIL
import com.tomclaw.drawa.draw.view.PaletteView
import com.tomclaw.drawa.util.hide
import com.tomclaw.drawa.util.isVisible
import com.tomclaw.drawa.util.show
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

interface ToolsView {

    fun showToolChooser(animate: Boolean = true)

    fun showColorChooser(animate: Boolean = true)

    fun showSizeChooser(animate: Boolean = true)

    fun setToolSelected(toolType: Int)

    fun setColorSelected(color: Int)

    fun setSizeSelected(size: Int)

    fun hideChooser(animate: Boolean = true)

    fun tuneClicks(): Observable<Int>

    fun hideChooserClicks(): Observable<Unit>

    fun toolSelected(): Observable<Int>

    fun colorSelected(): Observable<Int>

    fun sizeSelected(): Observable<Int>

}

class ToolsViewImpl(view: View) : ToolsView {

    private val tuneTool: ImageView = view.findViewById(R.id.tune_tool)
    private val tuneColor: ImageView = view.findViewById(R.id.tune_color)
    private val tuneSize: ImageView = view.findViewById(R.id.tune_size)
    private val toolsBackground: View = view.findViewById(R.id.tools_background)
    private val toolsContainer: View = view.findViewById(R.id.tools_container)
    private val toolsWrapper: View = view.findViewById(R.id.tools_wrapper)
    private val toolChooser: View = view.findViewById(R.id.tool_chooser)
    private val colorChooser: View = view.findViewById(R.id.color_chooser)
    private val sizeChooser: View = view.findViewById(R.id.size_chooser)

    private val selectChooserRelay = PublishRelay.create<Int>()
    private val hideChooserRelay = PublishRelay.create<Unit>()
    private val toolRelay = PublishRelay.create<Int>()
    private val colorRelay = PublishRelay.create<Int>()
    private val sizeRelay = PublishRelay.create<Int>()

    init {
        tuneTool.setOnClickListener { selectChooserRelay.accept(ID_TOOL_CHOOSER) }
        tuneColor.setOnClickListener { selectChooserRelay.accept(ID_COLOR_CHOOSER) }
        tuneSize.setOnClickListener { selectChooserRelay.accept(ID_SIZE_CHOOSER) }
        toolsBackground.setOnClickListener { hideChooserRelay.accept(Unit) }

        view.setOnClickListener(R.id.tool_pencil, { toolRelay.accept(TYPE_PENCIL) })
        view.setOnClickListener(R.id.tool_brush, { toolRelay.accept(TYPE_BRUSH) })
        view.setOnClickListener(R.id.tool_marker, { toolRelay.accept(TYPE_MARKER) })
        view.setOnClickListener(R.id.tool_fluffy, { toolRelay.accept(TYPE_FLUFFY) })
        view.setOnClickListener(R.id.tool_fill, { toolRelay.accept(TYPE_FILL) })
        view.setOnClickListener(R.id.tool_eraser, { toolRelay.accept(TYPE_ERASER) })
        view.findViewById<PaletteView>(R.id.palette_view).colorClickListener =
                object : PaletteView.OnColorClickListener {
                    override fun onColorClicked(color: Int) {
                        colorRelay.accept(color)
                    }
                }
        view.setOnClickListener(R.id.size_s, { sizeRelay.accept(SIZE_S) })
        view.setOnClickListener(R.id.size_m, { sizeRelay.accept(SIZE_M) })
        view.setOnClickListener(R.id.size_l, { sizeRelay.accept(SIZE_L) })
        view.setOnClickListener(R.id.size_xl, { sizeRelay.accept(SIZE_XL) })
        view.setOnClickListener(R.id.size_xxl, { sizeRelay.accept(SIZE_XXL) })
    }

    override fun showToolChooser(animate: Boolean) {
        if (toolsContainer.isVisible()) {
            switchChooser(toolChooser, animate)
        } else {
            toolChooser.show()
            colorChooser.hide()
            sizeChooser.hide()
            showTools(chooser = toolChooser, animate = animate)
        }
    }

    override fun showColorChooser(animate: Boolean) {
        if (toolsContainer.isVisible()) {
            switchChooser(colorChooser, animate)
        } else {
            toolChooser.hide()
            colorChooser.show()
            sizeChooser.hide()
            showTools(chooser = colorChooser, animate = animate)
        }
    }

    override fun showSizeChooser(animate: Boolean) {
        if (toolsContainer.isVisible()) {
            switchChooser(sizeChooser, animate)
        } else {
            toolChooser.hide()
            colorChooser.hide()
            sizeChooser.show()
            showTools(chooser = sizeChooser, animate = animate)
        }
    }

    override fun setToolSelected(toolType: Int) {
        val toolIcon = when (toolType) {
            TYPE_PENCIL -> R.drawable.lead_pencil
            TYPE_BRUSH -> R.drawable.brush
            TYPE_MARKER -> R.drawable.marker
            TYPE_FLUFFY -> R.drawable.spray
            TYPE_FILL -> R.drawable.format_color_fill
            TYPE_ERASER -> R.drawable.eraser
            else -> return
        }
        tuneTool.setImageResource(toolIcon)
    }

    override fun setColorSelected(color: Int) {
        tuneColor.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

    override fun setSizeSelected(size: Int) {
        val sizeIcon = when (size) {
            SIZE_S -> R.drawable.size_s
            SIZE_M -> R.drawable.size_m
            SIZE_L -> R.drawable.size_l
            SIZE_XL -> R.drawable.size_xl
            SIZE_XXL -> R.drawable.size_xxl
            else -> return
        }
        tuneSize.setImageResource(sizeIcon)
    }

    override fun hideChooser(animate: Boolean) {
        if (toolsContainer.isVisible()) {
            hideTools(animate)
        }
    }

    override fun tuneClicks(): Observable<Int> = selectChooserRelay
            .throttleFirst(ANIMATION_DURATION, TimeUnit.MILLISECONDS)

    override fun hideChooserClicks(): Observable<Unit> = hideChooserRelay
            .throttleFirst(ANIMATION_DURATION, TimeUnit.MILLISECONDS)

    override fun toolSelected(): Observable<Int> = toolRelay

    override fun colorSelected(): Observable<Int> = colorRelay

    override fun sizeSelected(): Observable<Int> = sizeRelay

    private fun switchChooser(nextChooser: View, animate: Boolean) {
        val visibleChooser = when {
            toolChooser.isVisible() -> toolChooser
            colorChooser.isVisible() -> colorChooser
            sizeChooser.isVisible() -> sizeChooser
            else -> null
        }
        if (visibleChooser == nextChooser || visibleChooser == null) {
            hideTools(animate)
        } else {
            val delta = visibleChooser.height - nextChooser.height
            val fromTranslationY = if (delta >= 0) toolsWrapper.translationY else -delta.toFloat()
            val tillTranslationY = if (delta >= 0) delta.toFloat() else 0f
            visibleChooser.hideWithAlphaAnimation(
                    duration = ANIMATION_DURATION,
                    endCallback = { }
            )
            nextChooser.showWithAlphaAnimation(
                    duration = ANIMATION_DURATION,
                    endCallback = { }
            )
            toolsWrapper.moveWithTranslationAnimation(fromTranslationY, tillTranslationY, {
                toolChooser.hide()
                colorChooser.hide()
                sizeChooser.hide()
                nextChooser.show()
            })
        }
    }

    private fun showTools(chooser: View, animate: Boolean) {
        if (animate) {
            toolsContainer.show()
            toolsBackground.showWithAlphaAnimation()
            toolsWrapper.showWithTranslationAnimation(chooser.height.toFloat())
        } else {
            toolsContainer.show()
            toolsBackground.show()
            toolsWrapper.show()
        }
    }

    private fun hideTools(animate: Boolean, endCallback: (() -> Unit)? = null) {
        if (animate) {
            toolsBackground.hideWithAlphaAnimation {
                toolsContainer.hide()
                toolsBackground.hide()
            }
            toolsWrapper.hideWithTranslationAnimation { endCallback?.invoke() }
        } else {
            toolsContainer.hide()
            toolsBackground.hide()
            toolsWrapper.hide()
        }
    }

    private fun View.showWithAlphaAnimation(duration: Long = ANIMATION_DURATION,
                                            endCallback: (() -> Unit)? = null) {
        alpha = 0.0f
        show()
        animate()
                .setDuration(duration)
                .alpha(1.0f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        alpha = 1.0f
                        show()
                        endCallback?.invoke()
                    }
                })
    }

    private fun View.showWithTranslationAnimation(height: Float) {
        alpha = 0.0f
        translationY = height
        show()
        animate()
                .setDuration(ANIMATION_DURATION)
                .alpha(1.0f)
                .translationY(0f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        alpha = 1.0f
                        translationY = 0f
                        show()
                    }
                })
    }

    private fun View.moveWithTranslationAnimation(fromTranslationY: Float,
                                                  tillTranslationY: Float,
                                                  endCallback: () -> (Unit)) {
        translationY = fromTranslationY
        animate()
                .setDuration(ANIMATION_DURATION)
                .translationY(tillTranslationY)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        translationY = 0f
                        endCallback.invoke()
                    }
                })
    }

    private fun View.hideWithAlphaAnimation(duration: Long = ANIMATION_DURATION,
                                            endCallback: () -> (Unit)) {
        alpha = 1.0f
        animate()
                .setDuration(duration)
                .alpha(0.0f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        alpha = 0.0f
                        hide()
                        endCallback.invoke()
                    }
                })
    }

    private fun View.hideWithTranslationAnimation(endCallback: () -> (Unit)) {
        alpha = 1.0f
        translationY = 0f
        val endTranslationY = height.toFloat()
        animate()
                .setDuration(ANIMATION_DURATION)
                .alpha(0.0f)
                .translationY(endTranslationY)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        alpha = 0.0f
                        translationY = endTranslationY
                        hide()
                        endCallback.invoke()
                    }
                })
    }

    private fun View.setOnClickListener(id: Int, listener: () -> Unit) {
        findViewById<View>(id).setOnClickListener { listener.invoke() }
    }

}

private const val ANIMATION_DURATION: Long = 250

const val ID_TOOL_CHOOSER = 1
const val ID_COLOR_CHOOSER = 2
const val ID_SIZE_CHOOSER = 3
