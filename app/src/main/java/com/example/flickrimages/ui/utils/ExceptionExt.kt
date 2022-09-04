package com.example.flickrimages.ui.utils

fun Exception.toUserError() = UserError(message ?: cause.toString())