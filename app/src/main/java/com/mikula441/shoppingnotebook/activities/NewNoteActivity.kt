package com.mikula441.shoppingnotebook.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.mikula441.shoppingnotebook.R
import com.mikula441.shoppingnotebook.databinding.ActivityNewNoteBinding
import com.mikula441.shoppingnotebook.entities.NoteItem
import com.mikula441.shoppingnotebook.fragments.NoteFragment
import com.mikula441.shoppingnotebook.utils.HtmlManager
import com.mikula441.shoppingnotebook.utils.MyTouchListener
import com.mikula441.shoppingnotebook.utils.TimeManager

class NewNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewNoteBinding
    private var note: NoteItem? = null
    private var pref: SharedPreferences? = null
    private lateinit var defPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme())
        super.onCreate(savedInstanceState)
        binding = ActivityNewNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBarSettings()
        getNote()
        init()
        setTextSize()
        onClickColorsPicker()
        actionMenuCallback()
    }

    private fun onClickColorsPicker() = with(binding){
        ibBlack.setOnClickListener {
            setColorForSelectedColors(R.color.black)
        }
        ibGreen.setOnClickListener {
            setColorForSelectedColors(R.color.green)
        }
        ibOrange.setOnClickListener {
            setColorForSelectedColors(R.color.orange)
        }
        ibRed.setOnClickListener {
            setColorForSelectedColors(R.color.red)
        }
        ibViolet.setOnClickListener {
            setColorForSelectedColors(R.color.violet)
        }
        ibYellow.setOnClickListener {
            setColorForSelectedColors(R.color.yellow)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init(){
        binding.colorPicker.setOnTouchListener(MyTouchListener())
        pref = PreferenceManager.getDefaultSharedPreferences(this)
    }

    private fun getNote(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val sNote = intent.getSerializableExtra(NoteFragment.NEW_NOTE_KEY,
                NoteItem::class.java)
            if (sNote != null) {
                note = sNote
                fillNote()
            }
        } else {
            @Suppress("DEPRECATION")
            val sNote = intent.getSerializableExtra(NoteFragment.NEW_NOTE_KEY)
            if (sNote != null) {
                note = sNote as NoteItem
                fillNote()
            }
        }
    }

    private fun fillNote() = with(binding){
        edTitle.setText(note?.title)
        edDescription.setText(HtmlManager.getFromHtml(note?.content!!).trim())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_note_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.id_save -> {
                setMainResult()
            }
            android.R.id.home -> {
                finish()
            }
            R.id.id_bold -> {
                setBoldForSelectedText()
            }
            R.id.id_colors -> {
                if (binding.colorPicker.isShown){
                    closeColorsPicker()
                } else { openColorsPicker() }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setBoldForSelectedText()= with(binding){
        val startPos = edDescription.selectionStart
        val endPos = edDescription.selectionEnd
        val styles = edDescription
            .text.getSpans(startPos, endPos, StyleSpan::class.java)
        var boldStyle: StyleSpan? = null

        if (styles.isNotEmpty()){
            edDescription.text.removeSpan(styles[0])
        } else {
            boldStyle = StyleSpan(Typeface.BOLD)
        }
        edDescription.text
            .setSpan(boldStyle, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        edDescription.text.trim()
        edDescription.setSelection(startPos)
    }

    private fun setColorForSelectedColors(colorId: Int)= with(binding){
        val startPos = edDescription.selectionStart
        val endPos = edDescription.selectionEnd

        val styles = edDescription
            .text.getSpans(startPos, endPos, ForegroundColorSpan::class.java)

        if (styles.isNotEmpty()) edDescription.text.removeSpan(styles[0])

        edDescription.text
            .setSpan(ForegroundColorSpan(
                ContextCompat.getColor(this@NewNoteActivity, colorId)),
                startPos,
                endPos,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        edDescription.text.trim()
        edDescription.setSelection(startPos)
    }

    private fun setMainResult(){
        var editState = "new"
        val  tempNote: NoteItem? = if ( note==null ){
            createNewNote()
        } else {
            editState = "update"
            updateNote()
        }
        val  i = Intent().apply {
            putExtra(NoteFragment.NEW_NOTE_KEY, tempNote)
            putExtra(NoteFragment.EDIT_STATE_KEY, editState)
        }
        setResult(RESULT_OK, i)
        finish()
    }
    private fun updateNote(): NoteItem? = with(binding){
        return note?.copy(
            title = edTitle.text.toString(),
            content = HtmlManager.toHtml(edDescription.text)
        )
    }

    private fun createNewNote(): NoteItem {
        return NoteItem(
            null,
            binding.edTitle.text.toString(),
            HtmlManager.toHtml(binding.edDescription.text),
            TimeManager.getCurrentTime(),
            ""
        )
    }

    private fun actionBarSettings(){
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    private fun openColorsPicker(){
        binding.colorPicker.visibility = View.VISIBLE
        val openAnim =
            AnimationUtils.loadAnimation(this, R.anim.open_colors_picker)
        binding.colorPicker.startAnimation(openAnim)

    }

    private fun closeColorsPicker(){
        val openAnim =
            AnimationUtils.loadAnimation(this, R.anim.close_colors_picker)
        openAnim.setAnimationListener(object: Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.colorPicker.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }

        })
        binding.colorPicker.startAnimation(openAnim)
    }

    private fun actionMenuCallback(){
        val  actionCallback = object: ActionMode.Callback{
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menu?.clear()
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menu?.clear()
                return true
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return true
            }

            override fun onDestroyActionMode(mode: ActionMode?) {

            }

        }
        binding.edDescription.customSelectionActionModeCallback = actionCallback
    }

    private fun setTextSize() = with(binding){
        edTitle.setTextSize(pref?.getString("title_size_key", "16"))
        edDescription.setTextSize(pref?.getString("content_size_key", "12"))

    }

    private fun EditText.setTextSize(size: String?){
        if (size!= null){
            this.textSize = size.toFloat()
        }
    }

    private fun getSelectedTheme(): Int{
        return if (defPref.getString("theme_key", "green") == "green"){
            R.style.Base_Theme_ShoppingNotebookGreen
        } else {
            R.style.Base_Theme_ShoppingNotebookViolet
        }
    }
}