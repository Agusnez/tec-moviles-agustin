package us.example.entregable1.items.dummy

import java.util.*

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<DummyItem> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, DummyItem> = HashMap()

    /**
     * A filter
     */
    val startDate: Calendar = Calendar.getInstance()
    val endDate: Calendar = Calendar.getInstance()

    var COLUMNS = 1

    private val COUNT = 25

    init {
        // Add some sample items.
        for (i in 1..COUNT) {
            addItem(createDummyItem(i))
        }
        startDate.set(Calendar.YEAR, 2019)
        startDate.set(Calendar.MONTH, 0)
        startDate.set(Calendar.DATE, 1)
    }

    private fun addItem(item: DummyItem) {
        ITEMS.add(item)
        ITEM_MAP.put(item.id, item)
    }

    private fun createDummyItem(position: Int): DummyItem {
        return DummyItem(position.toString(), "Item " + position, makeDetails(position))
    }

    private fun makeDetails(position: Int): String {
        val builder = StringBuilder()
        builder.append("Details about Item: ").append(position)
        for (i in 0..position - 1) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }

    public fun getFilteredItems(): MutableList<DummyItem> {
        return ITEMS
    }

    /**
     * A dummy item representing a piece of content.
     */
    data class DummyItem(val id: String, val content: String, val details: String) {
        override fun toString(): String = content
    }
}
