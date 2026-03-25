package com.groze.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val GrozeColorScheme = lightColorScheme(
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

@Composable
fun GrozeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GrozeColorScheme,
        typography = GrozeTypography,
        shapes = GrozeShapes,
        content = content
    )
}
