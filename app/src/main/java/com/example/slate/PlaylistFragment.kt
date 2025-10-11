package com.example.slate

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import org.json.JSONArray

class PlaylistFragment : Fragment() {

    private lateinit var playlistContainer: LinearLayout
    private lateinit var btnAddPlaylist: Button
    private var playlistCount = 0
    private val handler = Handler(Looper.getMainLooper())

    private val PREF_NAME = "playlist_prefs"
    private val KEY_PLAYLISTS = "playlists"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_playlist, container, false)

        playlistContainer = view.findViewById(R.id.playlistContainer)
        btnAddPlaylist = view.findViewById(R.id.btnAddPlaylist)


        loadPlaylists()

        // 새로운 플레이리스트 추가
        btnAddPlaylist.setOnClickListener {
            addPlaylistBlock("My Moviest blocks")
            savePlaylists()
        }

        return view
    }


    private fun savePlaylists() {
        val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonArray = JSONArray()

        // 현재 있는 모든 카드의 제목을 JSON 배열로 저장
        for (i in 0 until playlistContainer.childCount) {
            val block = playlistContainer.getChildAt(i) as LinearLayout
            val header = block.getChildAt(0) as LinearLayout
            val textLayout = header.getChildAt(0) as LinearLayout
            val titleView = textLayout.getChildAt(0) as TextView
            jsonArray.put(titleView.text.toString())
        }

        prefs.edit().putString(KEY_PLAYLISTS, jsonArray.toString()).apply()
    }


    private fun loadPlaylists() {
        val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(KEY_PLAYLISTS, "[]")
        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            val title = jsonArray.getString(i)
            addPlaylistBlock(title)
        }
    }

    private fun addPlaylistBlock(titleText: String) {
        val context = requireContext()

        // 카드 전체 컨테이너
        val blockLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(30, 30, 30, 30)
            setBackgroundColor(0xFFD9D9D9.toInt())
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 20)
            layoutParams = params
        }

        // 제목/부제 + 아이콘 영역
        val headerLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val textLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val title = TextView(context).apply {
            text = titleText
            textSize = 16f
            setTextColor(0xFF000000.toInt())
        }

        val subText = TextView(context).apply {
            text = "영화리스트 0개"
            textSize = 14f
            setTextColor(0xFF444444.toInt())
        }

        textLayout.addView(title)
        textLayout.addView(subText)

        // 아이콘
        val iconLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            visibility = View.INVISIBLE
        }

        val editIcon = ImageView(context).apply {
            setImageResource(R.drawable.ic_edit)
            layoutParams = LinearLayout.LayoutParams(60, 60)
        }

        val deleteIcon = ImageView(context).apply {
            setImageResource(R.drawable.ic_delete)
            layoutParams = LinearLayout.LayoutParams(60, 60)
        }

        iconLayout.addView(editIcon)
        iconLayout.addView(deleteIcon)
        headerLayout.addView(textLayout)
        headerLayout.addView(iconLayout)
        blockLayout.addView(headerLayout)


        blockLayout.setOnClickListener {
            iconLayout.visibility = View.VISIBLE
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({
                iconLayout.visibility = View.INVISIBLE
            }, 2000)
        }


        editIcon.setOnClickListener {
            val editText = EditText(context).apply {
                setText(title.text)
                textSize = 16f
                setTextColor(0xFF000000.toInt())
                inputType = android.text.InputType.TYPE_CLASS_TEXT
                setBackgroundResource(android.R.drawable.edit_text)
            }

            val index = textLayout.indexOfChild(title)
            textLayout.removeViewAt(index)
            textLayout.addView(editText, index)

            editText.requestFocus()

            editText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    title.text = editText.text.toString()
                    textLayout.removeView(editText)
                    textLayout.addView(title, index)
                    savePlaylists()
                }
            }

            editText.setOnEditorActionListener { _, _, _ ->
                title.text = editText.text.toString()
                textLayout.removeView(editText)
                textLayout.addView(title, index)
                savePlaylists()
                true
            }
        }

        deleteIcon.setOnClickListener {
            playlistContainer.removeView(blockLayout)
            savePlaylists()
        }

        playlistContainer.addView(blockLayout)
    }
}
