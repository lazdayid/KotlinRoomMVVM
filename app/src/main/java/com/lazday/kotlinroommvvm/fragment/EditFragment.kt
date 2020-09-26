package com.lazday.kotlinroommvvm.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lazday.kotlinroommvvm.R
import com.lazday.kotlinroommvvm.room.Constant
import com.lazday.kotlinroommvvm.room.Note
import com.lazday.kotlinroommvvm.room.NoteDB
import kotlinx.android.synthetic.main.fragment_edit.button_save
import kotlinx.android.synthetic.main.fragment_edit.button_update
import kotlinx.android.synthetic.main.fragment_edit.edit_note
import kotlinx.android.synthetic.main.fragment_edit.edit_title
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditFragment : Fragment() {

    private val db by lazy { NoteDB(requireContext()) }
    private var noteId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupListener()
//        val args = arguments?.getString("amount")
//        Log.d("arguments", args!! )
    }

    private fun setupView(){
        Log.d("EditFragment", "navigationType: ${navigationType()}" )
        val actionBar = (requireActivity() as BaseFragmentActivity).supportActionBar!!
        when (navigationType()) {
            Constant.TYPE_CREATE -> {
                actionBar.title = "BUAT BARU"
                button_save.visibility = View.VISIBLE
                button_update.visibility = View.GONE
            }
            Constant.TYPE_READ -> {
                actionBar.title = "BACA"
                button_save.visibility = View.GONE
                button_update.visibility = View.GONE
                getNote()
            }
            Constant.TYPE_UPDATE -> {
                actionBar.title = "EDIT"
                button_save.visibility = View.GONE
                button_update.visibility = View.VISIBLE
                getNote()
            }
        }
    }

    private fun setupListener(){
        button_save.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.noteDao().addNote(
                    Note(
                        0,
                        edit_title.text.toString(),
                        edit_note.text.toString()
                    )
                )
                requireActivity().onBackPressed()
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
                requireActivity().onBackPressed()
            }
        }
    }

    private fun getNote(){
        noteId = requireArguments().getInt("note_id")
        CoroutineScope(Dispatchers.IO).launch {
            val notes = db.noteDao().getNote(noteId).get(0)
            withContext(Dispatchers.Main) {
                edit_title.setText( notes.title )
                edit_note.setText( notes.note )
            }
        }
    }

    private fun navigationType(): Int {
        return requireArguments().getInt("navigation_type")
    }
}