package com.felis.mldl.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.felis.mldl.R
import com.felis.mldl.activity.WelcomeActivity

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val nextButton: Button = root.findViewById(R.id.nextbtn)



        nextButton.setOnClickListener {
//            findNavController().navigate(R.id.find)
            startActivity(Intent(context, Maintip1Activity::class.java))
        }

//        logoutButton.setOnClickListener()
//        {
//            logoutService.getLogout().enqueue(object : Callback<Logout> {
//                override fun onResponse(call: Call<Logout>, response: Response<Logout>) {
//                    Toast.makeText(this@.activity.WelcomeActivity, "로그아웃 성공", Toast.LENGTH_LONG).show()
//                    startActivity(intent)
//                }
//
//                override fun onFailure(call: Call<Logout>, t: Throwable) {
//                }
//            })
//        }
        return root
    }


}