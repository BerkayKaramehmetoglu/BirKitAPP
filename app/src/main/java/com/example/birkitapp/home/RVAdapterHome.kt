package com.example.birkitapp.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.birkitapp.R
import com.example.birkitapp.utils.Utils
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class RVAdapterHome(
    private val context: Context,
    private val booksList: ArrayList<BooksModel>,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : RecyclerView.Adapter<RVAdapterHome.BooksViewHolder>() {

    class BooksViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookName: TextView = view.findViewById(R.id.cardBookName)
        val bookImage: ImageView = view.findViewById(R.id.cardBookImage)
        val bookProgress: ProgressBar = view.findViewById(R.id.cardBookProgress)
        val deleteImageBtn: ImageButton = view.findViewById(R.id.cardDelete)
        val editImageBtn: ImageButton = view.findViewById(R.id.cardEdit)
        val bookCardConstraint: ConstraintLayout = view.findViewById(R.id.bookCardConstraint)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.book_card, parent, false)
        return BooksViewHolder(view)
    }

    override fun getItemCount(): Int = booksList.size

    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
        val book = booksList[position]

        holder.bookCardConstraint.visibility = View.INVISIBLE
        Picasso.get().load(book.bookUrl).resize(450, 500)
            .into(holder.bookImage, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    holder.bookCardConstraint.visibility = View.VISIBLE
                }

                override fun onError(e: Exception?) {
                    Utils.toastMessage(context, "Görsel yüklenirken bir hata oluştu.")
                }
            })

        holder.deleteImageBtn.setOnClickListener {
            AlertDialog.Builder(context).apply {
                setTitle("Kitabı Sil")
                setMessage("${book.bookName} adlı kitabı silmek istediğinizden emin misiniz?")
                setPositiveButton("Evet") { _, _ ->
                    deleteBook(book, position)
                }
                setNegativeButton("Hayır", null)
            }.show()
        }

        holder.editImageBtn.setOnClickListener {
            showEditBookDialog(book, position)
        }

        holder.bookProgress.max = book.bookPageCount.toIntOrNull() ?: 0
        holder.bookProgress.progress = book.bookReadPage.toIntOrNull() ?: 0
        holder.bookName.text = book.bookName
    }

    private fun deleteBook(book: BooksModel, position: Int) {
        db.collection("Books").document(book.bookId)
            .delete()
            .addOnSuccessListener {
                if (position in booksList.indices) {
                    booksList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, booksList.size)
                }

                storage.reference.child(book.uuID).delete()
                    .addOnSuccessListener {
                        Utils.toastMessage(context, "Kitap başarıyla silindi.")
                    }
                    .addOnFailureListener {
                        Utils.toastMessage(
                            context,
                            "Görsel silinirken bir hata oluştu: ${it.localizedMessage}"
                        )
                    }
            }
            .addOnFailureListener {
                Utils.toastMessage(
                    context,
                    "Kitap silinirken bir hata oluştu: ${it.localizedMessage}"
                )
            }
    }

    private fun showEditBookDialog(book: BooksModel, position: Int) {
        val alertDesign = LayoutInflater.from(context).inflate(R.layout.edit_alet_view, null)
        val editBookName: EditText = alertDesign.findViewById(R.id.editBookName)
        val editBookPageCount: EditText = alertDesign.findViewById(R.id.editBookPageCount)
        val editBookReadPageCount: EditText = alertDesign.findViewById(R.id.editBookReadCount)

        editBookName.setText(book.bookName)
        editBookPageCount.setText(book.bookPageCount)
        editBookReadPageCount.setText(book.bookReadPage)

        AlertDialog.Builder(context).apply {
            setIcon(R.drawable.baseline_bookmark_24)
            setTitle("${book.bookName} Güncelle")
            setView(alertDesign)

            setPositiveButton("Güncelle") { _, _ ->
                val updatedBookName = editBookName.text.toString()
                val updatedPageCount = editBookPageCount.text.toString()
                val updatedReadPageCount = editBookReadPageCount.text.toString()

                if (updatedBookName.isEmpty() || updatedPageCount.isEmpty() || updatedReadPageCount.isEmpty()) {
                    Utils.toastMessage(context, "Tüm alanlar doldurulmalıdır!")
                    return@setPositiveButton
                }

                val pageCount = updatedPageCount.toIntOrNull() ?: 0
                val readPageCount = updatedReadPageCount.toIntOrNull() ?: 0

                if (readPageCount > pageCount) {
                    Utils.toastMessage(
                        context,
                        "Okunan sayfa sayısı, kitap sayfa sayısını geçemez!"
                    )
                    return@setPositiveButton
                }

                val updates = mapOf(
                    "book_name" to updatedBookName,
                    "book_page_count" to updatedPageCount,
                    "book_read_page" to updatedReadPageCount
                )

                db.collection("Books").document(book.bookId)
                    .update(updates)
                    .addOnSuccessListener {
                        book.bookName = updatedBookName
                        book.bookPageCount = updatedPageCount
                        book.bookReadPage = updatedReadPageCount

                        notifyItemChanged(position)
                        Utils.toastMessage(context, "${book.bookName} başarıyla güncellendi.")
                    }
                    .addOnFailureListener {
                        Utils.toastMessage(context, "Güncelleme başarısız: ${it.localizedMessage}")
                    }
            }

            setNegativeButton("İptal", null)
        }.show()
    }
}