package com.hazratbilal.notecraft.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hazratbilal.notecraft.R
import com.hazratbilal.notecraft.databinding.FragmentNotesBinding
import com.hazratbilal.notecraft.model.NoteRequest
import com.hazratbilal.notecraft.model.NotesResponse
import com.hazratbilal.notecraft.ui.BaseFragment
import com.hazratbilal.notecraft.remote.NetworkResult
import com.hazratbilal.notecraft.utils.SharedPrefs
import com.hazratbilal.notecraft.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class NotesFragment : BaseFragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private val notesViewModel by viewModels<NotesViewModel>()

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private var isFirstLoad = true
    private lateinit var adapter: NotesAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        adapter = NotesAdapter(::deleteNote, ::editNote)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        attachClickListener()
        attachListener()
        bindObservers()

        if (isFirstLoad) {
            fetchData()
            isFirstLoad = false
        }

    }

    private fun initViews() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun attachClickListener() {
        binding.addNote.setOnClickListener {
            findNavController().navigate(R.id.createNoteFragment)
        }
    }

    private fun attachListener() {
        val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle
        val refreshNotes = savedStateHandle?.get<Boolean>("refreshNotes")
        if (refreshNotes == true) {
            savedStateHandle.remove<Boolean>("refreshNotes")
            fetchData()
        }
    }

    private fun bindObservers() {
        notesViewModel.notesResponseLiveData.observe(viewLifecycleOwner) {
            hideLoading()
            when (it) {
                is NetworkResult.Idle -> { }

                is NetworkResult.Loading -> {
                    showLoading()
                }

                is NetworkResult.Success -> {
                    if (it.data?.success == true) {

                        adapter.submitList(it.data.notes.toList())

                        if (it.data.notes.toList().isEmpty()) {
                            binding.notFound.visibility = View.VISIBLE
                        } else {
                            binding.notFound.visibility = View.GONE
                        }
                    } else {
                        requireContext().showToast(it.data?.message.toString())
						notesViewModel.clearState()
                    }
                }

                is NetworkResult.AuthError -> {
                    requireContext().showToast("Token expired. Please login again.")
					notesViewModel.clearState()
					
                    sharedPrefs.clearAll()
                    findNavController().navigate(
                        R.id.loginFragment, null, NavOptions.Builder()
                            .setPopUpTo(R.id.nav_graph, inclusive = true)
                            .setLaunchSingleTop(true)
                            .build()
                    )
                }

                is NetworkResult.Error -> {
                    requireContext().showToast(it.message ?: "Unknown error")
                    notesViewModel.clearState()
                }

            }
        }
    }

    private fun fetchData() {
        notesViewModel.getNotes()
    }

    private fun deleteNote(note: NotesResponse.Note) {
        notesViewModel.deleteNote(NoteRequest(note.id, "", ""))
    }

    private fun editNote(note: NotesResponse.Note) {
        val bundle = Bundle()
        bundle.putString("id", note.id)
        bundle.putString("title", note.title)
        bundle.putString("description", note.description)

        findNavController().navigate(R.id.editNoteFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}