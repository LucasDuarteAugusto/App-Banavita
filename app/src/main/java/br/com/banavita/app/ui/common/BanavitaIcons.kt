package br.com.banavita.app.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import br.com.banavita.app.R

/**
 * Ícones que não existem em `material-icons-core`. Vetores locais evitam
 * Unresolved reference no Android Studio com o artefato extended.
 */
object BanavitaIcons {
    val People: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_people)

    val LocalShipping: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_local_shipping)

    val ShoppingBag: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_shopping_bag)

    val Category: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_category)

    val Inventory: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_inventory)

    val Apps: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_apps)

    val Event: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_event)

    val Store: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_store)

    val Payments: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_payments)

    val Mic: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_mic)

    val Visibility: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_visibility)

    val VisibilityOff: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_visibility_off)

    val ContentCopy: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_content_copy)

    val Settings: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_settings)
}
