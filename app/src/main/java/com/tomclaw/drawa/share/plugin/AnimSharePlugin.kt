package com.tomclaw.drawa.share.plugin

import com.tomclaw.drawa.R
import com.tomclaw.drawa.share.SharePlugin
import io.reactivex.Observable
import java.io.File
import java.util.concurrent.TimeUnit

class AnimSharePlugin : SharePlugin {

    override val image: Int
        get() = R.drawable.animation
    override val title: Int
        get() = R.string.anim_share_title
    override val description: Int
        get() = R.string.anim_share_description

    override val operation: Observable<File> = Observable
            .timer(3, TimeUnit.SECONDS)
            .flatMap { Observable.empty<File>() }

}