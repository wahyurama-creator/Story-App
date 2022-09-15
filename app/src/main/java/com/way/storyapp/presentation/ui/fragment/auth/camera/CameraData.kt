package com.way.storyapp.presentation.ui.fragment.auth.camera

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class CameraData(
    val file: File,
    val isBackCamera: Boolean = false
) : Parcelable
