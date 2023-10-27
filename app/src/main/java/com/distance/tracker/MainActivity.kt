package com.distance.tracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.distance.tracker.DistanceTracker.DistanceTrackerActivity
import com.distance.tracker.Notes.NotesActivity
import com.distance.tracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.distanceTracker.setOnClickListener{
            val intent = Intent(this, DistanceTrackerActivity::class.java)
            startActivity(intent)
        }
        viewBinding.notes.setOnClickListener{
            val intent = Intent(this, NotesActivity::class.java)
            startActivity(intent)
        }
    }
}