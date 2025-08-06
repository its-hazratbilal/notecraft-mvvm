package com.hazratbilal.notecraft.ui.notes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hazratbilal.notecraft.databinding.NoteItemBinding
import com.hazratbilal.notecraft.model.NotesResponse


class NotesAdapter(
    private val deleteNote: (NotesResponse.Note) -> Unit,
    private val editNote: (NotesResponse.Note) -> Unit
) : ListAdapter<NotesResponse.Note, NotesAdapter.NoteViewHolder>(ComparatorDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note)
    }

    inner class NoteViewHolder(private val binding: NoteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: NotesResponse.Note) {
            binding.title.text = note.title
            binding.description.text = note.description
            binding.date.text = note.created_at
            binding.delete.setOnClickListener {
                deleteNote(note)
            }
            binding.edit.setOnClickListener {
                editNote(note)
            }
        }
    }

    class ComparatorDiffUtil : DiffUtil.ItemCallback<NotesResponse.Note>() {
        override fun areItemsTheSame(oldItem: NotesResponse.Note, newItem: NotesResponse.Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NotesResponse.Note, newItem: NotesResponse.Note): Boolean {
            return oldItem.title == newItem.title &&
                    oldItem.description == newItem.description
        }
    }
}