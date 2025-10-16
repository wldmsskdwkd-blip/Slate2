package com.example.slate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.slate.databinding.ActivityPlaylistDetailBinding

class PlaylistDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 전달받은 제목 표시
        val playlistName = intent.getStringExtra("playlistName") ?: "My Playlist"
        binding.playlistTitle.text = playlistName

        // 뒤로가기 버튼 클릭 시
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
