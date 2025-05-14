package com.example.bookmanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanager.R
import com.example.bookmanager.databinding.BookItemBinding
import com.example.bookmanager.model.Book

class BookAdapter(
    private val context: Context,
    private val books: List<Book>,
    private val onEditBook: (Book) -> Unit,
    private val onDeleteBook: (Book) -> Unit,
    private val onItemClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    inner class BookViewHolder(private val binding: BookItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            binding.titleTextView.text = book.title
            binding.authorTextView.text = book.author
            binding.publisherTextView.text = book.publisher

            // Handle item click
            binding.root.setOnClickListener {
                onItemClick(book)
            }

            // Handle long click for edit/delete
            binding.root.setOnLongClickListener {
                val menu = PopupMenu(context, binding.root)
                val inflater: MenuInflater = menu.menuInflater
                inflater.inflate(R.menu.menu_context, menu.menu)

                menu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.edit_book -> {
                            onEditBook(book)
                            true
                        }
                        R.id.delete_book -> {
                            onDeleteBook(book)
                            true
                        }
                        else -> false
                    }
                }
                menu.show()
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = BookItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int = books.size
}