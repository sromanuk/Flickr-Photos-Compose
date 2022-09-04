package com.example.flickrimages.ui.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flickrimages.ui.theme.AlmostWhite
import com.example.flickrimages.ui.theme.Gray
import com.example.flickrimages.ui.theme.Transparent
import com.example.flickrimages.ui.theme.spacing

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun SearchBar(
    modifier: Modifier = Modifier,
    input: String,
    output: (String) -> Unit,
    onClear: () -> Unit,
    showSelector: Boolean = true,
    selectorState: Boolean = false,
    selectorChangeAction: (Boolean) -> Unit = {}
) {

    val keyboard = LocalSoftwareKeyboardController.current
    Card(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .then(modifier),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(width = 1.dp, color = Gray),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().background(AlmostWhite),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                modifier = Modifier
                    .padding(start = spacing.medium)
                    .size(28.dp),
                painter = painterResource(id = android.R.drawable.ic_menu_search),
                contentDescription = null,
            )

            TextField(
                modifier = Modifier.weight(1f),
                value = input,
                onValueChange = output,
                shape = RoundedCornerShape(ZeroCornerSize),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Transparent,
                    unfocusedIndicatorColor = Transparent,
                    containerColor = AlmostWhite
                ),
                maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    keyboard?.hide()
                })
            )

            AnimatedVisibility(visible = input.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                        contentDescription = null,
                    )
                }
            }

            if (showSelector) {
                Switch(
                    modifier = Modifier.padding(end = spacing.medium),
                    checked = selectorState,
                    onCheckedChange = { selectorChangeAction(it) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun EmptyPreview() {
    SearchBar(input = "", output = {}, onClear = {})
}

@Preview
@Composable
private fun FillerOutPreview() {
    SearchBar(input = "test tag search performed", output = {}, onClear = {})
}

@Preview
@Composable
private fun FillerOutWithSelectorPreview() {
    SearchBar(input = "test tag search performed", output = {}, onClear = {}, showSelector = true)
}