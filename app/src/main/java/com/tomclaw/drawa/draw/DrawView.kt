package com.tomclaw.drawa.draw

import androidx.appcompat.widget.Toolbar
import android.view.View
import android.widget.ViewFlipper
import com.jakewharton.rxrelay2.PublishRelay
import com.tomclaw.drawa.R
import com.tomclaw.drawa.draw.tools.Tool
import com.tomclaw.drawa.draw.view.DrawingListener
import com.tomclaw.drawa.draw.view.DrawingView
import com.tomclaw.drawa.draw.view.TouchEvent
import com.tomclaw.drawa.util.MetricsProvider
import com.tomclaw.drawa.util.hideWithAlphaAnimation
import com.tomclaw.drawa.util.showWithAlphaAnimation
import io.reactivex.Observable

interface DrawView : ToolsView {

    fun setDrawingListener(listener: DrawingListener)

    fun acceptTool(tool: Tool)

    fun showProgress()

    fun showOverlayProgress()

    fun showContent()

    fun touchEvents(): Observable<TouchEvent>

    fun drawEvents(): Observable<Unit>

    fun navigationClicks(): Observable<Unit>

    fun undoClicks(): Observable<Unit>

    fun doneClicks(): Observable<Unit>

    fun duplicateClicks(): Observable<Unit>

    fun deleteClicks(): Observable<Unit>

}

class DrawViewImpl(
        view: View,
        drawHostHolder: DrawHostHolder,
        private val metricsProvider: MetricsProvider
) : DrawView, ToolsView by ToolsViewImpl(view) {

    private val toolbar: Toolbar = view.findViewById(R.id.toolbar)
    private val drawingView: DrawingView = view.findViewById(R.id.drawing_view)
    private val overlayProgress: View = view.findViewById(R.id.overlay_progress)
    private val flipper: ViewFlipper = view.findViewById(R.id.flipper)
    private val undoButton: View = view.findViewById(R.id.undo_button)
    private val doneButton: View = view.findViewById(R.id.done_button)

    private val touchRelay = PublishRelay.create<TouchEvent>()
    private val drawRelay = PublishRelay.create<Unit>()
    private val navigationRelay = PublishRelay.create<Unit>()
    private val undoRelay = PublishRelay.create<Unit>()
    private val doneRelay = PublishRelay.create<Unit>()
    private val duplicateRelay = PublishRelay.create<Unit>()
    private val deleteRelay = PublishRelay.create<Unit>()

    init {
        drawHostHolder.drawHost = drawingView
        toolbar.setTitle(R.string.draw)
        toolbar.setNavigationOnClickListener {
            navigationRelay.accept(Unit)
        }
        toolbar.inflateMenu(R.menu.draw)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_duplicate -> duplicateRelay.accept(Unit)
                R.id.menu_delete -> deleteRelay.accept(Unit)
            }
            true
        }
        undoButton.setOnClickListener { undoRelay.accept(Unit) }
        doneButton.setOnClickListener { doneRelay.accept(Unit) }
        drawingView.drawingListener = object : DrawingListener {
            override fun onTouchEvent(event: TouchEvent) {
                touchRelay.accept(event)
            }

            override fun onDraw() {
                drawRelay.accept(Unit)
            }
        }
    }

    override fun setDrawingListener(listener: DrawingListener) {
        drawingView.drawingListener = listener
    }

    override fun acceptTool(tool: Tool) {
        tool.initialize(drawingView, metricsProvider)
    }

    override fun showProgress() {
        flipper.displayedChild = 0
    }

    override fun showOverlayProgress() {
        overlayProgress.showWithAlphaAnimation(animateFully = true)
    }

    override fun showContent() {
        flipper.displayedChild = 1
        overlayProgress.hideWithAlphaAnimation(animateFully = false)
    }

    override fun touchEvents(): Observable<TouchEvent> = touchRelay

    override fun drawEvents(): Observable<Unit> = drawRelay

    override fun navigationClicks(): Observable<Unit> = navigationRelay

    override fun undoClicks(): Observable<Unit> = undoRelay

    override fun doneClicks(): Observable<Unit> = doneRelay

    override fun duplicateClicks(): Observable<Unit> = duplicateRelay

    override fun deleteClicks(): Observable<Unit> = deleteRelay

}
