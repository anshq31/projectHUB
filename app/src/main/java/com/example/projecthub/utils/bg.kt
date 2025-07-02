import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.example.projecthub.viewModel.ThemeViewModel

import kotlin.random.Random



@Composable
fun AppBackground7(themeViewModel: ThemeViewModel){
    val isDarkTheme by themeViewModel.isDarkMode.collectAsState()

    if (isDarkTheme) {
        AppBackgroundDark()
    } else {
        AppBackgroundLight()
    }
}

@Composable
fun ChatWallpaperBackground(themeViewModel: ThemeViewModel){
    val isDarkTheme by themeViewModel.isDarkMode.collectAsState()
//    Log.d("ChatWallpaperBackground", "isDarkTheme: $isDarkTheme")


    if (isDarkTheme) {
        ChatWallpaperBackgroundDark()
    } else {
       ChatWallpaperBackgroundLight()
    }
}


@Composable
fun AppBackgroundDark() {
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val darkBlackGradient = Brush.linearGradient(
                colorStops = arrayOf(
                    0.0f to Color(0xFF0A0A0A),
                    0.3f to Color(0xFF080808),
                    0.6f to Color(0xFF050505),
                    0.8f to Color(0xFF030303),
                    1.0f to Color(0xFF000000)
                ),
                start = Offset(0f, size.height * 0.5f),
                end = Offset(size.width, size.height * 0.5f)
            )

            drawRect(brush = darkBlackGradient)

            val goldDiagonalPath = Path().apply {
                moveTo(size.width * -0.2f, size.height * -0.1f)
                lineTo(size.width * 0.3f, size.height * -0.2f)
                lineTo(size.width * 1.2f, size.height * 0.7f)
                lineTo(size.width * 0.7f, size.height * 0.8f)
                close()
            }

            val darkGoldGradient = Brush.linearGradient(
                colorStops = arrayOf(
                    0.0f to Color(0xFF8B6914).copy(alpha = 0.25f),
                    0.2f to Color(0xFFA67C00).copy(alpha = 0.22f),
                    0.4f to Color(0xFFD4AF37).copy(alpha = 0.2f),
                    0.5f to Color(0xFFEDC967).copy(alpha = 0.18f),
                    0.7f to Color(0xFF8B6914).copy(alpha = 0.15f),
                    0.85f to Color(0xFF6B550F).copy(alpha = 0.12f),
                    1.0f to Color(0xFF513C0B).copy(alpha = 0.1f)
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            )

            drawPath(
                path = goldDiagonalPath,
                brush = darkGoldGradient
            )

            val darkDiagonalPath = Path().apply {
                moveTo(size.width * 0.7f, size.height * -0.2f)
                lineTo(size.width * 1.3f, size.height * -0.2f)
                lineTo(size.width * 1.3f, size.height * 1.3f)
                lineTo(size.width * 0.3f, size.height * 1.3f)
                close()
            }

            val darkGradient = Brush.linearGradient(
                colorStops = arrayOf(
                    0.0f to Color.Black.copy(alpha = 0.3f),
                    0.4f to Color.Black.copy(alpha = 0.5f),
                    0.7f to Color.Black.copy(alpha = 0.7f),
                    1.0f to Color.Black.copy(alpha = 0.8f)
                ),
                start = Offset(size.width * 0.7f, 0f),
                end = Offset(size.width, size.height)
            )

            drawPath(
                path = darkDiagonalPath,
                brush = darkGradient
            )



            val bottomLeftGoldPath = Path().apply {
                moveTo(0f, size.height * 0.6f)
                lineTo(size.width * 0.4f, size.height)
                lineTo(0f, size.height)
                close()
            }

            val darkGoldBottomGradient = Brush.linearGradient(
                colorStops = arrayOf(
                    0.0f to Color(0xFF8B6914).copy(alpha = 0.3f),
                    0.5f to Color(0xFFA67C00).copy(alpha = 0.2f),
                    1.0f to Color(0xFF513C0B).copy(alpha = 0.1f)
                ),
                start = Offset(0f, size.height * 0.8f),
                end = Offset(size.width * 0.2f, size.height)
            )

            drawPath(
                path = bottomLeftGoldPath,
                brush = darkGoldBottomGradient
            )

            drawLine(
                color = Color(0xFFAA8523).copy(alpha = 0.3f),
                start = Offset(size.width * 0.5f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = size.width * 0.004f
            )

            val topRightPath = Path().apply {
                moveTo(size.width * 0.85f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height * 0.15f)
                close()
            }

            drawPath(
                path = topRightPath,
                brush = Brush.linearGradient(
                    colorStops = arrayOf(
                        0.0f to Color(0xFFD4AF37).copy(alpha = 0.15f),
                        1.0f to Color(0xFF8B6914).copy(alpha = 0.1f)
                    ),
                    start = Offset(size.width, 0f),
                    end = Offset(size.width * 0.9f, size.height * 0.1f)
                )
            )

            drawPath(
                path = goldDiagonalPath,
                color = Color(0xFF513C0B).copy(alpha = 0.25f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = size.width * 0.001f
                )
            )
        }
    }
}




