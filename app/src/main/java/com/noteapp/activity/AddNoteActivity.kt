package com.noteapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.noteapp.R
import com.noteapp.databinding.ActivityAddNoteBinding
import com.noteapp.models.Note
import java.text.SimpleDateFormat
import java.util.Date

class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var note : Note
    private lateinit var oldNote : Note
    private var isUpdate = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            oldNote = intent.getSerializableExtra("current_note") as Note
            binding.etTitle.setText(oldNote.title)
            binding.etDescription.setText(oldNote.description)
            isUpdate = true
        }catch (e:Exception){
            e.printStackTrace()
            Log.d("TAG", "onCreate: ${e.message}")
        }

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.imgCheck.setOnClickListener {
           if(binding.etTitle.text.toString().isNotEmpty() && binding.etDescription.text.toString().isNotEmpty()){
               val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
               if(isUpdate){
                   note = Note(
                       oldNote.id,
                       binding.etTitle.text.toString(),
                       binding.etDescription.text.toString(),
                       formatter.format(Date())
                   )
               }else{
                   note = Note(
                       null,
                       binding.etTitle.text.toString(),
                       binding.etDescription.text.toString(),
                       formatter.format(Date())
                   )
               }
               val intent = Intent()
               intent.putExtra("note", note)
               setResult(Activity.RESULT_OK, intent)
               finish()
           }
            else{
                if(binding.etTitle.text.toString().isEmpty()){
                     binding.etTitle.error = "Title is required"
                }
                if(binding.etDescription.text.toString().isEmpty()){
                     binding.etDescription.error = "Description is required"
                }
           }
        }
    }
}