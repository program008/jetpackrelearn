package com.enabot.composesamples.ui.bloom

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.enabot.composesamples.R
import com.enabot.composesamples.model.Item
import com.enabot.composesamples.ui.theme.JetpackrelearnTheme

/**
 * @author liu tao
 * @date 2023/8/17 11:08
 * @description
 */
val navList = listOf(
    Item("Home", R.drawable.ic_home),
    Item("Favorites", R.drawable.ic_favorite_border),
    Item("Profile", R.drawable.ic_account_circle),
    Item("Cart", R.drawable.ic_shopping_cart)
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainContainerPage(navHostController: NavHostController) {
    var currentNavigationIndex by rememberSaveable { mutableIntStateOf(0) }
    Scaffold(
        bottomBar = {
            BottomBar(currentNavigationIndex) {
                currentNavigationIndex = it
            }
        }
    ) {
        CompositionLocalProvider(LocaleNavHostController provides navHostController) {
            when (currentNavigationIndex) {
                0 -> HomePage()
                1 -> FavoritesPage()
                2 -> ProfilePage()
                3 -> CartPage()
                else -> HomePage()
            }
        }
    }
}

@Composable
fun BottomBar(index: Int, callback: (index: Int) -> Unit) {
    NavigationBar(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
    ) {
        navList.forEachIndexed { i, item ->
            NavigationBarItem(
                onClick = {
                    callback.invoke(i)
                },
                icon = {
                    Icon(
                        painterResource(id = item.resId),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                },
                label = {
                    Text(
                        item.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                selected = (index == i)
            )
        }
    }
}

@Preview
@Composable
fun BottomBarPreview() {
    JetpackrelearnTheme() {
        BottomBar(0) {}
    }
}