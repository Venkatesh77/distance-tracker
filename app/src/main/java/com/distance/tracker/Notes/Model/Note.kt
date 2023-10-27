package com.distance.tracker.Notes.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(var id: String, var message: String) : Parcelable
