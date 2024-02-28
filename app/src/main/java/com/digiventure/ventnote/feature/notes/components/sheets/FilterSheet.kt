package com.digiventure.ventnote.feature.notes.components.sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digiventure.ventnote.R
import com.digiventure.ventnote.commons.Constants
import com.digiventure.ventnote.components.bottomSheet.RegularBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(
    openBottomSheet: MutableState<Boolean>,
    bottomSheetState: SheetState,
    onDismiss: () -> Unit,
    onFilter: (sortBy: String, orderBy: String) -> Unit
) {
    val createdDate = stringResource(id = R.string.sort_created_date)
    val title = stringResource(id = R.string.sort_title)
    val modifiedDate = stringResource(id = R.string.sort_modified_date)
    val sortByOptions = listOf(title, createdDate, modifiedDate)

    val ascending = stringResource(id = R.string.order_ascending)
    val descending = stringResource(id = R.string.order_descending)
    val orderByOptions = listOf(ascending, descending)

    val selectedSortBy = remember { mutableStateOf(createdDate) }
    val selectedOrderBy = remember { mutableStateOf(descending) }

    fun convertSortBy(sortBy: String): String {
        return when (sortBy) {
            title -> Constants.TITLE
            createdDate -> Constants.CREATED_AT
            modifiedDate -> Constants.UPDATED_AT
            else -> {
                Constants.CREATED_AT
            }
        }
    }

    fun convertOrderBy(orderBy: String): String {
        return when (orderBy) {
            ascending -> Constants.ASCENDING
            descending -> Constants.DESCENDING
            else -> {
                Constants.DESCENDING
            }
        }
    }

    RegularBottomSheet(
        isOpened = openBottomSheet.value,
        bottomSheetState = bottomSheetState,
        onDismissRequest = { openBottomSheet.value = false }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.sort_by),
                style = TextStyle(
                    fontSize = 18.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Box(modifier = Modifier.padding(8.dp))
            SortByList(sortByOptions = sortByOptions, selectedValue = selectedSortBy.value) {
                selectedSortBy.value = it
            }
            Box(modifier = Modifier.padding(16.dp))
            Text(
                text = stringResource(id = R.string.order_by),
                style = TextStyle(
                    fontSize = 18.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Box(modifier = Modifier.padding(8.dp))
            OrderByList(orderByOptions = orderByOptions, selectedValue = selectedOrderBy.value) {
                selectedOrderBy.value = it
            }
            Box(modifier = Modifier.padding(16.dp))
            Row {
                TextButton(
                    onClick = { onDismiss() },
                    shape = RoundedCornerShape(20),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(id = R.string.dismiss))
                }
                Button(
                    onClick = {
                        onFilter(
                            convertSortBy(selectedSortBy.value),
                            convertOrderBy(selectedOrderBy.value)
                        )
                        onDismiss()
                    },
                    shape = RoundedCornerShape(20),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            }
        }
    }
}

@Composable
fun SortByList(
    sortByOptions: List<String>, selectedValue: String,
    onPress: (sortByValue: String) -> Unit
) {
    Column(modifier = Modifier.selectableGroup()) {
        sortByOptions.forEach {
            ListItem(title = it, selectedValue = selectedValue) {
                onPress(it)
            }
        }
    }
}

@Composable
fun OrderByList(
    orderByOptions: List<String>, selectedValue: String,
    onPress: (orderByValue: String) -> Unit
) {
    Column(modifier = Modifier.selectableGroup()) {
        orderByOptions.forEach {
            ListItem(title = it, selectedValue = selectedValue) {
                onPress(it)
            }
        }
    }
}

@Composable
fun ListItem(title: String, selectedValue: String, onPress: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) { onPress() }
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = 16.sp, fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.weight(1f)
        )
        RadioButton(selected = (title == selectedValue), onClick = { onPress() })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun FilterSheetPreview() {
    val openBottomSheet = rememberSaveable { mutableStateOf(true) }
    val skipPartiallyExpanded = remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded.value
    )

    Scaffold(
        content = { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {}
        }
    )

    FilterSheet(
        openBottomSheet = openBottomSheet,
        bottomSheetState = bottomSheetState,
        onDismiss = {}
    ) { _, _ ->

    }
}