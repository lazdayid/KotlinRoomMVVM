package com.lazday.kotlinroommvvm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.lazday.kotlinroommvvm.room.Note
import com.lazday.kotlinroommvvm.room.NoteDB
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    val db by lazy { NoteDB(this) }
    lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        setupListener()
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(GlobalScope.coroutineContext).launch {
            noteAdapter.setData(db.noteDao().getNotes())
        }
    }

    fun setupView (){
        supportActionBar!!.apply {
            title = "Catatan"
        }
    }

    fun setupListener(){
        button_create.setOnClickListener {
            intentEdit(Constant.TYPE_CREATE, 0)
        }
    }

    fun setupRecyclerView () {

        noteAdapter = NoteAdapter(arrayListOf(), object : NoteAdapter.OnAdapterListener {
            override fun onClick(note: Note) {
                intentEdit( Constant.TYPE_READ, note.id )
            }

            override fun onUpdate(note: Note) {
                intentEdit( Constant.TYPE_UPDATE, note.id )
            }

            override fun onDelete(note: Note) {
                deleteAlert(note)
            }

        })

        list_note.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = noteAdapter
        }

    }

    fun intentEdit(intent_type: Int, note_id: Int) {
        startActivity(
            Intent(this, EditActivity::class.java)
                .putExtra("intent_type", intent_type)
                .putExtra("note_id", note_id)
        )

    }

    fun deleteAlert(note: Note){
        val dialog = AlertDialog.Builder(this)
        dialog.apply {
            title = "Konfirmasi Hapus"
            setMessage("Hapus ${note.title}?")
            setPositiveButton("Hapus") { dialogInterface, i ->
                CoroutineScope(GlobalScope.coroutineContext).launch {
                    db.noteDao().deleteNote(note)
                    finish()
                }
                dialogInterface.dismiss()
            }
            setNegativeButton("Batal") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
        }

        dialog.show()
    }
}