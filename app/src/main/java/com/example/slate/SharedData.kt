package com.example.slate

/**
 * 앱 전체에서 공유되는 데이터 저장소
 * - 재생목록 이름 / 영화 개수 리스트를 메모리에 유지
 * - AddToPlaylistDialog와 PlaylistFragment에서 함께 사용
 */
object SharedData {
    var playlistNames = mutableListOf<String>()
    var playlistCounts = mutableListOf<Int>()
}