@Composable
fun AppBackgroundLight() {
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val darkGrayGradient = Brush.linearGradient(
                colorStops = arrayOf(
                    0.0f to Color(0xFFE5E5E5),
                    0.3f to Color(0xFFDEDEDE),
                    0.6f to Color(0xFFD8D8D8),
                    0.8f to Color(0xFFD0D0D0),
                    1.0f to Color(0xFFC8C8C8)
                ),
                start = Offset(0f, size.height * 0.5f),
                end = Offset(size.width, size.height * 0.5f)
            )

            drawRect(brush = darkGrayGradient)

            val fadeYellowDiagonalPath = Path().apply {
                moveTo(size.width * -0.2f, size.height * -0.1f)
                lineTo(size.width * 0.3f, size.height * -0.2f)
                lineTo(size.width * 1.2f, size.height * 0.7f)
                lineTo(size.width * 0.7f, size.height * 0.8f)
                close()
            }

            val fadeYellowGradient = Brush.linearGradient(
                colorStops = arrayOf(
                    0.0f to Color(0xFFEDE1B8).copy(alpha = 0.5f),
                    0.2f to Color(0xFFE8D699).copy(alpha = 0.45f),
                    0.4f to Color(0xFFE2CA7B).copy(alpha = 0.4f),
                    0.5f to Color(0xFFEDE1B8).copy(alpha = 0.35f),
                    0.7f to Color(0xFFD7C285).copy(alpha = 0.3f),
                    0.85f to Color(0xFFD0BB74).copy(alpha = 0.25f),
                    1.0f to Color(0xFFC9B463).copy(alpha = 0.2f)
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            )

            drawPath(
                path = fadeYellowDiagonalPath,
                brush = fadeYellowGradient
            )

            val lightDiagonalPath = Path().apply {
                moveTo(size.width * 0.7f, size.height * -0.2f)
                lineTo(size.width * 1.3f, size.height * -0.2f)
                lineTo(size.width * 1.3f, size.height * 1.3f)
                lineTo(size.width * 0.3f, size.height * 1.3f)
                close()
            }

            val darkOverlayGradient = Brush.linearGradient(
                colorStops = arrayOf(
                    0.0f to Color(0xFFD0D0D0).copy(alpha = 0.3f),
                    0.4f to Color(0xFFC0C0C0).copy(alpha = 0.4f),
                    0.7f to Color(0xFFB8B8B8).copy(alpha = 0.5f),
                    1.0f to Color(0xFFB0B0B0).copy(alpha = 0.6f)
                ),
                start = Offset(size.width * 0.7f, 0f),
                end = Offset(size.width, size.height)
            )

            drawPath(
                path = lightDiagonalPath,
                brush = darkOverlayGradient
            )

            val bottomLeftPath = Path().apply {
                moveTo(0f, size.height * 0.6f)
                lineTo(size.width * 0.4f, size.height)
                lineTo(0f, size.height)
                close()
            }

            val creamBottomGradient = Brush.linearGradient(
                colorStops = arrayOf(
                    0.0f to Color(0xFFE2CA7B).copy(alpha = 0.5f),
                    0.5f to Color(0xFFEDE1B8).copy(alpha = 0.4f),
                    1.0f to Color(0xFFD7C285).copy(alpha = 0.3f)
                ),
                start = Offset(0f, size.height * 0.8f),
                end = Offset(size.width * 0.2f, size.height)
            )

            drawPath(
                path = bottomLeftPath,
                brush = creamBottomGradient
            )

            drawLine(
                color = Color(0xFFE2CA7B).copy(alpha = 0.5f),
                start = Offset(size.width * 0.5f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = size.width * 0.004f
            )

            val topRightPath = Path().apply {
                moveTo(size.width * 0.85f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height * 0.15f)
                close()
            }

            drawPath(
                path = topRightPath,
                brush = Brush.linearGradient(
                    colorStops = arrayOf(
                        0.0f to Color(0xFFE2CA7B).copy(alpha = 0.4f),
                        1.0f to Color(0xFFEDE1B8).copy(alpha = 0.3f)
                    ),
                    start = Offset(size.width, 0f),
                    end = Offset(size.width * 0.9f, size.height * 0.1f)
                )
            )



            val dotCount = 15
            for (i in 0 until dotCount) {
                val x = size.width * (0.1f + (i * 0.06f))
                val y = size.height * 0.1f

                drawCircle(
                    color = Color(0xFFE2CA7B).copy(alpha = 0.4f),
                    radius = size.width * 0.002f,
                    center = Offset(x, y)
                )
            }
        }
    }
}

