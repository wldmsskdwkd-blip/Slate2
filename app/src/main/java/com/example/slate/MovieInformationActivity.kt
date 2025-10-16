package com.example.slate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.slate.databinding.ActivityMovieInformationBinding

class MovieInformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로가기 버튼
        binding.backArrow.setOnClickListener { finish() }

        // DashboardFragment에서 전달된 영화 제목 받기
        val title = intent.getStringExtra("title") ?: "unknown"

        // ic_add 버튼 클릭 시 팝업 띄우기 (영화 제목 전달)
        binding.btnAdd.setOnClickListener {
            val dialog = AddToPlaylistDialog(this, title)
            dialog.show()
        }
    }
}
