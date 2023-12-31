package com.mikula441.shoppingnotebook.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mikula441.shoppingnotebook.activities.MainApp
import com.mikula441.shoppingnotebook.activities.NewNoteActivity
import com.mikula441.shoppingnotebook.database.MainViewModel
import com.mikula441.shoppingnotebook.database.NoteAdapter
import com.mikula441.shoppingnotebook.databinding.FragmentNoteBinding
import com.mikula441.shoppingnotebook.entities.NoteItem


class NoteFragment : BaseFragment(), NoteAdapter.Listener {
    private lateinit var binding: FragmentNoteBinding
    private lateinit var editLauncher: ActivityResultLauncher<Intent>
    private lateinit var adapter: NoteAdapter
    private lateinit var defPref: SharedPreferences
    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory(
            (context?.applicationContext as MainApp).database
        )
    }
    override fun onClickNew() {
        editLauncher.launch(Intent(activity, NewNoteActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onEditResult()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        observer()
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun initRcView() = with(binding){
        defPref = PreferenceManager
            .getDefaultSharedPreferences(this@NoteFragment.activity!!)
        rcViewNote.layoutManager = getLayoutManager()
        adapter = NoteAdapter(this@NoteFragment, defPref)
        rcViewNote.adapter = adapter
    }

    private fun getLayoutManager(): RecyclerView.LayoutManager{
        return if (defPref.getString(
                "note_style_key", "linear"
            ) == "linear"){
            LinearLayoutManager(activity)
        } else {
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }

    private fun observer(){
        mainViewModel.allNotes.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun onEditResult(){
        editLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){
            if (it.resultCode == Activity.RESULT_OK){
                val editState = it.data?.getStringExtra(EDIT_STATE_KEY)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (editState == "update"){
                        mainViewModel.updateNote(it.data?.getSerializableExtra(NEW_NOTE_KEY, NoteItem::class.java) as NoteItem)
                    } else {
                        mainViewModel.insertNote(it.data?.getSerializableExtra(NEW_NOTE_KEY, NoteItem::class.java) as NoteItem)
                    }
                } else {
                    if (editState == "update") {
                        @Suppress("DEPRECATION")
                        mainViewModel.updateNote(it.data?.getSerializableExtra(NEW_NOTE_KEY) as NoteItem)
                    } else {
                        @Suppress("DEPRECATION")
                        mainViewModel.insertNote(it.data?.getSerializableExtra(NEW_NOTE_KEY) as NoteItem)
                    }

                }
            }
        }
    }

    override fun deleteItem(id: Int) {
        mainViewModel.deleteNote(id)

    }

    override fun onClickItem(note: NoteItem) {
        val intent = Intent(activity, NewNoteActivity::class.java).apply {
            putExtra(NEW_NOTE_KEY, note)
        }
        editLauncher.launch(intent)
    }

    companion object {
        const val NEW_NOTE_KEY = "new_note_key"
        const val EDIT_STATE_KEY = "edit_state_key"
        @JvmStatic
        fun newInstance() = NoteFragment()
    }
}