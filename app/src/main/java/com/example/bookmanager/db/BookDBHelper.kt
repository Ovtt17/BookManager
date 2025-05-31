package com.example.bookmanager.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.example.bookmanager.contract.BookContract.BookEntry

class BookDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "BookManager.db"

        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${BookEntry.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "${BookEntry.COLUMN_NAME_TITLE} TEXT," +
                    "${BookEntry.COLUMN_NAME_AUTHOR} TEXT," +
                    "${BookEntry.COLUMN_NAME_PUBLISHER} TEXT," +
                    "${BookEntry.COLUMN_NAME_GENRE} TEXT," +
                    "${BookEntry.COLUMN_NAME_YEAR} INTEGER)"

        private const val SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS ${BookEntry.TABLE_NAME}"
    }
}