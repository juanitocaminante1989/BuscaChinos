package com.example.juan.buscachinos.presentation.map

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.juan.buscachinos.BuscaChinosApplication
import com.example.juan.buscachinos.R
import com.example.juan.buscachinos.domain.model.Chino
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/** Listado (dentro de un bottom sheet) de todos los chinos tagueados. */
class TagListBottomSheet : BottomSheetDialogFragment() {

    fun interface Listener {
        fun onChinoSelected(chino: Chino)
    }

    private var listener: Listener? = null

    // Comparte la misma instancia de MapViewModel que MainActivity (misma factory/container).
    private val viewModel: MapViewModel by activityViewModels {
        val container = (requireActivity().application as BuscaChinosApplication).container
        MapViewModelFactory(container)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? Listener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.bottom_sheet_chino_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.chino_list)
        val emptyState = view.findViewById<TextView>(R.id.empty_state)
        val adapter = ChinoListAdapter { chino ->
            listener?.onChinoSelected(chino)
            dismiss()
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.allChinos }.distinctUntilChanged()
                    .collect { chinos ->
                        adapter.submitList(chinos)
                        recyclerView.visibility = if (chinos.isEmpty()) View.GONE else View.VISIBLE
                        emptyState.visibility = if (chinos.isEmpty()) View.VISIBLE else View.GONE
                    }
            }
        }
    }

    companion object {
        const val TAG = "TagListBottomSheet"
    }
}
