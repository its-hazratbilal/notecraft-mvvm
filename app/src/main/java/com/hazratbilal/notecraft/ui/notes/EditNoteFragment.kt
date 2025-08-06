package com.hazratbilal.notecraft.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.hazratbilal.notecraft.R
import com.hazratbilal.notecraft.databinding.FragmentEditNoteBinding
import com.hazratbilal.notecraft.model.NoteRequest
import com.hazratbilal.notecraft.ui.BaseFragment
import com.hazratbilal.notecraft.remote.NetworkResult
import com.hazratbilal.notecraft.utils.SharedPrefs
import com.hazratbilal.notecraft.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class EditNoteFragment : BaseFragment() {

    private var _binding: FragmentEditNoteBinding? = null
    private val binding get() = _binding!!
    private val notesViewModel by viewModels<NotesViewModel>()

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private var noteId: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentEditNoteBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        attachClickListener()
        attachListener()
        bindObservers()

    }

    private fun attachClickListener() {
        binding.updateNote.setOnClickListener {
            val title = binding.title.text.toString()
            val description = binding.description.text.toString()

            if (title.trim().isEmpty() || description.trim().isEmpty()) {
                requireContext().showToast("Title and Description cannot be empty")
            } else {
                notesViewModel.updateNote(NoteRequest(noteId, title, description))
            }
        }

    }

    private fun attachListener() {
        val args = requireArguments()
        noteId = args.getString("id").toString()
        val title = args.getString("title")
        val description = args.getString("description")

        binding.title.setText(title)
        binding.description.setText(description)

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

                        requireContext().showToast("Note updated successfully")

                        findNavController().previousBackStackEntry?.savedStateHandle?.apply {
                            set("refreshNotes", true)
                        }
                        findNavController().popBackStack()

                    } else {
                        requireContext().showToast(it.data?.message.toString())
                    }
                }

                is NetworkResult.AuthError -> {
                    requireContext().showToast("Token expired. Please login again.")
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
                }

            }

            if (it !is NetworkResult.Idle && it !is NetworkResult.Loading) {
                notesViewModel.clearState()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}