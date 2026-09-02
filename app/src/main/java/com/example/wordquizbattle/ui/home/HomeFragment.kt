package com.example.wordquizbattle.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.wordquizbattle.R
import com.example.wordquizbattle.data.db.AppDatabase
import com.example.wordquizbattle.databinding.FragmentHomeBinding
import com.example.wordquizbattle.util.LastDeckStore
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardQuiz.setOnClickListener { startQuickMode("quiz") }
        binding.cardTimeAttack.setOnClickListener { startQuickMode("timeattack") }
        binding.cardCombo.setOnClickListener { startQuickMode("combo") }
        binding.cardWeak.setOnClickListener { startQuickMode("weak") }

        loadHomeStats()
    }

    private fun loadHomeStats() {
        val db = AppDatabase.getDatabase(requireContext())

        lifecycleScope.launch {
            // 登録単語数
            val wordCount = db.wordDao().getTotalWordCount()
            binding.tvWordCount.text = "$wordCount"

            // 平均正答率
            val overall = db.quizAnswerLogDao().getOverallAccuracy()
            binding.tvAccuracy.text = if (overall.total > 0) {
                "${overall.correct * 100 / overall.total}%"
            } else {
                "--%"
            }

            // 連続記録
            val playDates = db.quizResultDao().getDistinctPlayDates()
            binding.tvStreak.text = "${calculateStreak(playDates)}日"

            // 昨日のスコア
            val yesterdayScore = db.quizResultDao().getYesterdayBestScore()
            binding.tvYesterdayScore.text = if (yesterdayScore != null) {
                "昨日のスコア：${yesterdayScore}pt"
            } else {
                "昨日のスコア：--pt"
            }
        }
    }

    /** 今日から遡って、何日連続でプレイ記録があるかを数える */
    private fun calculateStreak(playDates: List<String>): Int {
        val daySet = playDates.toHashSet()
        var cursor = LocalDate.now()

        // 今日まだプレイしていない場合、昨日までの記録で連続日数を判定する
        if (!daySet.contains(cursor.toString())) {
            cursor = cursor.minusDays(1)
        }

        var streak = 0
        while (daySet.contains(cursor.toString())) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }

    private fun startQuickMode(mode: String) {
        val deckId = LastDeckStore.getLastDeckId(requireContext())
        if (deckId == null) {
            Toast.makeText(requireContext(), "まずは単語タブからデッキを選んでください", Toast.LENGTH_SHORT).show()
            return
        }
        val bundle = Bundle().apply {
            putLong("deckId", deckId)
            putString("mode", mode)
        }
        findNavController().navigate(R.id.quizFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}