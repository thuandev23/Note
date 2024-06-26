package com.noteapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.noteapp.adapter.DayAdapter
import com.noteapp.adapter.NoteAdapter
import com.noteapp.database.NoteDatabase
import com.noteapp.databinding.ActivityMainBinding
import com.noteapp.models.Day
import com.noteapp.models.Note
import com.noteapp.viewmodels.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NoteAdapter.NoteItemClickListener{
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<NoteViewModel>()
    lateinit var adapterNote: NoteAdapter
    lateinit var adapterDay: DayAdapter
    private lateinit var database: NoteDatabase
    lateinit var selectedNote: Note

    private val updateNote = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == RESULT_OK){
            val note = result.data?.getSerializableExtra("note") as Note
            val isDelete = result.data?.getBooleanExtra("isDelete", false) ?: false
            if (isDelete){
                viewModel.delete(note)
            }else{
                viewModel.update(note)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnSearch.setOnClickListener {
                searchView.visibility = View.VISIBLE
                btnSearch.visibility = View.GONE
                tvNotes.visibility = View.GONE
                searchView.isIconified = false // Ensure the search view is expanded
                searchView.requestFocus() // Optional: focus on search input
            }
            searchView.setOnCloseListener {
                btnSearch.visibility = View.VISIBLE
                tvNotes.visibility = View.VISIBLE
                searchView.visibility = View.GONE
                false
            }
        }

        initRecyclerviewDay()
        initRecyclerviewNote()
        // hilt understanding the viewModel
        // viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[NoteViewModel::class.java]

        viewModel.allNotes.observe(this ){ list ->
            list?.let {
                if (list.isEmpty()){
                    binding.imgNotNotes.visibility = View.VISIBLE
                    binding.createText.visibility = View.VISIBLE
                    binding.recyclerViewNote.visibility = View.GONE
                }
                else{
                    binding.imgNotNotes.visibility = View.GONE
                    binding.createText.visibility = View.GONE
                    adapterNote.updateList(list)
                    binding.recyclerViewNote.visibility = View.VISIBLE

                }

            }
        }
        database = NoteDatabase.getDatabase(this)
    }

    private fun initRecyclerviewDay() {
        binding.apply {
            recyclerViewDay.setHasFixedSize(true)
            recyclerViewDay.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            //example day
            val exampleDays = listOf(
                Day("Tus", "25", "April"),
                Day("Tus", "25", "April"),
                Day("Tus", "25", "April"),
                Day("Tus", "25", "April"),
                Day("Tus", "25", "April"),
                Day("Tus", "25", "April"),
                Day("Tus", "25", "April"),
                Day("Tus", "25", "April"),
                Day("Tus", "25", "April"),
            )

            adapterDay = DayAdapter(exampleDays)
            recyclerViewDay.adapter = adapterDay
        }
    }

    private fun initRecyclerviewNote() {
        binding.apply {
            recyclerViewNote.setHasFixedSize(true)
            recyclerViewNote.layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
            adapterNote = NoteAdapter(this@MainActivity, this@MainActivity)
            recyclerViewNote.adapter = adapterNote

            val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
                if (result.resultCode == RESULT_OK){
                    val note = result.data?.getSerializableExtra("note") as Note
                    if (note != null){
                        viewModel.insert(note)
                    }
                }
            }

            binding.buttonAddNote.setOnClickListener {
                val intent = Intent(this@MainActivity, AddNoteActivity::class.java)
                getContent.launch(intent)
            }

            binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != null){
                        adapterNote.searchList(newText)
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
    }

}