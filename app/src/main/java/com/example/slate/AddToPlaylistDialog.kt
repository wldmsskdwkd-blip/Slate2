package com.example.slate

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import org.json.JSONArray

class AddToPlaylistDialog(context: Context, private val movieTitle: String) : Dialog(context) {

    private lateinit var playlistContainer: LinearLayout
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button
    private var titleView: TextView? = null

    private val PREF_NAME = "playlist_prefs"
    private val KEY_PLAYLISTS = "playlist_names"
    private val KEY_COUNTS = "playlist_counts"
    //영화마다 독립 저장: 제목을 키에 포함
    private val KEY_CHECKED_PREFIX = "playlist_checked_"

    private var checkBoxList = mutableListOf<CheckBox>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_add_to_playlist, null)
        setContentView(view)

        playlistContainer = view.findViewById(R.id.playlistContainer)
        btnCancel = view.findViewById(R.id.btnCancel)
        btnSave = view.findViewById(R.id.btnSave)
        titleView = view.findViewById(R.id.tvDialogTitle)
        titleView?.text = "동영상 저장"

        btnCancel.setOnClickListener { dismiss() }

        btnSave.setOnClickListener {
            saveSelectedPlaylists()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val width = (context.resources.displayMetrics.widthPixels * 0.9f).toInt()
        window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

        val playlists = loadListFromPrefs(KEY_PLAYLISTS)
        val checkedStates = loadCheckedStates(playlists.size)
        fillCheckBoxes(playlists, checkedStates)
    }

    // SharedPreferences 리스트 로드
    private fun loadListFromPrefs(key: String): List<String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(key, null) ?: return emptyList()
        val arr = JSONArray(json)
        val list = mutableListOf<String>()
        for (i in 0 until arr.length()) list.add(arr.getString(i))
        return list
    }

    // 영화별 체크 상태 로드
    private fun loadCheckedStates(size: Int): MutableList<Boolean> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val keyChecked = KEY_CHECKED_PREFIX + movieTitle
        val json = prefs.getString(keyChecked, null)
        val result = MutableList(size) { false }

        if (json != null) {
            val arr = JSONArray(json)
            for (i in 0 until minOf(size, arr.length())) {
                result[i] = arr.getBoolean(i)
            }
        }
        return result
    }

    // 체크박스 UI 생성
    private fun fillCheckBoxes(playlists: List<String>, checkedStates: List<Boolean>) {
        playlistContainer.removeAllViews()
        checkBoxList.clear()

        for (i in playlists.indices) {
            val cb = CheckBox(context).apply {
                text = playlists[i]
                isChecked = checkedStates.getOrNull(i) ?: false
                setTextColor(Color.WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                minHeight = dp(40)
                setPadding(dp(8), dp(6), dp(8), dp(6))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            playlistContainer.addView(cb)
            checkBoxList.add(cb)
        }
    }

    // 저장 시 체크 상태별 카운트 업데이트 및 저장
    private fun saveSelectedPlaylists() {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val names = loadListFromPrefs(KEY_PLAYLISTS).toMutableList()
        val counts = loadListFromPrefs(KEY_COUNTS).map { it.toIntOrNull() ?: 0 }.toMutableList()
        val previousChecked = loadCheckedStates(names.size)
        val currentChecked = MutableList(names.size) { false }

        while (counts.size < names.size) counts.add(0)

        for (i in checkBoxList.indices) {
            val cb = checkBoxList[i]
            val wasChecked = previousChecked.getOrNull(i) ?: false
            val isChecked = cb.isChecked

            // 상태 저장용
            currentChecked[i] = isChecked

            // 체크 변화에 따른 카운트 증감
            if (isChecked && !wasChecked) {
                counts[i] = counts[i] + 1
            } else if (!isChecked && wasChecked) {
                counts[i] = (counts[i] - 1).coerceAtLeast(0)
            }
        }

        // 저장 (영화별로 독립 KEY 사용)
        val keyChecked = KEY_CHECKED_PREFIX + movieTitle
        prefs.edit()
            .putString(KEY_PLAYLISTS, JSONArray(names).toString())
            .putString(KEY_COUNTS, JSONArray(counts).toString())
            .putString(keyChecked, JSONArray(currentChecked).toString())
            .apply()

        // SharedData 갱신 (UI 반영용)
        SharedData.playlistNames = names
        SharedData.playlistCounts = counts.toMutableList()
    }

    private fun dp(value: Int): Int =
        (value * context.resources.displayMetrics.density).toInt()
}
