package com.example.bookmanager

import android.os.Bundle
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
import com.example.bookmanager.model.Book

class MainActivity : AppCompatActivity() {

    private lateinit var books: MutableList<Book>
    private lateinit var adapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        books = mutableListOf(
            Book(
                1,
                "Matar a un ruiseñor",
                "Harper Lee",
                "J.B. Lippincott & Co.",
                "Ficción",
                1960,
            ),
            Book(
                2,
                "1984",
                "George Orwell",
                "Secker & Warburg",
                "Distopía",
                1949,
            ),
            Book(
                3,
                "El gran Gatsbyqq",
                "F. Scott Fitzgerald",
                "Charles Scribner's Sons",
                "Clásico",
                1925,
            ),
            Book(
                4,
                "Orgullo y prejuicio",
                "Jane Austen",
                "T. Egerton",
                "Romance",
                1813,
            ),
            Book(
                5,
                "Moby-Dick",
                "Herman Melville",
                "Harper & Brothers",
                "Aventura",
                1851,
            )
        )

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
                    val newBook = Book(
                        id = System.currentTimeMillis(),
                        title = title,
                        author = author,
                        publisher = publisher,
                        genre = genre,
                        year = year,
                    )

                    books.add(newBook)
                    adapter.notifyItemInserted(books.size - 1)

                    Toast.makeText(this, "Libro agregado: $title", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
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
                    book.title = updatedTitle
                    book.author = updatedAuthor
                    book.publisher = updatedPublisher
                    book.genre = updatedGenre
                    book.year = updatedYear

                    val position = books.indexOf(book)
                    adapter.notifyItemChanged(position)

                    Toast.makeText(this, "Libro actualizado: $updatedTitle", Toast.LENGTH_SHORT)
                        .show()
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    private fun handleItemClick(book: Book) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_book_details, null)

        // Bind the book details to the dialog's TextViews
        dialogView.findViewById<TextView>(R.id.titleTextView).text = book.title
        dialogView.findViewById<TextView>(R.id.authorTextView).text = book.author
        dialogView.findViewById<TextView>(R.id.publisherTextView).text = book.publisher
        dialogView.findViewById<TextView>(R.id.genreTextView).text = book.genre
        dialogView.findViewById<TextView>(R.id.yearTextView).text = book.year.toString()

        // Create and show the dialog
        AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Detalles del Libro")
            .setPositiveButton("Cerrar", null)
            .create()
            .show()
    }

    private fun handleDeleteBook(book: Book) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Eliminar Libro")
            .setMessage("¿Está seguro de que desea eliminar ${book.title}?")
            .setPositiveButton("Eliminar") { _, _ ->
                val position = books.indexOf(book)
                books.remove(book)
                adapter.notifyItemRemoved(position)
                Toast.makeText(this, "${book.title} eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }
}