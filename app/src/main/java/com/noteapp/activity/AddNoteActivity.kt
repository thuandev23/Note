package com.noteapp.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.noteapp.R
import com.noteapp.databinding.ActivityAddNoteBinding
import com.noteapp.models.Note
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Math.random
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var note: Note
    private lateinit var oldNote: Note
    private var isUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Handle the back button
        binding.imgBack.setOnClickListener {
            finish()
        }

        // Voice to text
        binding.speechVoice.setOnClickListener {
            askSpeechInput()
        }

        // Check if it is an update operation
        try {
            intent.getSerializableExtra("current_note")?.let {
                oldNote = it as Note
                binding.etTitle.setText(oldNote.title)
                binding.etDescription.setText(oldNote.description)
                isUpdate = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("TAG", "onCreate: ${e.message}")
        }
    }

    private fun askSpeechInput() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) { // Kiểm tra xem thiết bị có hỗ trợ speech recognition không
            Toast.makeText(this, "Speech recognition is not available", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something for description note")
            startActivityForResult(intent, 1)
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            binding.etDescription.setText(result?.get(0))
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.pop_up_menu, menu)
        // Chỉ hiện delete icon khi update
        menu?.findItem(R.id.delete)?.isVisible = isUpdate
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> {
                showDeleteConfirmationDialog()
                true
            }
            R.id.edit -> {
                handleEditNote()
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleEditNote(): Boolean {
        if (binding.etTitle.text.toString().isNotEmpty() && binding.etDescription.text.toString()
                .isNotEmpty()
        ) {
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            if (isUpdate) {
                note = Note(
                    oldNote.id,
                    binding.etTitle.text.toString(),
                    binding.etDescription.text.toString(),
                    formatter.format(Date())
                )
            } else {
                note = Note(
                    random().toInt(),
                    binding.etTitle.text.toString(),
                    binding.etDescription.text.toString(),
                    formatter.format(Date())
                )

            }
            val intent = Intent()
            intent.putExtra("note", note)
            setResult(RESULT_OK, intent)
            finish()
        } else {
            if (binding.etTitle.text.toString().isEmpty()) {
                binding.etTitle.error = "Title is required"
            }
            if (binding.etDescription.text.toString().isEmpty()) {
                binding.etDescription.error = "Description is required"
            }
        }
        return true
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Yes") { _, _ ->
                val intent = Intent().apply {
                    putExtra("note", oldNote)
                    putExtra("isDelete", true)
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }


}
