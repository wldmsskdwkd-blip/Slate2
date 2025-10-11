package com.example.slate

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {

    private var isNotificationOn = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val btnToggle = view.findViewById<Button>(R.id.btnNotificationToggle)
        val btnLogout = view.findViewById<LinearLayout>(R.id.btnLogout)


        updateButtonUI(btnToggle)


        btnToggle.setOnClickListener {
            isNotificationOn = !isNotificationOn
            updateButtonUI(btnToggle)
        }


        btnLogout.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // 현재 액티비티(MainActivity) 종료
        }

        return view
    }

    private fun updateButtonUI(button: Button) {
        if (isNotificationOn) {
            button.text = "Notification (ON)"
            button.setBackgroundColor(
                ContextCompat.getColor(requireContext(), android.R.color.holo_green_light)
            )
        } else {
            button.text = "Notification Toggle (OFF)"
            button.setBackgroundColor(
                ContextCompat.getColor(requireContext(), android.R.color.darker_gray)
            )
        }
    }
}