@Composable
fun ChatWallpaperBackgroundDark() {
    val goldColor = Color(0xFF6E572C)

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val backgroundGradient = Brush.radialGradient(
                colorStops = arrayOf(
                    0.0f to Color(0xFF0F0D0A),
                    0.5f to Color(0xFF080706),
                    1.0f to Color(0xFF050403)
                ),
                center = Offset(size.width * 0.5f, size.height * 0.3f),
                radius = size.width * 0.8f
            )

            drawRect(brush = backgroundGradient)

            val gridSize = 8
            val cellWidth = size.width / gridSize
            val cellHeight = size.height / gridSize

            for (i in 0..gridSize) {
                drawLine(
                    color = goldColor.copy(alpha = 0.1f),
                    start = Offset(0f, i * cellHeight),
                    end = Offset(size.width, i * cellHeight),
                    strokeWidth = size.width * 0.001f
                )

                drawLine(
                    color = goldColor.copy(alpha = 0.1f),
                    start = Offset(i * cellWidth, 0f),
                    end = Offset(i * cellWidth, size.height),
                    strokeWidth = size.width * 0.001f
                )
            }

            val random = Random(42)
            val numPaths = 25

            for (p in 0 until numPaths) {
                val startX = random.nextInt(gridSize + 1) * cellWidth
                val startY = random.nextInt(gridSize + 1) * cellHeight
                var x = startX
                var y = startY

                drawCircle(
                    color = goldColor.copy(alpha = 0.7f),
                    radius = cellWidth * 0.05f,
                    center = Offset(x, y)
                )

                val pathSegments = random.nextInt(3, 8)

                for (s in 0 until pathSegments) {
                    val direction = random.nextInt(4)
                    val oldX = x
                    val oldY = y

                    when (direction) {
                        0 -> x += cellWidth * random.nextInt(1, 3)
                        1 -> y += cellHeight * random.nextInt(1, 3)
                        2 -> x -= cellWidth * random.nextInt(1, 3)
                        3 -> y -= cellHeight * random.nextInt(1, 3)
                    }

                    x = x.coerceIn(0f, size.width)
                    y = y.coerceIn(0f, size.height)

                    drawLine(
                        color = goldColor.copy(alpha = 0.5f),
                        start = Offset(oldX, oldY),
                        end = Offset(x, y),
                        strokeWidth = size.width * 0.003f
                    )

                    if (s == pathSegments - 1) {
                        drawCircle(
                            color = Color(0xFFFFD700).copy(alpha = 0.7f),
                            radius = cellWidth * 0.04f,
                            center = Offset(x, y)
                        )
                    } else if (random.nextFloat() > 0.7f) {
                        drawCircle(
                            color = Color(0xFFB8860B).copy(alpha = 0.4f),
                            radius = cellWidth * 0.02f,
                            center = Offset(x, y)
                        )
                    }
                }
            }

            for (i in 0 until 5) {
                val x = random.nextInt(gridSize + 1) * cellWidth
                val y = random.nextInt(gridSize + 1) * cellHeight
                val pulseRadius = cellWidth * 0.15f

                drawCircle(
                    color = Color(0xFFFFF8DC).copy(alpha = 0.7f),
                    radius = cellWidth * 0.05f,
                    center = Offset(x, y)
                )

                drawCircle(
                    color = goldColor.copy(alpha = 0.3f),
                    radius = cellWidth * 0.09f,
                    center = Offset(x, y)
                )

                drawCircle(
                    color = Color(0xFFDAA520).copy(alpha = 0.1f),
                    radius = pulseRadius,
                    center = Offset(x, y)
                )
            }

            for (i in 0 until 8) {
                val x = random.nextInt(gridSize * 2) * cellWidth / 2
                val y = random.nextInt(gridSize * 2) * cellHeight / 2

                drawCircle(
                    color = Color(0xFFFAE5B3).copy(alpha = 0.15f),
                    radius = cellWidth * 0.01f,
                    center = Offset(x, y)
                )
            }
        }
    }
}
@Composable
fun ChatWallpaperBackgroundLight() {
    val creamColor = Color(0xFFE8D699)

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val backgroundGradient = Brush.radialGradient(
                colorStops = arrayOf(
                    0.0f to Color(0xFFFAFAFA),
                    0.5f to Color(0xFFF5F5F5),
                    1.0f to Color(0xFFEEEEEE)
                ),
                center = Offset(size.width * 0.5f, size.height * 0.3f),
                radius = size.width * 0.8f
            )

            drawRect(brush = backgroundGradient)

            val gridSize = 8
            val cellWidth = size.width / gridSize
            val cellHeight = size.height / gridSize

            for (i in 0..gridSize) {
                drawLine(
                    color = creamColor.copy(alpha = 0.2f),
                    start = Offset(0f, i * cellHeight),
                    end = Offset(size.width, i * cellHeight),
                    strokeWidth = size.width * 0.001f
                )

                drawLine(
                    color = creamColor.copy(alpha = 0.2f),
                    start = Offset(i * cellWidth, 0f),
                    end = Offset(i * cellWidth, size.height),
                    strokeWidth = size.width * 0.001f
                )
            }

            val random = Random(42)
            val numPaths = 25

            for (p in 0 until numPaths) {
                val startX = random.nextInt(gridSize + 1) * cellWidth
                val startY = random.nextInt(gridSize + 1) * cellHeight
                var x = startX
                var y = startY

                drawCircle(
                    color = creamColor.copy(alpha = 0.7f),
                    radius = cellWidth * 0.05f,
                    center = Offset(x, y)
                )

                val pathSegments = random.nextInt(3, 8)

                for (s in 0 until pathSegments) {
                    val direction = random.nextInt(4)
                    val oldX = x
                    val oldY = y

                    when (direction) {
                        0 -> x += cellWidth * random.nextInt(1, 3)
                        1 -> y += cellHeight * random.nextInt(1, 3)
                        2 -> x -= cellWidth * random.nextInt(1, 3)
                        3 -> y -= cellHeight * random.nextInt(1, 3)
                    }

                    x = x.coerceIn(0f, size.width)
                    y = y.coerceIn(0f, size.height)

                    drawLine(
                        color = creamColor.copy(alpha = 0.5f),
                        start = Offset(oldX, oldY),
                        end = Offset(x, y),
                        strokeWidth = size.width * 0.003f
                    )

                    if (s == pathSegments - 1) {
                        drawCircle(
                            color = Color(0xFFFFE4B5).copy(alpha = 0.7f),
                            radius = cellWidth * 0.04f,
                            center = Offset(x, y)
                        )
                    } else if (random.nextFloat() > 0.7f) {
                        drawCircle(
                            color = Color(0xFFF0E68C).copy(alpha = 0.4f),
                            radius = cellWidth * 0.02f,
                            center = Offset(x, y)
                        )
                    }
                }
            }

            for (i in 0 until 5) {
                val x = random.nextInt(gridSize + 1) * cellWidth
                val y = random.nextInt(gridSize + 1) * cellHeight
                val pulseRadius = cellWidth * 0.15f

                drawCircle(
                    color = Color(0xFFFFFFFF).copy(alpha = 0.7f),
                    radius = cellWidth * 0.05f,
                    center = Offset(x, y)
                )

                drawCircle(
                    color = creamColor.copy(alpha = 0.3f),
                    radius = cellWidth * 0.09f,
                    center = Offset(x, y)
                )

                drawCircle(
                    color = Color(0xFFF5DEB3).copy(alpha = 0.1f),
                    radius = pulseRadius,
                    center = Offset(x, y)
                )
            }

            for (i in 0 until 8) {
                val x = random.nextInt(gridSize * 2) * cellWidth / 2
                val y = random.nextInt(gridSize * 2) * cellHeight / 2

                drawCircle(
                    color = Color(0xFFFFECB3).copy(alpha = 0.15f),
                    radius = cellWidth * 0.01f,
                    center = Offset(x, y)
                )
            }
        }
    }
}