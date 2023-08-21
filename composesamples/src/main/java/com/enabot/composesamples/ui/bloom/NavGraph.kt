/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.enabot.composesamples.ui.bloom

import android.view.Window
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

//anim {
//                this.enter = R.anim.slide_in_right
//                this.exit = R.anim.slide_out_left
//                this.popEnter = R.anim.slide_in_left
//                this.popExit = R.anim.slide_out_right
//            }
object RouterPath {
    val WELCOME = "welcome"
    val LOGIN = "login"
    val HOME = "home"
}

object ParamsConfig {
    /**
     * 参数-name
     */
    const val PARAMS_NAME = "name"

    /**
     * 参数-age
     */
    const val PARAMS_AGE = "age"
}

val LocaleNavHostController = compositionLocalOf<NavHostController?> { error("no init controller") }

@Composable
fun NavGraph(window: Window) {
    val navHostController = rememberNavController()
    NavHost(navController = navHostController, startDestination = RouterPath.WELCOME) {
        composable(RouterPath.WELCOME) {
            window.statusBarColor = MaterialTheme.colorScheme.primary.toArgb()
            WelcomePage(navHostController)
        }
        composable("${RouterPath.LOGIN}/{${ParamsConfig.PARAMS_NAME}}?${ParamsConfig.PARAMS_AGE}={${ParamsConfig.PARAMS_AGE}}",
            arguments = listOf(
                //必传参数
                navArgument(ParamsConfig.PARAMS_NAME) {},
                navArgument(ParamsConfig.PARAMS_AGE) {
                    defaultValue = 30
                    type = NavType.IntType
                }
            )
        ) {
            window.statusBarColor = MaterialTheme.colorScheme.background.toArgb()
            val argument = requireNotNull(it.arguments)
            val name = argument.getString(ParamsConfig.PARAMS_NAME)
            val age = argument.getInt(ParamsConfig.PARAMS_AGE)
            LoginPage(name, age, navHostController)
        }
        composable(RouterPath.HOME) {
            window.statusBarColor = MaterialTheme.colorScheme.background.toArgb()
            MainContainerPage(navHostController)
        }
    }
}
