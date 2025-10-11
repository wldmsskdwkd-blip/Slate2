package com.example.slate

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        replaceFragment(DashboardFragment())

        val btnPopular = findViewById<Button>(R.id.btnPopular)
        val btnPlaylist = findViewById<Button>(R.id.btnPlaylist)
        val btnSettings = findViewById<Button>(R.id.btnSettings)

        btnPopular.setOnClickListener { replaceFragment(DashboardFragment()) }
        btnPlaylist.setOnClickListener { replaceFragment(PlaylistFragment()) }
        btnSettings.setOnClickListener { replaceFragment(SettingsFragment()) }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
