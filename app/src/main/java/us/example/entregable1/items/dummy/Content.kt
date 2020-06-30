package us.example.entregable1.items.dummy

import android.content.ContentValues.TAG
import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime
import java.util.*

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object Content {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<Item> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, Item> = HashMap()

    private val COUNT = 10
    val db = FirebaseFirestore.getInstance()

    init {
        // Add some sample items.
        Log.d(TAG, "Retrieving pecks...")
        db.collection("pecks")
            .get()
            .addOnSuccessListener { result ->
                var i = 1

                var favorite = false
                var month = (1..3).random()
                var day = (1..28).random()
                var hour = (0..23).random()
                var minute = (0..59).random()
                var date = Calendar.getInstance()
                date.set(2020,month,day,hour,minute,0)
                for (document in result) {
                    val item = Item(i.toString(), document.data.get("peck").toString(), favorite,
                                        date)
                    addItem(item)
                    Log.d(TAG, "${document.id} => ${document.data}")
                    i += 1
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun addItem(item: Item) {
        ITEMS.add(item)
        ITEM_MAP.put(item.id, item)
    }

    private fun createItem(position: Int): Item {
        var favorite = false
        var month = (1..3).random()
        var day = (1..28).random()
        var hour = (0..23).random()
        var minute = (0..59).random()
        var date = Calendar.getInstance()
        date.set(2020,month,day,hour,minute,0)

        return Item(position.toString(), "Item $position", favorite, date)
    }

    /**
     * A dummy item representing a piece of content.
     */
    @Parcelize
    data class Item(val id: String, val content: String, var favorite: Boolean, var dateOfPost: Calendar) : Parcelable {
        override fun toString(): String = content
    }
}
