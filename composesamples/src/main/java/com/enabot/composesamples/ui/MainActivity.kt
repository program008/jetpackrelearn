package com.enabot.composesamples.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import coil.compose.rememberAsyncImagePainter
import com.enabot.composesamples.R
import com.enabot.composesamples.ui.theme.JetpackrelearnTheme
import com.enabot.composesamples.ui.theme.welcomeAssets
import com.enabot.composesamples.ui.bloom.NavGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackrelearnTheme {
                NavGraph(window)
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
            .wrapContentSize()
            .padding(all = 8.dp),
        color = surfaceColor
    ) {
        Row(
            modifier = Modifier.padding(all = 8.dp), // 在我们的 Card 周围添加 padding)
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = "https://picsum.photos/300/300",
                    placeholder = painterResource(
                        id = R.drawable.sticker
                    )
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(
                        1.5.dp, MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )
            Spacer(Modifier.padding(horizontal = 8.dp)) // 添加一个空的控件用来填充水平间距，设置 padding 为 8.dp
            Column(modifier = Modifier.weight(1f)) {
                Image(
                    painter = rememberVectorPainter(image = ImageVector.vectorResource(id = MaterialTheme.welcomeAssets.logo)),
                    contentDescription = null,
                    modifier = Modifier.wrapContentSize()
                )
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
            Spacer(Modifier.padding(horizontal = 8.dp))
            Image(
                painter = rememberVectorPainter(image = ImageVector.vectorResource(id = MaterialTheme.welcomeAssets.illos)),
                contentDescription = null,
                modifier = Modifier.size(80.dp, 80.dp)
            )
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

/**
 * 1.约束布局ConstraintLayout
 * 创建引用的方式 createRefs() createRefFor()
 *
 * 2.Guideline
 * 可以创建一个指示线，指示线可以相较于父组件的top、bottom、start、end以一个百分比或dp进行偏移，
 * 以便别的组件可以针对指示线进行约束，Guideline创建方式有以下4种：
 * - 较于父组件左边10%位置创建
 *
 * `val startGuideline = createGuidelineFromStart(0.1f)`
 * - 较于父组件右边10%位置创建
 *
 * `val endGuideline = createGuidelineFromEnd(0.1f)`
 *
 * - 较于父组件顶部16dp位置创建
 *
 * `val topGuideline = createGuidelineFromTop(16.dp)`
 *
 * - 较于父组件底部16dp位置创建
 *
 * `val bottomGuideline = createGuidelineFromBottom(16.dp)`
 *
 * 3.Barrier
 * Barrier可以将多个内容组件引用组合成一个屏障，其他的组件就可以以屏障Barrier来进行约束，
 * 创建Barrier有以下4中方式:
 * - 以btn，txt进行组合，创建右边的barrier
 *
 *  `val barrier = createEndBarrier(btn, txt)`
 *
 * - 以btn，txt进行组合，创建左边的barrier
 *
 *  `val barrier = createStartBarrier(btn, txt)`
 *
 * - 以btn，txt进行组合，创建顶部的barrier
 *
 *  `val barrier = createTopBarrier(btn, txt)`
 *
 * - 以btn，txt进行组合，创建底部的barrier
 *
 *  `val barrier = createBottomBarrier(btn, txt)`
 *
 *  4.Chain
 *  Chain用于将多个内容组件引用组合成以个链，并以不同的 ChainStyles 配置链内各个组件的分布，创建方式有两种：
 *  - 创建水平的链
 *
 * val chain = createHorizontalChain(txt1, txt2, txt3, chainStyle = ChainStyle.SpreadInside)
 *
 * - 创建垂直的链
 *
 * val chain = createVerticalChain(txt1, txt2, txt3, chainStyle = ChainStyle.SpreadInside)
 * ChainStyles配置:
 * - ChainStyle.Spread：空间会在所有可组合项之间均匀分布，包括第一个可组合项之前和最后一个可组合项之后的可用空间。
 * - ChainStyle.SpreadInside：空间会在所有可组合项之间均匀分布，不包括第一个可组合项之前或最后一个可组合项之后的任何可用空间。
 * - ChainStyle.Packed：空间会分布在第一个可组合项之前和最后一个可组合项之后，各个可组合项之间没有空间，会挤在一起。
 */
@Preview(
    name = "Light Mode",
    wallpaper = Wallpapers.YELLOW_DOMINATED_EXAMPLE
)
@Composable
fun ConstraintLayoutTest() {
    JetpackrelearnTheme {
        Surface(modifier = Modifier.fillMaxWidth()) {
            ConstraintLayout {
                val (button1, button2) = createRefs()
                val startGuideline = createGuidelineFromStart(0.5f)
                Button(onClick = { /*TODO*/ },
                    modifier = Modifier.constrainAs(button1) {
                        top.linkTo(parent.top, margin = 16.dp)
                        end.linkTo(startGuideline)
                    }) {
                    Text(text = "button1")
                }
                Button(onClick = { /*TODO*/ },
                    modifier = Modifier.constrainAs(button2) {
                        top.linkTo(parent.top, margin = 8.dp)
                        start.linkTo(startGuideline)
                    }) {
                    Text(text = "button2")
                }
                val barrier = createBottomBarrier(button1, button2)
                val button3 = createRef()
                Button(onClick = { /*TODO*/ },
                    modifier = Modifier.constrainAs(button3) {
                        top.linkTo(barrier)
                        start.linkTo(parent.start)
                    }) {
                    Text(text = "button3")
                }
                val (button4, button5) = createRefs()
                val chain = createHorizontalChain(
                    button3,
                    button4,
                    button5,
                    chainStyle = ChainStyle.SpreadInside
                )
                Button(onClick = { /*TODO*/ }, modifier = Modifier.constrainAs(button4) {
                    top.linkTo(button3.top)
                }) {
                    Text(text = "button4")
                }
                Button(onClick = { /*TODO*/ }, modifier = Modifier.constrainAs(button5) {
                    top.linkTo(button3.top)
                }) {
                    Text(text = "button5")
                }
            }
        }
    }
}

/**
 * 如果你不想在ConstraintLayout作用域内定义引用以及约束规则，
 * 那么可以将 ConstraintSet 作为参数传递给 ConstraintLayout，
 * 外部通过createRefFor("key")指定一个字符串key创建引用，constrain("key")进行约束条件；
 * 内容组件使用修饰符layoutId("key")进行约束匹配
 */
@Preview
@Composable
fun ConstraintLayoutTest2() {
    //假如布局需要根据尺寸和方向来更改布局内容，则BoxWithConstraints自适应布局可以派上用场。
    BoxWithConstraints {
        val constraints = if (minWidth < 600.dp) {
            decoupledConstraints(margin = 16.dp) // Portrait constraints
        } else {
            decoupledConstraints(margin = 32.dp) // Landscape constraints
        }
        ConstraintLayout(constraintSet = constraints) {
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier.layoutId("button1")
            ) {
                Text(text = "button1")
            }
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier.layoutId("button2")
            ) {
                Text(text = "button2")
            }
        }
    }
}

private fun decoupledConstraints(margin: Dp): ConstraintSet {
    return ConstraintSet {
        val button1 = createRefFor("button1")
        val button2 = createRefFor("button2")
        constrain(button1) {
            top.linkTo(parent.top, margin)
        }
        constrain(button2) {
            top.linkTo(button1.bottom)
            start.linkTo(button1.end)
        }
    }
}

/**
 * Stop Using ViewModel To Manage Your State
 * ViewModels should be used if you want to access the business logic,
 * and you need to update the state depending on what you get provided
 * from the business logic, but if you just want to deal with the state
 * of your UI then using view models would be an overkill for this you
 * also can just put your state in your composable function.
 *
 * but it wouldn’t look nice.
 * 如何改变，实现状态分离
 * - 1.创建一个状态类封装状态
 * - 2.创建composable 函数rememberScreenState，可普通变量变成remember变量
 * - 3.在需要使用这些状态的composable函数中引入函数rememberScreenState，该函数中包括了所有的状态。
 */
class ScreenState(
    val snackbarHostState: SnackbarHostState,
    val scope: CoroutineScope,
    val scrollState: ScrollState
) {
    var counter by mutableStateOf(0)

    var isButtonVisible by mutableStateOf(false)

    fun showSnackBar(message: String) {
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }
}

@Composable
fun rememberScreenState(
    scaffoldState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    scrollState: ScrollState = rememberScrollState()
) = remember {
    ScreenState(scaffoldState, scope, scrollState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Screen() {
    val screenState = rememberScreenState() //all the state is here
    Scaffold(snackbarHost = { SnackbarHost(screenState.snackbarHostState) }) { padding ->
        if (screenState.isButtonVisible) {
            screenState.showSnackBar("good job!!")
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Button(onClick = {
                ++screenState.counter
                screenState.isButtonVisible = !screenState.isButtonVisible
            }) {
                Text(text = "click me ${screenState.counter}")
            }
            Text(text = "是否显示snackbar ${screenState.isButtonVisible}")
        }

        //the rest of the Ui
    }
}

