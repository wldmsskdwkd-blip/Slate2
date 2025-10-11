package com.example.slate

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MovieInformationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_information)

        val title = intent.getStringExtra("title") ?: "제목 없음"
        val rank = intent.getIntExtra("rank", 0)

        val textMovieInfo = findViewById<TextView>(R.id.textMovieInfo)
        val backArrow = findViewById<ImageView>(R.id.backArrow)

        textMovieInfo.text = "영화 제목: $title\n순위: ${rank}위"

        backArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}