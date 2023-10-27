package com.distance.tracker.Notes.AddNotes

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.distance.tracker.Notes.Model.Note
import com.distance.tracker.Utils.Constants
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.distance.tracker.Utils.ViewState
import java.lang.Exception


class AddOrUpdatesNotesFragmentViewModel: ViewModel() {
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var databaseReference: DatabaseReference = firebaseDatabase.reference.child("Notes")
    val notes = MutableLiveData<ViewState<String>>()
    val tag = "ListNotesFragmentViewModel"

    fun addNoteToFirebase(message: String){
        try {
            val key: String = databaseReference.push().key.toString()
            val note = Note(id = key, message = message)
            note.id = key
            databaseReference.child(key).setValue(note)
            notes.postValue(ViewState.Success(Constants.add))
        }catch (ex: Exception){
            Log.d(tag, ex.message.toString())
        }
    }

    fun updateNoteToFirebase(message: String, note: Note){
        try {
            val note = Note(id = note.id, message = message)
            databaseReference.child(note.id).setValue(note)
            notes.postValue(ViewState.Success(Constants.update))
        }catch (ex: Exception){
            Log.d(tag, ex.message.toString())
        }
    }

}