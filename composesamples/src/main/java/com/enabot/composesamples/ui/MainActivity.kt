package com.enabot.composesamples.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.enabot.composesamples.R
import com.enabot.composesamples.ui.theme.JetpackrelearnTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackrelearnTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val message = Message(
                        "ali",
                        "ali is beautiful girl,are you like her?ali is beautiful girl,are you like her?ali is beautiful girl,are you like her?"
                    )
                    JetpackrelearnTheme {
                        MessageCard(message)
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetpackrelearnTheme {
        Greeting("Android1")
    }
}

data class Message(val author: String, val body: String)
data class ButtonState(var text: String, var textColor: Color, var buttonColor: Color)

@Composable
fun MessageCard(message: Message) {
    var isExpanded by remember { mutableStateOf(false) }
    // 创建一个能够根据 isExpanded 变量值而改变颜色的变量
    val surfaceColor by animateColorAsState(
        targetValue = if (isExpanded) Color(0xFFCCCCCC) else MaterialTheme.colorScheme.surface,
        label = ""
    )

    // 获取按钮的状态
    val interactionState = remember { MutableInteractionSource() }

    // 使用 Kotlin 的解构方法
    val (text, textColor, buttonColor) = when {
        interactionState.collectIsPressedAsState().value -> ButtonState(
            "收起",
            Color.Red,
            Color.Black
        )

        else -> ButtonState("展开", Color.White, Color.Red)
    }
    Surface(
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 5.dp,
        modifier = Modifier
            .padding(all = 8.dp),
        color = surfaceColor
    ) {
        Row(
            modifier = Modifier.padding(all = 8.dp), // 在我们的 Card 周围添加 padding)
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = "https://picsum.photos/300/300"),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.secondary, shape = CircleShape)
            )
            Spacer(Modifier.padding(horizontal = 8.dp)) // 添加一个空的控件用来填充水平间距，设置 padding 为 8.dp
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = message.author,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge
                ) // 添加颜色)
                Spacer(Modifier.padding(vertical = 2.dp))
                Text(
                    text = message.body,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    modifier = Modifier.animateContentSize()
                )
            }
            Spacer(Modifier.padding(horizontal = 8.dp))
            Button(
                onClick = { isExpanded = !isExpanded },
                interactionSource = interactionState,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                ),
                modifier = Modifier
                    .height(30.dp)
            ) {
                Text(text = text, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun AlertDialogDemo() {
    val openDialog = remember { mutableStateOf(true) }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = {
                Text(
                    text = "开启定位服务",
                    fontWeight = FontWeight.W700,
                    style = MaterialTheme.typography.headlineMedium
                )
            },
            text = {
                Text(
                    text = "这将意味着，我们会给您提供精准的位置服务，并且您将接受关于您订阅的位置信息",
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { openDialog.value = false }) {
                    Text(
                        text = "确定",
                        fontWeight = FontWeight.W700,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    openDialog.value = false
                }) {
                    Text(
                        text = "取消",
                        fontWeight = FontWeight.W700,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            })
    }
}

@Preview(name = "Light Mode")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
fun PreMessageCard() {
    val message = Message(
        "ali",
        "ali is beautiful girl,are you like her?ali is beautiful girl,are you like her?ali is beautiful girl,are you like her?"
    )
    JetpackrelearnTheme {
        MessageCard(message)
    }

}