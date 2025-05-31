package com.example.bookmanager.contract

import android.provider.BaseColumns

object BookContract {
    object BookEntry : BaseColumns {
        const val TABLE_NAME = "books"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_AUTHOR = "author"
        const val COLUMN_NAME_PUBLISHER = "publisher"
        const val COLUMN_NAME_GENRE = "genre"
        const val COLUMN_NAME_YEAR = "year"
    }
}