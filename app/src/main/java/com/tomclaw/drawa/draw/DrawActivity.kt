package com.tomclaw.drawa.draw

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tomclaw.drawa.R
import com.tomclaw.drawa.main.getComponent
import com.tomclaw.drawa.stock.di.DrawModule
import javax.inject.Inject

class DrawActivity : AppCompatActivity(), DrawPresenter.DrawRouter {

    @Inject
    lateinit var presenter: DrawPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        val presenterState = savedInstanceState?.getBundle(KEY_PRESENTER_STATE)
        application.getComponent()
                .drawComponent(DrawModule(presenterState))
                .inject(activity = this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.draw)

        val view = DrawViewImpl(window.decorView)

        presenter.attachView(view)
    }

    override fun onStart() {
        super.onStart()
        presenter.attachRouter(this)
    }

    override fun onStop() {
        presenter.detachRouter()
        super.onStop()
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBundle(KEY_PRESENTER_STATE, presenter.saveState())
    }

    override fun showStockScreen() {
    }

}

fun createDrawActivityIntent(context: Context) = Intent(context, DrawActivity::class.java)

private const val KEY_PRESENTER_STATE = "presenter_state"