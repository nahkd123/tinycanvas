package io.github.nahkd123.tinycanvas.android

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalFloatingToolbar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.nahkd123.tinycanvas.android.ui.theme.TinyCanvasTheme
import io.github.nahkd123.tinycanvas.android.ui.view.TinyCanvasView
import io.github.nahkd123.tinycanvas.engine.layer.RasterLayer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false

        setContent {
            TinyCanvasTheme { App() }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            Scaffold { innerPadding ->
                var index by remember { mutableIntStateOf(0) }

                Box {
                    UserDrawableCanvas(modifier = Modifier.fillMaxSize())

                    OverlayScaffold(
                        modifier = Modifier.padding(innerPadding),
                        tools = {
                            FilledIconToggleButton(
                                checked = index == 0,
                                onCheckedChange = { index = 0 }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.stylus_pen_24px),
                                    contentDescription = null
                                )
                            }
                        },
                        quickMenus = {
                            FilledIconToggleButton(
                                checked = false,
                                onCheckedChange = {}
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.layers_24px),
                                    contentDescription = null
                                )
                            }
                        },
                        controls = {
                            IconButton(
                                onClick = {}
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.save_24px),
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@SuppressLint("ClickableViewAccessibility")
@Composable
fun UserDrawableCanvas(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        val context = LocalContext.current

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { TinyCanvasView(context, RasterLayer()) }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OverlayScaffold(
    modifier: Modifier = Modifier,
    invertHand: Boolean = false,
    tools: @Composable (() -> Unit),
    quickMenus: @Composable (() -> Unit),
    controls: @Composable (() -> Unit)
) {
    val handnessAlignmentBias by animateFloatAsState(
        targetValue = if (invertHand) -1f else 1f,
        animationSpec = MotionScheme.expressive().fastSpatialSpec()
    )

    BoxWithConstraints(
        modifier = modifier.fillMaxSize().padding(16.dp)
    ) {
        Column(
            modifier = Modifier.align(BiasAlignment(-handnessAlignmentBias, if (this.maxHeight < 600.dp) 1f else 0f)),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VerticalFloatingToolbar(expanded = false) { tools() }
            VerticalFloatingToolbar(expanded = false) { quickMenus() }
        }
        HorizontalFloatingToolbar(
            modifier = Modifier.align(BiasAlignment(-handnessAlignmentBias, -1f)),
            expanded = false
        ) {
            controls()
        }
    }
}