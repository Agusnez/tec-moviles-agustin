package us.example.entregable1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.item_details.*
import us.example.entregable1.items.dummy.Content
import us.example.entregable1.utils.AsyncTaskLoadImage
import java.text.SimpleDateFormat


class ItemDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_details)

        val item: Content.Item = intent.getParcelableExtra("item")

        getSupportActionBar()?.setTitle("Item " + item.id)

        title_text.setText("Esta es la vista de detalle del item " + item.id  )
        val url = "https://api.adorable.io/avatars/300/${item.id}"
        AsyncTaskLoadImage(profile_pic_details).execute(url)

        val format = SimpleDateFormat("yyyy-MM-dd HH:MM")
        date_details.text = "${format.format(item.dateOfPost.time)}"

        val favButton = findViewById<ImageButton>(R.id.fav_button)

        favButton.setOnClickListener { addToFavorites(favButton, item) }

        Log.d("INFO", item.toString() + " ---> " + item.favorite)

        if (item.favorite) {
            (favButton as ImageButton).setImageResource(R.drawable.ic_star_filled)
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

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)

        startActivity(intent)
        finish()
    }
}
