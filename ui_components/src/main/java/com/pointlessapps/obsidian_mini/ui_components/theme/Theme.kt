package com.pointlessapps.obsidian_mini.ui_components.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.project.ui_components.R

private val fontFamily = FontFamily(
    Font(
        resId = R.font.montserrat_light,
        weight = FontWeight.Light,
    ),
    Font(
        resId = R.font.montserrat_normal,
        weight = FontWeight.Normal,
    ),
    Font(
        resId = R.font.montserrat_medium,
        weight = FontWeight.Medium,
    ),
    Font(
        resId = R.font.montserrat_semi_bold,
        weight = FontWeight.SemiBold,
    ),
    Font(
        resId = R.font.montserrat_bold,
        weight = FontWeight.Bold,
    ),
)

@Composable
private fun typography() = Typography().run {
    copy(
        displayLarge = displayLarge.copy(fontFamily = fontFamily),
        displayMedium = displayMedium.copy(fontFamily = fontFamily),
        displaySmall = displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = titleLarge.copy(fontFamily = fontFamily),
        titleMedium = titleMedium.copy(fontFamily = fontFamily),
        titleSmall = titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = bodySmall.copy(fontFamily = fontFamily),
        labelLarge = labelLarge.copy(fontFamily = fontFamily),
        labelMedium = labelMedium.copy(fontFamily = fontFamily),
        labelSmall = labelSmall.copy(fontFamily = fontFamily),
    )
}

@Composable
private fun shapes() = Shapes(
    small = RoundedCornerShape(dimensionResource(id = R.dimen.small_rounded_corners)),
    medium = RoundedCornerShape(dimensionResource(id = R.dimen.medium_rounded_corners)),
    large = RoundedCornerShape(dimensionResource(id = R.dimen.large_rounded_corners)),
)

@Composable
private fun lightColorPalette() = lightColorScheme(
    primary = colorResource(id = R.color.gray_3),
    onPrimary = colorResource(id = R.color.gray_8),
    secondary = colorResource(id = R.color.gray_4),
    onSecondary = colorResource(id = R.color.gray_8),
    background = colorResource(id = R.color.gray_2),
    onBackground = colorResource(id = R.color.gray_8),
)

@Composable
private fun darkColorPalette() = darkColorScheme(
    primary = colorResource(id = R.color.gray_5),
    onPrimary = colorResource(id = R.color.gray_1),
    secondary = colorResource(id = R.color.gray_6),
    onSecondary = colorResource(id = R.color.gray_1),
    background = colorResource(id = R.color.gray_7),
    onBackground = colorResource(id = R.color.gray_1),
)

@Composable
fun ProjectTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    lightColorPalette: ColorScheme = lightColorPalette(),
    darkColorPalette: ColorScheme = darkColorPalette(),
    typography: Typography = typography(),
    shapes: Shapes = shapes(),
    content: @Composable () -> Unit,
) {
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = when {
        dynamicColor && isDarkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !isDarkTheme -> dynamicLightColorScheme(LocalContext.current)
        isDarkTheme -> darkColorPalette
        else -> lightColorPalette
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content,
    )
}
