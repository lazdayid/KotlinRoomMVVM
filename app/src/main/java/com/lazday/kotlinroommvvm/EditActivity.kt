package com.lazday.kotlinroommvvm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.lazday.kotlinroommvvm.room.Note
import com.lazday.kotlinroommvvm.room.NoteDB
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditActivity : AppCompatActivity() {

    private val db by lazy { NoteDB(this) }
    private var noteId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setupView()
        setupLstener()
    }

    private fun setupView(){
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        when (intentType()) {
            Constant.TYPE_CREATE -> {
                supportActionBar!!.title = "BUAT BARU"
                button_save.visibility = View.VISIBLE
                button_update.visibility = View.GONE
            }
            Constant.TYPE_READ -> {
                supportActionBar!!.title = "BACA"
                button_save.visibility = View.GONE
                button_update.visibility = View.GONE
                setNote()
            }
            Constant.TYPE_UPDATE -> {
                supportActionBar!!.title = "EDIT"
                button_save.visibility = View.GONE
                button_update.visibility = View.VISIBLE
                setNote()
            }
        }
    }

    private fun setupLstener(){
        button_save.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.noteDao().addNote(
                    Note(
                        0,
                        edit_title.text.toString(),
                        edit_note.text.toString()
                    )
                )
                finish()
            }
        }
        button_update.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.noteDao().updateNote(
                    Note(
                        noteId,
                        edit_title.text.toString(),
                        edit_note.text.toString()
                    )
                )
                finish()
            }
        }
    }

    private fun setNote(){
        noteId = intent.getIntExtra("note_id", 0)
        CoroutineScope(Dispatchers.IO).launch {
            val notes = db.noteDao().getNote(noteId).get(0)
            edit_title.setText( notes.title )
            edit_note.setText( notes.note )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    fun intentType(): Int {
        return intent.getIntExtra("intent_type", 0)
    }
}