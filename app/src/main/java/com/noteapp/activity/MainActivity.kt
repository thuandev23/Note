package com.noteapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.noteapp.R
import com.noteapp.adapter.NoteAdapter
import com.noteapp.database.NoteDatabase
import com.noteapp.databinding.ActivityMainBinding
import com.noteapp.models.Note
import com.noteapp.models.NoteViewModel
import com.noteapp.repository.NoteRepository

class MainActivity : AppCompatActivity(), NoteAdapter.NoteItemClickListener, PopupMenu.OnMenuItemClickListener{
    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: NoteViewModel
    lateinit var adapter: NoteAdapter
    private lateinit var database: NoteDatabase
    lateinit var selectedNote: Note

    private val updateNote = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == RESULT_OK){
            val note = result.data?.getSerializableExtra("note") as Note
            if (note != null){
                viewModel.update(note)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()

        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application))[NoteViewModel::class.java]

        viewModel.allNotes.observe(this ){ list ->
            list?.let {
                adapter.updateList(list)
            }
        }
        database = NoteDatabase.getDatabase(this)

    }

    private fun initUI() {
        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
            adapter = NoteAdapter(this@MainActivity, this@MainActivity)
            recyclerView.adapter = adapter

            val getCOntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
                if (result.resultCode == RESULT_OK){
                    val note = result.data?.getSerializableExtra("note") as Note
                    if (note != null){
                        viewModel.insert(note)
                    }
                }
            }

            binding.buttonAddNote.setOnClickListener {
                val intent = Intent(this@MainActivity, AddNoteActivity::class.java)
                getCOntent.launch(intent)
            }

            binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != null){
                        adapter.searchList(newText)
                    }
                    return false
                }
            })
        }
    }

    override fun onItemClicked(note: Note) {
        val intent = Intent(this, AddNoteActivity::class.java)
        intent.putExtra("current_note", note)
        updateNote.launch(intent)
    }

    override fun onLongItemClicked(note: Note, cardView: View) {
        selectedNote = note
        popUpDisplay(cardView)
    }

    private fun popUpDisplay(cardView: View) {
        val popup = PopupMenu(this, cardView)
        popup.inflate(R.menu.pop_up_menu)
        popup.setOnMenuItemClickListener(this)
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.delete){
            viewModel.delete(selectedNote)
            return true
        }
        return false
    }
}