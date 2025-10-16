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

class AddToPlaylistDialog(context: Context) : Dialog(context) {

    private lateinit var playlistContainer: LinearLayout
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button
    private var titleView: TextView? = null

    private val PREF_NAME = "playlist_prefs"
    private val KEY_PLAYLISTS = "playlist_names"
    private val KEY_COUNTS = "playlist_counts"
    private val KEY_CHECKED = "playlist_checked"

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

    // 🔸 SharedPreferences 불러오기
    private fun loadListFromPrefs(key: String): List<String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(key, null) ?: return emptyList()
        val arr = JSONArray(json)
        val list = mutableListOf<String>()
        for (i in 0 until arr.length()) list.add(arr.getString(i))
        return list
    }

    private fun loadCheckedStates(size: Int): MutableList<Boolean> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_CHECKED, null)
        val result = MutableList(size) { false }

        if (json != null) {
            val arr = JSONArray(json)
            for (i in 0 until minOf(size, arr.length())) {
                result[i] = arr.getBoolean(i)
            }
        }
        return result
    }

    // 🔸 체크박스 UI 생성
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

    // 🔸 저장 버튼 클릭 시 카운트 증가/감소 & 체크 상태 저장
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

            // 🔹 상태 저장용
            currentChecked[i] = isChecked

            // 🔹 체크 상태 변화에 따른 카운트 증감
            if (isChecked && !wasChecked) {
                // 새로 체크됨 → 영화 개수 +1
                counts[i] = counts[i] + 1
            } else if (!isChecked && wasChecked) {
                // 체크 해제됨 → 영화 개수 -1 (단, 0보다 작아지지 않게)
                counts[i] = (counts[i] - 1).coerceAtLeast(0)
            }
        }

        // 저장
        prefs.edit()
            .putString(KEY_PLAYLISTS, JSONArray(names).toString())
            .putString(KEY_COUNTS, JSONArray(counts).toString())
            .putString(KEY_CHECKED, JSONArray(currentChecked).toString())
            .apply()

        // SharedData 동기화 (화면 실시간 반영용)
        SharedData.playlistNames = names
        SharedData.playlistCounts = counts.toMutableList()
    }

    private fun dp(value: Int): Int =
        (value * context.resources.displayMetrics.density).toInt()
}
