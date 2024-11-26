package com.example.birkitapp.utils

import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.birkitapp.home.BooksModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object Utils {

    fun getBooks(
        auth: FirebaseAuth,
        db: FirebaseFirestore,
        context: Context,
        filterByUser: Boolean = true,
        booksList: MutableList<BooksModel>,
        adapter: RecyclerView.Adapter<*>?
    ) {
        val query = if (filterByUser && auth.currentUser?.email != null) {
            db.collection("Books").whereEqualTo("user_email", auth.currentUser!!.email)
        } else {
            db.collection("Books")
        }

        query.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                toastMessage(context, exception.localizedMessage)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                booksList.clear()
                for (book in snapshot.documents) {
                    val bookId = book.id
                    val bookName = book.get("book_name") as String
                    val bookPageCount = book.get("book_page_count") as String
                    val bookUrl = book.get("book_url") as String
                    val userEmail = book.get("user_email") as String
                    val bookReadPage = book.get("book_read_page") as String
                    val uuid = book.get("uuid") as String

                    val booksModel = BooksModel(
                        bookName,
                        bookPageCount,
                        bookUrl,
                        userEmail,
                        bookReadPage,
                        bookId,
                        uuid
                    )
                    booksList.add(booksModel)
                }
                adapter?.notifyDataSetChanged()
            } else {
                booksList.clear()
                adapter?.notifyDataSetChanged()
            }
        }
    }

    fun toastMessage(context: Context, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
