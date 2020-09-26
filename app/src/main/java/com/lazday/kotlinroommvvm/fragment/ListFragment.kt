package com.lazday.kotlinroommvvm.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lazday.kotlinroommvvm.R
import com.lazday.kotlinroommvvm.activity.NoteAdapter
import com.lazday.kotlinroommvvm.room.Constant
import com.lazday.kotlinroommvvm.room.Note
import com.lazday.kotlinroommvvm.room.NoteDB
import kotlinx.android.synthetic.main.fragment_list.button_create
import kotlinx.android.synthetic.main.fragment_list.list_note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListFragment : Fragment() {

    private val db by lazy { NoteDB(requireContext()) }
    lateinit var noteAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupListener()
        setupRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    private fun loadData(){
        CoroutineScope(Dispatchers.IO).launch {
            noteAdapter.setData(db.noteDao().getNotes())
            withContext(Dispatchers.Main) {
                noteAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun setupView (){
        (requireActivity() as BaseFragmentActivity).supportActionBar!!.title = "Catatan"
    }

    private fun setupListener(){
        button_create.setOnClickListener {
            navigationEdit(Constant.TYPE_CREATE, 0)
        }
    }

    private fun setupRecyclerView () {

        noteAdapter = NoteAdapter(
            arrayListOf(),
            object : NoteAdapter.OnAdapterListener {
                override fun onClick(note: Note) {
                    navigationEdit(Constant.TYPE_READ, note.id)
                }

                override fun onUpdate(note: Note) {
                    navigationEdit(Constant.TYPE_UPDATE, note.id)
                }

                override fun onDelete(note: Note) {
                    deleteAlert(note)
                }

            })

        list_note.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = noteAdapter
        }

    }

    private fun navigationEdit(navigation_type: Int, note_id: Int){
        val bundle = bundleOf("navigation_type" to navigation_type, "note_id" to note_id )
        findNavController().navigate(R.id.action_listFragment_to_editFragment, bundle)
    }

    private fun deleteAlert(note: Note){
        val dialog = AlertDialog.Builder(requireContext())
        dialog.apply {
            setTitle("Konfirmasi Hapus")
            setMessage("Yakin hapus ${note.title}?")
            setNegativeButton("Batal") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            setPositiveButton("Hapus") { dialogInterface, i ->
                CoroutineScope(Dispatchers.IO).launch {
                    db.noteDao().deleteNote(note)
                    dialogInterface.dismiss()
                    loadData()
                }
            }
        }

        dialog.show()
    }
}