package com.example.slate

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.json.JSONArray

class PlaylistFragment : Fragment() {

    private lateinit var playlistContainer: LinearLayout
    private lateinit var btnAddPlaylist: Button
    private val PREF_NAME = "playlist_prefs"
    private val KEY_PLAYLISTS = "playlist_names"
    private val KEY_COUNTS = "playlist_counts"

    // 👇 더블 클릭 감지용 변수
    private var lastClickedBlock: View? = null
    private var lastClickTime: Long = 0
    private val doubleClickThreshold = 400L // 400ms 안에 클릭되면 더블클릭으로 인식

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_playlist, container, false)

        playlistContainer = view.findViewById(R.id.playlistContainer)
        btnAddPlaylist = view.findViewById(R.id.btnAddPlaylist)

        loadPlaylists()

        btnAddPlaylist.setOnClickListener {
            addNewPlaylist()
        }

        return view
    }

    private fun loadPlaylists() {
        playlistContainer.removeAllViews()

        val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val names = loadListFromPrefs(prefs, KEY_PLAYLISTS)
        val counts = loadListFromPrefs(prefs, KEY_COUNTS).map { it.toIntOrNull() ?: 0 }

        SharedData.playlistNames = names.toMutableList()
        SharedData.playlistCounts = counts.toMutableList()

        for (i in names.indices) {
            addPlaylistBlock(names[i], counts[i], saveToPrefs = false)
        }
    }

    private fun addPlaylistBlock(name: String, movieCount: Int, saveToPrefs: Boolean = true) {
        val context = requireContext()
        val block = LayoutInflater.from(context)
            .inflate(R.layout.item_playlist_block, playlistContainer, false)

        val title = block.findViewById<TextView>(R.id.playlistTitle)
        val count = block.findViewById<TextView>(R.id.playlistCount)
        val edit = block.findViewById<View>(R.id.icEdit)
        val delete = block.findViewById<View>(R.id.icDelete)

        title.text = name
        count.text = "영화리스트 ${movieCount}개"

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, dp(10))
        block.layoutParams = params

        // 처음엔 편집/삭제 버튼 숨김
        edit.visibility = View.GONE
        delete.visibility = View.GONE

        // 🔸 클릭 이벤트
        block.setOnClickListener {
            val currentTime = System.currentTimeMillis()

            // 같은 블록을 일정 시간 내에 두 번 클릭하면 → 새 화면으로 이동
            if (lastClickedBlock == block && (currentTime - lastClickTime) < doubleClickThreshold) {
                val intent = Intent(requireContext(), PlaylistDetailActivity::class.java)
                intent.putExtra("playlistName", name)
                startActivity(intent)
                lastClickedBlock = null // 초기화
            } else {
                // 한 번 클릭이면 편집/삭제 토글
                toggleEditDelete(block)
                lastClickedBlock = block
                lastClickTime = currentTime
            }
        }

        // 수정 버튼
        edit.setOnClickListener {
            showRenameDialog(title, name)
        }

        // 삭제 버튼
        delete.setOnClickListener {
            playlistContainer.removeView(block)
            removePlaylist(name)
        }

        playlistContainer.addView(block)
        if (saveToPrefs) saveAllPlaylists()
    }

    private fun toggleEditDelete(block: View) {
        val edit = block.findViewById<View>(R.id.icEdit)
        val delete = block.findViewById<View>(R.id.icDelete)
        val visible = edit.visibility == View.VISIBLE

        edit.visibility = if (visible) View.GONE else View.VISIBLE
        delete.visibility = if (visible) View.GONE else View.VISIBLE
    }

    private fun showRenameDialog(titleView: TextView, oldName: String) {
        val editText = EditText(requireContext())
        editText.setText(oldName)

        AlertDialog.Builder(requireContext())
            .setTitle("이름 수정")
            .setView(editText)
            .setPositiveButton("확인") { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    titleView.text = newName
                    renamePlaylist(oldName, newName)
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun addNewPlaylist() {
        val newName = "My Moviest blocks ${playlistContainer.childCount + 1}"
        addPlaylistBlock(newName, 0)
    }

    private fun renamePlaylist(oldName: String, newName: String) {
        val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val names = loadListFromPrefs(prefs, KEY_PLAYLISTS).toMutableList()
        val counts = loadListFromPrefs(prefs, KEY_COUNTS).toMutableList()

        val index = names.indexOf(oldName)
        if (index != -1) {
            names[index] = newName
            saveListToPrefs(prefs, KEY_PLAYLISTS, names)
        }

        SharedData.playlistNames = names
        SharedData.playlistCounts = counts.map { it.toIntOrNull() ?: 0 }.toMutableList()
    }

    private fun removePlaylist(name: String) {
        val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val names = loadListFromPrefs(prefs, KEY_PLAYLISTS).toMutableList()
        val counts = loadListFromPrefs(prefs, KEY_COUNTS).toMutableList()

        val index = names.indexOf(name)
        if (index != -1) {
            names.removeAt(index)
            if (index < counts.size) counts.removeAt(index)
        }

        saveListToPrefs(prefs, KEY_PLAYLISTS, names)
        saveListToPrefs(prefs, KEY_COUNTS, counts)

        SharedData.playlistNames = names
        SharedData.playlistCounts = counts.map { it.toIntOrNull() ?: 0 }.toMutableList()
    }

    private fun saveAllPlaylists() {
        val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val names = mutableListOf<String>()
        val counts = mutableListOf<String>()

        for (i in 0 until playlistContainer.childCount) {
            val block = playlistContainer.getChildAt(i)
            val title = block.findViewById<TextView>(R.id.playlistTitle).text.toString()
            val countText = block.findViewById<TextView>(R.id.playlistCount).text.toString()
            val num = countText.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
            names.add(title)
            counts.add(num.toString())
        }

        saveListToPrefs(prefs, KEY_PLAYLISTS, names)
        saveListToPrefs(prefs, KEY_COUNTS, counts)

        SharedData.playlistNames = names
        SharedData.playlistCounts = counts.map { it.toIntOrNull() ?: 0 }.toMutableList()
    }

    private fun loadListFromPrefs(prefs: android.content.SharedPreferences, key: String): List<String> {
        val json = prefs.getString(key, null) ?: return emptyList()
        val arr = JSONArray(json)
        val list = mutableListOf<String>()
        for (i in 0 until arr.length()) list.add(arr.getString(i))
        return list
    }

    private fun saveListToPrefs(prefs: android.content.SharedPreferences, key: String, list: List<String>) {
        val arr = JSONArray(list)
        prefs.edit().putString(key, arr.toString()).apply()
    }

    private fun dp(value: Int): Int =
        (value * requireContext().resources.displayMetrics.density).toInt()
}
