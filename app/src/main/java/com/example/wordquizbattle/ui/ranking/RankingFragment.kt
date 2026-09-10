package com.example.wordquizbattle.ui.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordquizbattle.R
import com.example.wordquizbattle.data.db.AppDatabase
import com.example.wordquizbattle.databinding.FragmentRankingBinding
import com.example.wordquizbattle.ui.analysis.AnalysisAdapter
import com.example.wordquizbattle.ui.analysis.WeakWordItem
import kotlinx.coroutines.launch

class RankingFragment : Fragment() {
    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!

    private val rankingAdapter = RankingAdapter()
    private val weakWordsAdapter = AnalysisAdapter()
    private var selectedMode = "quiz"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRankingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvRanking.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRanking.adapter = rankingAdapter

        binding.rvWeakWordsTop3.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWeakWordsTop3.adapter = weakWordsAdapter

        setupTabs()
        loadRanking(selectedMode)
        loadWeakWordsTop3(selectedMode)
    }

    private fun setupTabs() {
        val tabMap = mapOf(
            binding.tabNormal to "quiz",
            binding.tabTimeAttack to "timeattack",
            binding.tabCombo to "combo"
        )

        tabMap.keys.forEach { tab ->
            tab.setOnClickListener {
                selectedMode = tabMap[tab] ?: selectedMode
                updateTabUi(tabMap)
                loadRanking(selectedMode)
                loadWeakWordsTop3(selectedMode)
            }
        }
        updateTabUi(tabMap)
    }

    private fun updateTabUi(tabMap: Map<android.widget.TextView, String>) {
        val context = requireContext()
        tabMap.forEach { (tab, mode) ->
            val isSelected = mode == selectedMode
            // ランキング専用の選択色（黄色）。単語一覧の既存tab_selected（紫）とは別ファイル。
            tab.setBackgroundResource(
                if (isSelected) R.drawable.tab_selected_ranking
                else R.drawable.tab_unselected
            )
            tab.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (isSelected) R.color.text_primary else R.color.text_secondary
                )
            )
        }
    }

    private fun loadRanking(mode: String) {
        val db = AppDatabase.getDatabase(requireContext())
        db.quizResultDao().getGlobalRanking(mode).observe(viewLifecycleOwner) { results ->
            viewLifecycleOwner.lifecycleScope.launch {
                val items = results.mapIndexed { index, result ->
                    val deck = db.deckDao().getDeckById(result.deckId)
                    RankingItem(
                        deckName = deck?.name ?: "不明なデッキ",
                        score = result.score,
                        maxCombo = result.maxCombo,
                        rank = index + 1
                    )
                }
                rankingAdapter.submitList(items)
            }
        }
    }

    private fun loadWeakWordsTop3(mode: String) {
        val db = AppDatabase.getDatabase(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            val stats = db.quizAnswerLogDao().getWordAccuracyStatsByMode(mode)
            val top3 = stats
                .filter { it.total > 0 }
                .map { stat ->
                    val word = db.wordDao().getWordById(stat.wordId)
                    val accuracy = (stat.correct * 100 / stat.total)
                    WeakWordItem(word?.term ?: "不明", accuracy)
                }
                .sortedBy { it.accuracy }
                .take(3)
            weakWordsAdapter.submitList(top3)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}