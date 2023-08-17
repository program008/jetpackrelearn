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

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enabot.composesamples.R
import com.enabot.composesamples.model.Item
import com.enabot.composesamples.ui.theme.JetpackrelearnTheme

val plantList = listOf(
    Item("Desert chic", R.drawable.desert_chic),
    Item("Tiny terrariums", R.drawable.tiny_terrariums),
    Item("Jungle Vibes", R.drawable.jungle_vibes)
)

val designList = listOf(
    Item("Monstera", R.drawable.monstera),
    Item("Aglaonema", R.drawable.aglaonema),
    Item("Peace lily", R.drawable.peace_lily),
    Item("Fiddle leaf tree", R.drawable.fiddle_leaf),
    Item("Desert chic", R.drawable.desert_chic),
    Item("Tiny terrariums", R.drawable.tiny_terrariums),
    Item("Jungle Vibes", R.drawable.jungle_vibes)
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomePage() {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            SearchBar()
        }
        Box(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        ) {
            Text(
                text = "Browse themes",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .paddingFromBaseline(top = 32.dp, bottom = 16.dp)
            )
        }
        LazyRow(
            modifier = Modifier.height(136.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
        ) {
            items(plantList.size) {
                if (it != 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
                PlantCard(plantList[it])
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Design your home garden",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .paddingFromBaseline(top = 40.dp)
            )
            Icon(
                painterResource(id = R.drawable.ic_filter_list),
                "filter",
                modifier = Modifier
                    .padding(top = 24.dp)
                    .size(24.dp)
            )
        }
        LazyColumn(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 56.dp)
        ) {
            items(designList.size) {
                if (it != 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                DesignCard(designList[it])
            }
        }
    }
}

@Composable
fun DesignCard(plant: Item) {
    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth()
    ) {
        Image(
            painterResource(id = plant.resId),
            contentScale = ContentScale.Crop,
            contentDescription = "image",
            modifier = Modifier
                .padding(end = 16.dp)
                .size(64.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = plant.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .paddingFromBaseline(top = 24.dp)
                    )
                    Text(
                        text = "This is a description",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                    )
                }
                Checkbox(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .size(24.dp),
                    checked = plant.enable,
                    onCheckedChange = {
                        plant.enable = it
                    },
                    colors = CheckboxDefaults.colors(
                        checkmarkColor = MaterialTheme.colorScheme.background
                    )
                )
            }
            Divider(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(top = 16.dp),
                thickness = 0.5.dp
            )
        }
    }
}

@Composable
fun PlantCard(plant: Item) {
    Card(
        modifier = Modifier
            .size(136.dp)
            .clip(RoundedCornerShape(4.dp))
    ) {
        Column {
            Image(
                painterResource(id = plant.resId),
                contentScale = ContentScale.Crop,
                contentDescription = "image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = plant.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .paddingFromBaseline(top = 24.dp, bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
fun SearchBar() {
    var text by remember { mutableStateOf("") }
    BasicTextField(
        value = text,
        onValueChange = { text = it },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp),
        decorationBox = { innerTextField ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.ic_search)),
                    contentDescription = null,
                    Modifier.size(20.dp)
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (text.isEmpty()) {
                        Text(text = "请输入内容", color = Color(0xff999999), fontSize = 13.sp)
                    }
                    innerTextField()
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(18.dp)
                        .background(color = Color(0xFFDBDBDB))
                        .padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.width(13.dp))
                IconButton(onClick = { /*TODO*/ }) {
                    Text(text = "搜索", color = Color(0xFF00A0F8), fontSize = 17.sp)
                }
            }
        }
    )
}

@Preview
@Composable
fun DesignCardPreview() {
    JetpackrelearnTheme() {
        DesignCard(designList[0])
    }
}

@Preview
@Composable
fun PlantCardPreview() {
    JetpackrelearnTheme() {
        PlantCard(plantList[0])
    }
}

@Preview(
    name = "Light Mode",
    wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE
)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun HomePagePreview() {
    JetpackrelearnTheme() {
        HomePage()
    }
}
