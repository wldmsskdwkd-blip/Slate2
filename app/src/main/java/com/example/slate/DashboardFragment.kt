package com.example.slate

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // 버튼 ID와 순위 지정
        val mapping: List<Pair<Int, Int>> = listOf(
            Pair(R.id.movie1Button, 1),
            Pair(R.id.movie2Button, 2),
            Pair(R.id.movie3Button, 3),
            Pair(R.id.movie4Button, 4),
            Pair(R.id.movie5Button, 5),
            Pair(R.id.movie6Button, 6)
        )

        // 각 버튼 클릭 시 제목과 순위를 MovieInformationActivity로 전달
        mapping.forEach { (btnId, rank) ->
            val btn = view.findViewById<Button>(btnId)
            btn.setOnClickListener { v ->
                val title = (v as Button).text.toString()
                val intent = Intent(requireContext(), MovieInformationActivity::class.java)
                intent.putExtra("title", title)
                intent.putExtra("rank", rank)
                startActivity(intent)
            }
        }

        return view
    }
}