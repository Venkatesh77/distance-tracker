package com.distance.tracker.Notes.ListNotes


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.distance.tracker.Notes.Adapter.NotesAdapter
import com.distance.tracker.Notes.Model.Note
import com.distance.tracker.R
import com.distance.tracker.Utils.OnItemClickListener
import com.distance.tracker.Utils.ViewState
import com.distance.tracker.databinding.FragmentListNotesBinding


class ListNotesFragment : Fragment() {
    private lateinit var viewBinding: FragmentListNotesBinding
    private val tag = "ListNotesFragment"
    private val listNotesFragmentViewModel: ListNotesFragmentViewModel by viewModels()
    private var notesList = ArrayList<Note>()
    private val adapter : NotesAdapter = NotesAdapter(notesList)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewBinding = FragmentListNotesBinding.inflate(layoutInflater)
        return viewBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listNotesFragmentViewModel.notes.observe(viewLifecycleOwner){
            render(it)
        }
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
        viewBinding.recyclerView.adapter = adapter
        setOnClickListener()
    }

    private fun setOnClickListener(){
        viewBinding.btnAddNotes.setOnClickListener {
            findNavController().navigate(ListNotesFragmentDirections.actionListFragmentToAddOrUpdateNoteFragment(null))
        }

        adapter.setOnItemClickListener(object : OnItemClickListener{
            override fun onItemClick(position: Int) {
                findNavController().navigate(ListNotesFragmentDirections.actionListFragmentToAddOrUpdateNoteFragment(notesList[position]))
            }
        })
    }

    // noinspection NotifyDataSetChanged
    private fun render(state: ViewState<List<Note>>){
        when(state){
            is ViewState.Success -> {
                viewBinding.loader.hide()
                notesList.clear()
                notesList.addAll(state.data as ArrayList<Note>)
                adapter.notifyDataSetChanged()
            }
            is ViewState.Failure -> {
                viewBinding.loader.hide()
                Toast.makeText(requireContext(), getText(R.string.notes), Toast.LENGTH_SHORT).show()
            }
            is ViewState.Loading -> {
                viewBinding.loader.show()
            }
        }
    }
}