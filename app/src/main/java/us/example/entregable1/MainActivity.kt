package us.example.entregable1

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import us.example.entregable1.items.FavoritesFragment
import us.example.entregable1.items.ItemFragment
import us.example.entregable1.items.dummy.Content
import us.example.entregable1.ui.login.LoginActivity


class MainActivity : AppCompatActivity() , ItemFragment.OnItemListFragmentInteractionListener, FavoritesFragment.OnFavListFragmentInteractionListener {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        setSupportActionBar(action_bar)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                 R.id.navigation_dashboard, R.id.navigation_home, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()

        val user = auth.currentUser

        if (user != null) {
            Toast.makeText(applicationContext, "You are now logged in!" , Toast.LENGTH_SHORT).show()

            val name = user.displayName
            actionBar?.setTitle("Hello, ${name}")
        } else {
            Toast.makeText(applicationContext, "Could not login!" , Toast.LENGTH_SHORT).show()
            //val intent = Intent(this, LoginActivity::class.java)
            //startActivity(intent)
            //finish()
        }
    }

    override fun onItemListFragmentInteraction(item: Content.Item?) {

        val rv: RecyclerView
        rv = findViewById(R.id.list)

        rv.adapter?.notifyDataSetChanged()

        val intent = Intent(this, ItemDetails::class.java)

        intent.putExtra("item", item)
        startActivity(intent)
        finish()
    }

    override fun onFavListFragmentInteraction(item: Content.Item?) {
        val rv: RecyclerView
        rv = findViewById(R.id.favoriteList)

        rv.adapter?.notifyDataSetChanged()

        val intent = Intent(this, ItemDetails::class.java)

        intent.putExtra("item", item)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.top_nav_menu, menu)
        return true
    }

    fun onFilterNavigation(mi: MenuItem) {
        val intent = Intent(this, FilterActivity::class.java)

        startActivity(intent)
        finish()
    }

    fun onRefresh(mi: MenuItem) {
        val rv: RecyclerView
        rv = findViewById(R.id.list)

        if (rv != null) {
            rv.removeAllViews()
            rv.adapter?.notifyDataSetChanged()
        }

    }
}
