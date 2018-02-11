package com.tomclaw.drawa.stock.di

import com.tomclaw.drawa.draw.DrawActivity
import com.tomclaw.drawa.util.PerActivity
import dagger.Subcomponent

@PerActivity
@Subcomponent(modules = [DrawModule::class])
interface DrawComponent {

    fun inject(activity: DrawActivity)

}