package com.example.currencyconversion

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class CurrencyItem (
    @DrawableRes
    val flagRes: Int,
    @StringRes
    val code: Int
)