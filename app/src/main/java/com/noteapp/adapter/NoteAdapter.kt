package com.noteapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.noteapp.R
import com.noteapp.databinding.ActivityAddNoteBinding
import com.noteapp.databinding.ListItemBinding
import com.noteapp.models.Note
import kotlin.random.Random

class NoteAdapter(private val context: Context, private val listener: NoteItemClickListener): RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(){
    private val notes = mutableListOf<Note>()
    private val fullNotes = mutableListOf<Note>()

    inner class NoteViewHolder(private val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int){
            binding.textViewTitle.text = notes[position].title
            binding.textViewDescription.text = notes[position].description
            binding.textViewDate.text = notes[position].date

            binding.textViewTitle.isSelected = true
            binding.textViewDescription.isSelected = true
            binding.textViewDate.isSelected = true
            binding.layoutCardView
                .setCardBackgroundColor(binding.layoutCardView.resources.getColor(randomColor(), null))
            binding.layoutCardView.setOnClickListener {
                listener.onItemClicked(notes[adapterPosition])
            }
            binding.layoutCardView.setOnLongClickListener {
                listener.onLongItemClicked(notes[adapterPosition], binding.layoutCardView)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
       val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(position)

    }
    fun randomColor(): Int {
        val list = ArrayList<Int>()
        list.add(R.color.red)
        list.add(R.color.green)
        list.add(R.color.blue)
        list.add(R.color.yellow)
        list.add(R.color.cyan)
        list.add(R.color.magenta)
        list.add(R.color.gray)
        list.add(R.color.lightGray)
        list.add(R.color.darkGray)
        list.add(R.color.purple)
        list.add(R.color.orange)
        val seed = System.currentTimeMillis().toInt()
        val random = Random(seed).nextInt(list.size)
        return list[random]
    }
    fun updateList(newList: List<Note>){
        notes.clear()
        notes.addAll(newList)
        fullNotes.clear()
        fullNotes.addAll(newList)
        notifyDataSetChanged()

    }
    fun searchList(search: String){
        notes.clear()
        fullNotes.forEach {
            if(it.title!!.lowercase().contains(search.lowercase(), true)
                || it.description!!.lowercase().contains(search.lowercase(), true)){
                notes.add(it)
            }
        }
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int = notes.size

    interface NoteItemClickListener{
        fun onItemClicked(note: Note)
        fun onLongItemClicked(note: Note, cardView: View)
    }
}