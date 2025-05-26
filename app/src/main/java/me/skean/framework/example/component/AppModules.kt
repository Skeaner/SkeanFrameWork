package me.skean.framework.example.component

import me.skean.framework.example.net.DouBanApi
import me.skean.framework.example.repository.DouBanRepository
import me.skean.skeanframework.utils.NetworkUtil
import org.koin.dsl.module

/**
 * Created by Skean on 2025/05/23.
 */
object AppModules {
    private val factories = module {
        factory { NetworkUtil.createService<DouBanApi>() }
        factory { DouBanRepository(get()) }
    }

    private val singletons = module {

    }

    private val viewModels = module {

    }

    private val fragments = module {

    }


    val all = listOf(factories, singletons, viewModels, fragments)
}