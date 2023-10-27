package com.distance.tracker.Notes.ListNotes

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.distance.tracker.Notes.Model.Note
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.distance.tracker.Utils.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception


class ListNotesFragmentViewModel: ViewModel() {
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var notesList: ArrayList<Note>
    val tag = "ListNotesFragmentViewModel"
    val notes = MutableLiveData<ViewState<ArrayList<Note>>>()

    init {
        viewModelScope.launch(Dispatchers.IO) { fetchNotes() }
    }

    private fun fetchNotes(){
        try {
            notes.postValue(ViewState.Loading)
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.reference.child("Notes")
            notesList = arrayListOf()

            databaseReference.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (previousChildName.isNullOrEmpty()) notesList.clear()
                    val id: String = (snapshot.value as HashMap<*, *>)["id"].toString()
                    val message: String = (snapshot.value as HashMap<*, *>)["message"].toString()
                    val note = Note(id = id, message = message)
                    notesList.add(note)
                    notes.postValue(ViewState.Success(notesList))
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val id: String = (snapshot.value as HashMap<*, *>)["id"].toString()
                    val message: String = (snapshot.value as HashMap<*, *>)["message"].toString()
                    val note: Note? = notesList.firstOrNull { note: Note -> note.id == id }
                    note?.let {
                        if (notesList.contains(it)) {
                            notesList.first { note: Note -> note.id == id }.message = message
                            notes.postValue(ViewState.Success(notesList))
                        }
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val id: String = (snapshot.value as HashMap<*, *>)["id"].toString()
                    val note: Note? = notesList.firstOrNull { note: Note -> note.id == id }
                    note?.let {
                        if (notesList.contains(it)) {
                            notesList.remove(it)
                            notes.postValue(ViewState.Success(notesList))
                        }
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(tag, error.message)
                }
            })
        }catch (ex: Exception){
            Log.d(tag, ex.message.toString())
        }
    }
}