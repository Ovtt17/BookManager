package com.example.bookmanager.model

import android.os.Parcel
import android.os.Parcelable

data class Book(
    var id: Long,
    var title: String,
    var author: String,
    var publisher: String,
    var genre: String,
    var year: Int,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeString(author)
        parcel.writeString(publisher)
        parcel.writeString(genre)
        parcel.writeInt(year)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book = Book(parcel)
        override fun newArray(size: Int): Array<Book?> = arrayOfNulls(size)
    }
}