import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleapp.R
import com.example.sampleapp.models.Quote

class QuotesAdapter(private val originalList: List<Quote>) :
    RecyclerView.Adapter<QuotesAdapter.QuoteViewHolder>() {

    private var filteredList: List<Quote> = originalList

    class QuoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val quoteTextView: TextView = itemView.findViewById(R.id.txtQuotes)
        val authorTextView: TextView = itemView.findViewById(R.id.txtAuthor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return QuoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val currentQuote = filteredList[position]
        holder.quoteTextView.text = currentQuote.text
        holder.authorTextView.text = currentQuote.author
    }

    override fun getItemCount() = filteredList.size

    // Method to filter the list based on search query
    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter { it.author.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }
}
