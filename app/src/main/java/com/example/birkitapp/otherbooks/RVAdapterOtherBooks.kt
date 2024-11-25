package com.example.birkitapp.otherbooks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.birkitapp.R
import com.example.birkitapp.home.BooksModel
import com.example.birkitapp.utils.Utils
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class RVAdapterOtherBooks(
    private val context: Context,
    private val booksList: ArrayList<BooksModel>,
) : RecyclerView.Adapter<RVAdapterOtherBooks.OtherBooksHolder>() {

    class OtherBooksHolder(view: View): RecyclerView.ViewHolder(view) {
        val userEmail: TextView = view.findViewById(R.id.otherUserEmail)
        val otherBookName: TextView = view.findViewById(R.id.otherBookName)
        val otherProgressBar: ProgressBar = view.findViewById(R.id.otherProgressBar)
        val otherBookImage: ImageView = view.findViewById(R.id.otherBookImage)
        val otherConstraints: ConstraintLayout = view.findViewById(R.id.otherConstraint)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherBooksHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.other_card, parent, false)
        return OtherBooksHolder(view)
    }

    override fun getItemCount(): Int {
        return booksList.size
    }

    override fun onBindViewHolder(holder: OtherBooksHolder, position: Int) {
        val book = booksList[position]

        holder.otherConstraints.visibility = View.INVISIBLE

        Picasso.get()
            .load(book.bookUrl)
            .resize(450, 500)
            .into(holder.otherBookImage, object : Callback {
                override fun onSuccess() {
                    holder.otherConstraints.visibility = View.VISIBLE
                }

                override fun onError(e: Exception?) {
                    Utils.toastMessage(context, e?.localizedMessage ?: "Resim yüklenemedi.")
                }
            })

        holder.userEmail.text = book.userEmail
        holder.otherBookName.text = book.bookName

        holder.otherProgressBar.max = book.bookPageCount.toIntOrNull() ?: 0
        holder.otherProgressBar.progress = book.bookReadPage.toIntOrNull() ?: 0
    }
}