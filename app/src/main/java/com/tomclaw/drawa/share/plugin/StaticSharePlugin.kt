package com.tomclaw.drawa.share.plugin

import com.tomclaw.drawa.share.SharePlugin
import io.reactivex.Observable
import java.io.File

class StaticSharePlugin : SharePlugin {

    override val image: Int
        get() = TODO("not implemented")
    override val title: Int
        get() = TODO("not implemented")
    override val description: Int
        get() = TODO("not implemented")

    override fun getOperation(): Observable<File> {
        TODO("not implemented")
    }

}