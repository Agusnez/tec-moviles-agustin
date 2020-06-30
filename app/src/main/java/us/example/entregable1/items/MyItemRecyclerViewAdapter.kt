package us.example.entregable1.items


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_item.view.*
import us.example.entregable1.ItemDetails
import us.example.entregable1.R
import us.example.entregable1.items.ItemFragment.OnItemListFragmentInteractionListener
import us.example.entregable1.items.dummy.Content
import us.example.entregable1.items.dummy.Content.Item
import us.example.entregable1.items.dummy.DummyContent
import us.example.entregable1.utils.AsyncTaskLoadImage
import java.text.SimpleDateFormat
import java.util.*


/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyItemRecyclerViewAdapter(
    private var mValues: List<Item>,
    private val mListener: OnItemListFragmentInteractionListener?
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Item
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onItemListFragmentInteraction(item)

            mValues = mValues.filter { it.dateOfPost.time > DummyContent.startDate.time && it.dateOfPost.time < DummyContent.endDate.time }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        val url = "https://api.adorable.io/avatars/150/${item.id}"
        AsyncTaskLoadImage(holder.mProfilePic).execute(url)
        holder.mContentView.text = item.content
        holder.mFavorited.setImageResource(if (item.favorite) R.drawable.ic_star_filled else R.drawable.ic_star_empty)
        val format = SimpleDateFormat("yyyy-MM-dd")
        holder.mDate.text = "${format.format(item.dateOfPost.time)}"

        holder.mFavorited.setOnClickListener {
            Toast.makeText(holder.mView.context, "Has marcado como favorito el item ${item.id}", Toast.LENGTH_SHORT).show()
            addToFavorites(holder.mFavorited,item)
        }

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mProfilePic: ImageView = mView.profile_pic
        val mContentView: TextView = mView.content
        val mFavorited: ImageButton = mView.fav_icon
        val mDate: TextView = mView.date

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }

    fun addToFavorites(button: View, item: Content.Item) {

        if (item.favorite) {
            (button as ImageButton).setImageResource(R.drawable.ic_star_empty)
            item.favorite = false

        } else {
            (button as ImageButton).setImageResource(R.drawable.ic_star_filled)
            item.favorite = true

        }
        Content.ITEMS[item.id.toInt() - 1] = item
    }

}
