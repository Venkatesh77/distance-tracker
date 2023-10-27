package com.distance.tracker.Notes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.distance.tracker.databinding.ActivityNotesBinding


class NotesActivity : AppCompatActivity(){
    private lateinit var viewBinding: ActivityNotesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }
}