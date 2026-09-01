package br.com.banavita.app.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> EntityListContent(
    items: List<T>,
    isLoading: Boolean,
    emptyMessage: String,
    key: (T) -> Any,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
    itemContent: @Composable (T) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            isLoading -> ListLoadingState()
            items.isEmpty() -> ListEmptyState(message = emptyMessage)
            else -> {
                LazyColumn(
                    contentPadding = contentPadding,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(items, key = key) { item ->
                        itemContent(item)
                    }
                }
            }
        }
    }
}

@Composable
fun <T> EntityListScreen(
    items: List<T>,
    isLoading: Boolean,
    emptyMessage: String,
    fabContentDescription: String,
    key: (T) -> Any,
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit = {},
    itemContent: @Composable (T) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        EntityListContent(
            items = items,
            isLoading = isLoading,
            emptyMessage = emptyMessage,
            key = key,
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
            itemContent = itemContent,
        )

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            onClick = onAddClick,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = fabContentDescription,
            )
        }
    }
}
