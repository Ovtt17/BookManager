package com.example.bookmanager

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.provider.BaseColumns
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanager.adapter.BookAdapter
import com.example.bookmanager.contract.BookContract.BookEntry
import com.example.bookmanager.db.BookDBHelper
import com.example.bookmanager.model.Book
import android.content.ContentValues

class MainActivity : AppCompatActivity() {

    private lateinit var books: MutableList<Book>
    private lateinit var adapter: BookAdapter
    private lateinit var bookDBHelper: BookDBHelper
    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        bookDBHelper = BookDBHelper(this)
        db = bookDBHelper.writableDatabase

        books = getBooksFromCursor(loadBooks())

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        adapter = BookAdapter(
            this,
            books,
            onEditBook = { book -> handleEditBook(book) },
            onDeleteBook = { book -> handleDeleteBook(book) },
            onItemClick = { book -> handleItemClick(book) }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                handleAddBook()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadBooks(): Cursor {
        val columns = arrayOf(
            BaseColumns._ID,
            BookEntry.COLUMN_NAME_TITLE,
            BookEntry.COLUMN_NAME_AUTHOR,
            BookEntry.COLUMN_NAME_PUBLISHER,
            BookEntry.COLUMN_NAME_GENRE,
            BookEntry.COLUMN_NAME_YEAR
        )
        return db.query(BookEntry.TABLE_NAME, columns, null, null, null, null, null)
    }

    private fun getBooksFromCursor(cursor: Cursor): MutableList<Book> {
        val bookList = mutableListOf<Book>()
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val title = getString(getColumnIndexOrThrow(BookEntry.COLUMN_NAME_TITLE))
                val author = getString(getColumnIndexOrThrow(BookEntry.COLUMN_NAME_AUTHOR))
                val publisher = getString(getColumnIndexOrThrow(BookEntry.COLUMN_NAME_PUBLISHER))
                val genre = getString(getColumnIndexOrThrow(BookEntry.COLUMN_NAME_GENRE))
                val year = getInt(getColumnIndexOrThrow(BookEntry.COLUMN_NAME_YEAR))
                bookList.add(Book(id, title, author, publisher, genre, year))
            }
        }
        cursor.close()
        return bookList
    }

    private fun handleAddBook() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_book_form, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Agregar Libro")
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                val title = dialogView.findViewById<EditText>(R.id.etTitle).text.toString()
                val author = dialogView.findViewById<EditText>(R.id.etAuthor).text.toString()
                val publisher = dialogView.findViewById<EditText>(R.id.etPublisher).text.toString()
                val genre = dialogView.findViewById<EditText>(R.id.etGenre).text.toString()
                val year =
                    dialogView.findViewById<EditText>(R.id.etYear).text.toString().toIntOrNull()

                if (title.isEmpty() || author.isEmpty() || publisher.isEmpty() || genre.isEmpty() || year == null) {
                    Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val values = ContentValues().apply {
                        put(BookEntry.COLUMN_NAME_TITLE, title)
                        put(BookEntry.COLUMN_NAME_AUTHOR, author)
                        put(BookEntry.COLUMN_NAME_PUBLISHER, publisher)
                        put(BookEntry.COLUMN_NAME_GENRE, genre)
                        put(BookEntry.COLUMN_NAME_YEAR, year)
                    }

                    val newRowId = db.insert(BookEntry.TABLE_NAME, null, values)
                    if (newRowId != -1L) {
                        val newBook = getBooksFromCursor(loadBooks()).last()
                        books.add(newBook)
                        adapter.notifyItemInserted(books.lastIndex)
                        Toast.makeText(this, "Libro agregado: $title", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(
                            this,
                            "Error al guardar en la base de datos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        dialog.show()
    }

    private fun handleEditBook(book: Book) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_book_form, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Editar Libro")
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.setOnShowListener {
            val etTitle = dialogView.findViewById<EditText>(R.id.etTitle)
            val etAuthor = dialogView.findViewById<EditText>(R.id.etAuthor)
            val etPublisher = dialogView.findViewById<EditText>(R.id.etPublisher)
            val etGenre = dialogView.findViewById<EditText>(R.id.etGenre)
            val etYear = dialogView.findViewById<EditText>(R.id.etYear)

            etTitle.setText(book.title)
            etAuthor.setText(book.author)
            etPublisher.setText(book.publisher)
            etGenre.setText(book.genre)
            etYear.setText(book.year.toString())

            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                val updatedTitle = etTitle.text.toString()
                val updatedAuthor = etAuthor.text.toString()
                val updatedPublisher = etPublisher.text.toString()
                val updatedGenre = etGenre.text.toString()
                val updatedYear = etYear.text.toString().toIntOrNull()

                if (updatedTitle.isEmpty() || updatedAuthor.isEmpty() || updatedPublisher.isEmpty() || updatedGenre.isEmpty() || updatedYear == null) {
                    Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val values = ContentValues().apply {
                        put(BookEntry.COLUMN_NAME_TITLE, updatedTitle)
                        put(BookEntry.COLUMN_NAME_AUTHOR, updatedAuthor)
                        put(BookEntry.COLUMN_NAME_PUBLISHER, updatedPublisher)
                        put(BookEntry.COLUMN_NAME_GENRE, updatedGenre)
                        put(BookEntry.COLUMN_NAME_YEAR, updatedYear)
                    }

                    val updatedRows = db.update(
                        BookEntry.TABLE_NAME,
                        values,
                        "${BaseColumns._ID} = ?",
                        arrayOf(book.id.toString())
                    )

                    if (updatedRows > 0) {
                        val index = books.indexOfFirst { it.id == book.id }
                        if (index != -1) {
                            val updatedBook = getBooksFromCursor(loadBooks()).first { it.id == book.id }
                            books[index] = updatedBook
                            adapter.notifyItemChanged(index)
                        }
                        Toast.makeText(this, "Libro actualizado: $updatedTitle", Toast.LENGTH_SHORT)
                            .show()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this, "Error al actualizar el libro", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        dialog.show()
    }

    private fun handleDeleteBook(book: Book) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Eliminar Libro")
            .setMessage("¿Está seguro de que desea eliminar ${book.title}?")
            .setPositiveButton("Eliminar") { _, _ ->
                val deletedRows = db.delete(
                    BookEntry.TABLE_NAME,
                    "${BaseColumns._ID} = ?",
                    arrayOf(book.id.toString())
                )

                if (deletedRows > 0) {
                    val index = books.indexOfFirst { it.id == book.id }
                    if (index != -1) {
                        books.removeAt(index)
                        adapter.notifyItemRemoved(index)
                    }
                    Toast.makeText(this, "${book.title} eliminado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al eliminar el libro", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private fun handleItemClick(book: Book) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_book_details, null)
        dialogView.findViewById<TextView>(R.id.titleTextView).text = book.title
        dialogView.findViewById<TextView>(R.id.authorTextView).text = book.author
        dialogView.findViewById<TextView>(R.id.publisherTextView).text = book.publisher
        dialogView.findViewById<TextView>(R.id.genreTextView).text = book.genre
        dialogView.findViewById<TextView>(R.id.yearTextView).text = book.year.toString()

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Detalles del Libro")
            .setPositiveButton("Cerrar", null)
            .create()
            .show()
    }

    override fun onDestroy() {
        bookDBHelper.close()
        super.onDestroy()
    }
}
