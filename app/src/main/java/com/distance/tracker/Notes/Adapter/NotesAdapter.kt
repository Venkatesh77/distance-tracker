package com.distance.tracker.Notes.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.distance.tracker.Notes.Model.Note
import com.distance.tracker.R
import com.distance.tracker.Utils.OnItemClickListener

class NotesAdapter (private val mNotes: List<Note>) : RecyclerView.Adapter<NotesAdapter.ViewHolder>()
{
    private lateinit var mListener: OnItemClickListener
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(itemView: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val tvNote: TextView = itemView.findViewById(R.id.tv_note)
        val cvNote: CardView = itemView.findViewById(R.id.cv_note)

        init {
            cvNote.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.note_row, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView, mListener)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: NotesAdapter.ViewHolder, position: Int) {
        // Get the data model based on position
        val note: Note = mNotes[position]
        // Set item views based on your views and data model
        val textView = viewHolder.tvNote
        textView.text = note.message
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mNotes.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

}