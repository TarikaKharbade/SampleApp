package com.example.sampleapp

import QuotesAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.sampleapp.Adapter.ImageAdapter
import com.example.sampleapp.models.ImageItem
import com.example.sampleapp.models.Quote
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private lateinit var viewpager2 : ViewPager2
    private lateinit var pageChangeListener: ViewPager2.OnPageChangeCallback
    var data = emptyArray<Quote>()
    private lateinit var userArrayList: ArrayList<Quote>
    private lateinit var quotesAdapter: QuotesAdapter
    private lateinit var quotesList: List<Quote>

    private val params = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        setMargins(8,0,8,0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        quotesList = loadAssetsFromFile()

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        quotesAdapter = QuotesAdapter(quotesList)
        recyclerView.adapter = quotesAdapter
        viewpager2 = findViewById(R.id.viewpager2)
        userArrayList = ArrayList()

        val imageList = arrayListOf(
            ImageItem(
                UUID.randomUUID().toString(),
                    "android.resource://" + packageName + "/" + R.drawable.imagep
            ),
            ImageItem(
                UUID.randomUUID().toString(),
                "android.resource://" + packageName + "/" + R.drawable.imaget
            ),
            ImageItem(
                UUID.randomUUID().toString(),
                "android.resource://" + packageName + "/" + R.drawable.imagejp
            ),
            ImageItem(
                UUID.randomUUID().toString(),
                "android.resource://" + packageName + "/" + R.drawable.imagepn
            )
        )

        val imageAdapter = ImageAdapter()
        viewpager2.adapter = imageAdapter

        imageAdapter.submitList(imageList)

        val searchEditText: EditText = findViewById(R.id.edtSearch)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Filter the adapter's list based on the search query
                quotesAdapter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }
        })

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            showBottomSheetDialog()
        }


        val slideDotLL = findViewById<LinearLayout>(R.id.slideDotLL)
        val dotsImage = Array(imageList.size) { ImageView(this)}

        dotsImage.forEach {
            it.setImageResource(
                R.drawable.non_active_dots
            )
            slideDotLL.addView(it,params)
        }

        // default first dot selected
        dotsImage[0].setImageResource(R.drawable.active_dots)

        pageChangeListener = object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                dotsImage.mapIndexed { index, imageView ->
                    if (position == index){
                        imageView.setImageResource(
                            R.drawable.active_dots
                        )
                    }else{
                        imageView.setImageResource(R.drawable.non_active_dots)
                    }
                }
                super.onPageSelected(position)
            }
        }
        viewpager2.registerOnPageChangeCallback(pageChangeListener)
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        val pageCountTextView: TextView = bottomSheetView.findViewById(R.id.pageCountTextView)
        val topCharacterTextView: TextView = bottomSheetView.findViewById(R.id.topCharacterTextView)
        val topCharactersListTextView: TextView = bottomSheetView.findViewById(R.id.topCharactersListTextView)

        // Calculate statistics
        val totalItems = quotesList.size
        val charCountMap = mutableMapOf<Char, Int>()

        quotesList.forEach { quote ->
            quote.text.forEach { char ->
                if (char.isLetter()) {
                    charCountMap[char.toLowerCase()] = charCountMap.getOrDefault(char.toLowerCase(), 0) + 1
                }
            }
        }

        val topCharacters = charCountMap.entries.sortedByDescending { it.value }.take(3)

        // Update TextViews with calculated data
        pageCountTextView.text = "Total Quotes: $totalItems"
        topCharacterTextView.text = "Top Characters:"

        val topCharactersText = topCharacters.joinToString("\n") { "${it.key} = ${it.value}" }
        topCharactersListTextView.text = topCharactersText

        // Show the bottom sheet dialog
        bottomSheetDialog.show()
    }


    override fun onDestroy() {
        super.onDestroy()
        viewpager2.unregisterOnPageChangeCallback(pageChangeListener)
    }

    fun loadAssetsFromFile(): List<Quote>{

        val inputStream = assets.open("Quote.json")
        val size : Int = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val json = String(buffer, Charsets.UTF_8)
        val gson = Gson()
        return gson.fromJson(json, Array<Quote>::class.java).toList()

    }
}