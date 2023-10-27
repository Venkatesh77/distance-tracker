package com.distance.tracker.DistanceTracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.distance.tracker.databinding.ActivityDistanceTrackerBinding

class DistanceTrackerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDistanceTrackerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDistanceTrackerBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}