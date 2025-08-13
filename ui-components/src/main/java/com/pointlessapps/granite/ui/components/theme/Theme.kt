package com.pointlessapps.granite.ui.components.theme

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
import com.pointlessapps.granite.ui.R

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
    primary = colorResource(R.color.light_primary),
    onPrimary = colorResource(R.color.light_onPrimary),
    secondary = colorResource(R.color.light_secondary),
    onSecondary = colorResource(R.color.light_onSecondary),
    background = colorResource(R.color.light_background),
    onBackground = colorResource(R.color.light_onBackground),
    surface = colorResource(R.color.light_surface),
    onSurface = colorResource(R.color.light_onSurface),
    onSurfaceVariant = colorResource(R.color.light_onSurfaceVariant),
    error = colorResource(R.color.light_error),
    onError = colorResource(R.color.light_onError),
    outline = colorResource(R.color.light_outline),
    outlineVariant = colorResource(R.color.light_outlineVariant),
    surfaceContainer = colorResource(R.color.light_surfaceContainer),
    surfaceContainerHigh = colorResource(R.color.light_surfaceContainerHigh),
    surfaceContainerLow = colorResource(R.color.light_surfaceContainerLow),
)

@Composable
private fun darkColorPalette() = darkColorScheme(
    primary = colorResource(R.color.dark_primary),
    onPrimary = colorResource(R.color.dark_onPrimary),
    secondary = colorResource(R.color.dark_secondary),
    onSecondary = colorResource(R.color.dark_onSecondary),
    background = colorResource(R.color.dark_background),
    onBackground = colorResource(R.color.dark_onBackground),
    surface = colorResource(R.color.dark_surface),
    onSurface = colorResource(R.color.dark_onSurface),
    onSurfaceVariant = colorResource(R.color.dark_onSurfaceVariant),
    error = colorResource(R.color.dark_error),
    onError = colorResource(R.color.dark_onError),
    outline = colorResource(R.color.dark_outline),
    outlineVariant = colorResource(R.color.dark_outlineVariant),
    surfaceContainer = colorResource(R.color.dark_surfaceContainer),
    surfaceContainerHigh = colorResource(R.color.dark_surfaceContainerHigh),
    surfaceContainerLow = colorResource(R.color.dark_surfaceContainerLow),
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
