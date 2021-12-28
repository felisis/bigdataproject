package com.felis.mldl.camera

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.felis.mldl.R


class CameraFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_camera, container, false)

        val nextButton: Button = root.findViewById(R.id.entermorphing)


        nextButton.setOnClickListener {
//            findNavController().navigate(R.id.find)
            startActivity(Intent(context, MorphingselectpicActivity::class.java))
        }

        return root
    }
}