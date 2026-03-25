package com.groze.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val GrozeLightColorScheme = lightColorScheme(
    primary = GrozePrimary,
    onPrimary = GrozeOnPrimary,
    primaryContainer = GrozePrimaryContainer,
    onPrimaryContainer = GrozeOnPrimaryContainer,
    inversePrimary = GrozeInversePrimary,

    secondary = GrozeSecondary,
    onSecondary = GrozeOnSecondary,
    secondaryContainer = GrozeSecondaryContainer,
    onSecondaryContainer = GrozeOnSecondaryContainer,

    tertiary = GrozeTertiary,
    onTertiary = GrozeOnTertiary,
    tertiaryContainer = GrozeTertiaryContainer,
    onTertiaryContainer = GrozeOnTertiaryContainer,

    error = GrozeError,
    onError = GrozeOnError,
    errorContainer = GrozeErrorContainer,
    onErrorContainer = GrozeOnErrorContainer,

    background = GrozeBackground,
    onBackground = GrozeOnBackground,

    surface = GrozeSurface,
    onSurface = GrozeOnSurface,
    surfaceVariant = GrozeSurfaceVariant,
    onSurfaceVariant = GrozeOnSurfaceVariant,
    surfaceTint = GrozeSurfaceTint,
    inverseSurface = GrozeInverseSurface,
    inverseOnSurface = GrozeInverseOnSurface,

    outline = GrozeOutline,
    outlineVariant = GrozeOutlineVariant,

    surfaceBright = GrozeSurfaceBright,
    surfaceDim = GrozeSurfaceDim,
    surfaceContainer = GrozeSurfaceContainer,
    surfaceContainerHigh = GrozeSurfaceContainerHigh,
    surfaceContainerHighest = GrozeSurfaceContainerHighest,
    surfaceContainerLow = GrozeSurfaceContainerLow,
    surfaceContainerLowest = GrozeSurfaceContainerLowest
)

private val GrozeDarkColorScheme = darkColorScheme(
    primary = GrozeDarkPrimary,
    onPrimary = GrozeDarkOnPrimary,
    primaryContainer = GrozeDarkPrimaryContainer,
    onPrimaryContainer = GrozeDarkOnPrimaryContainer,
    inversePrimary = GrozeDarkInversePrimary,

    secondary = GrozeDarkSecondary,
    onSecondary = GrozeDarkOnSecondary,
    secondaryContainer = GrozeDarkSecondaryContainer,
    onSecondaryContainer = GrozeDarkOnSecondaryContainer,

    tertiary = GrozeDarkTertiary,
    onTertiary = GrozeDarkOnTertiary,
    tertiaryContainer = GrozeDarkTertiaryContainer,
    onTertiaryContainer = GrozeDarkOnTertiaryContainer,

    error = GrozeDarkError,
    onError = GrozeDarkOnError,
    errorContainer = GrozeDarkErrorContainer,
    onErrorContainer = GrozeDarkOnErrorContainer,

    background = GrozeDarkBackground,
    onBackground = GrozeDarkOnBackground,

    surface = GrozeDarkSurface,
    onSurface = GrozeDarkOnSurface,
    surfaceVariant = GrozeDarkSurfaceVariant,
    onSurfaceVariant = GrozeDarkOnSurfaceVariant,
    surfaceTint = GrozeDarkPrimary,
    inverseSurface = GrozeDarkInverseSurface,
    inverseOnSurface = GrozeDarkInverseOnSurface,

    outline = GrozeDarkOutline,
    outlineVariant = GrozeDarkOutlineVariant,

    surfaceBright = GrozeDarkSurfaceBright,
    surfaceDim = GrozeDarkSurfaceDim,
    surfaceContainer = GrozeDarkSurfaceContainer,
    surfaceContainerHigh = GrozeDarkSurfaceContainerHigh,
    surfaceContainerHighest = GrozeDarkSurfaceContainerHighest,
    surfaceContainerLow = GrozeDarkSurfaceContainerLow,
    surfaceContainerLowest = GrozeDarkSurfaceContainerLowest
)

@Composable
fun GrozeTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) GrozeDarkColorScheme else GrozeLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = GrozeTypography,
        shapes = GrozeShapes,
        content = content
    )
}
