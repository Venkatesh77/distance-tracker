package com.distance.tracker.Notes.AddNotes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.distance.tracker.Notes.Model.Note
import com.distance.tracker.R
import com.distance.tracker.Utils.Constants
import com.distance.tracker.Utils.ViewState
import com.distance.tracker.databinding.FragmentAddOrUpdateNotesBinding

class AddOrUpdateNotesFragment : Fragment() {

    private lateinit var viewBinding: FragmentAddOrUpdateNotesBinding
    private val addOrUpdateNotesFragmentViewModel: AddOrUpdatesNotesFragmentViewModel by viewModels()
    private val args : AddOrUpdateNotesFragmentArgs by navArgs()
    private var noteArg: Note? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewBinding = FragmentAddOrUpdateNotesBinding.inflate(inflater)
        noteArg = args.note
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.tvTitle.text = if(noteArg == null) getText(R.string.add_notes) else getText(R.string.update_notes)
        addOrUpdateNotesFragmentViewModel.notes.observe(viewLifecycleOwner){
            render(it)
        }
        noteArg?.let {
            viewBinding.etNotes.setText(it.message)
        }
       setOnClickListener()
    }

    private fun setOnClickListener(){
        viewBinding.btnSave.setOnClickListener {
            if (viewBinding.etNotes.text.isNotEmpty()){
                if (noteArg == null)
                    addOrUpdateNotesFragmentViewModel.addNoteToFirebase(viewBinding.etNotes.text.toString())
                else
                    addOrUpdateNotesFragmentViewModel.updateNoteToFirebase(viewBinding.etNotes.text.toString(), noteArg!!)
            }else{
                Toast.makeText(requireContext(), getText(R.string.add_notes_empty_text), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // noinspection NotifyDataSetChanged
    private fun render(state: ViewState<String>){
        when(state){
            is ViewState.Success -> {
                findNavController().popBackStack()
                if (state.data == Constants.add){
                    Toast.makeText(activity, getString(R.string.note_added), Toast.LENGTH_SHORT).show()
                }else if (state.data == Constants.update) {
                    Toast.makeText(activity, getString(R.string.note_updated), Toast.LENGTH_SHORT).show()
                }
            }
            is ViewState.Failure -> {
                Toast.makeText(requireContext(), getText(R.string.error), Toast.LENGTH_SHORT).show()
            }
            is ViewState.Loading -> {
            }
        }
    }
}