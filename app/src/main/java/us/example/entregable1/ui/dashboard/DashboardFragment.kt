package us.example.entregable1.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import us.example.entregable1.ItemDetails
import us.example.entregable1.R
import us.example.entregable1.ui.login.LoginActivity
import java.util.*
import kotlin.system.exitProcess

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var auth:FirebaseAuth
    val db = FirebaseFirestore.getInstance()
    private lateinit var statusId: String

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        val root = inflater!!.inflate(R.layout.fragment_dashboard, container, false)
        /*val textView: TextView = root.findViewById(R.id.text_dashboard)
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/

        root.display_name.setText(auth.currentUser?.displayName, TextView.BufferType.EDITABLE)
        root.email.setText(auth.currentUser?.email, TextView.BufferType.EDITABLE)
        statusId = auth.currentUser?.email!!

        db.collection("pecks").document(auth.currentUser?.email!!).get()
            .addOnSuccessListener { document ->
                    if (document.get("peck") != null)
                        root.status_input.setText(document.get("peck").toString(), TextView.BufferType.EDITABLE)
            }

        root.name_button.setOnClickListener {
            val user = auth.currentUser
            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(
                display_name.text.toString()
            ).build()

            if (root.display_name.text.toString() != user?.displayName) {
                user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(activity, "Nombre actualizado!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            if (root.email.text.toString() != user?.email) {
                user?.updateEmail(root.email.text.toString())?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        db.collection("pecks").document(statusId).delete()
                            .addOnSuccessListener {
                                val data = hashMapOf(
                                    "peck" to status_input.text.toString(),
                                    "creation_date" to Timestamp.now(),
                                    "user" to auth.currentUser?.email
                                )
                                db.collection("pecks").document(auth.currentUser?.email!!)
                                    .set(data).addOnCompleteListener {
                                        Toast.makeText(
                                            activity, "Email actualizado!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                    } else {
                        Toast.makeText(activity, task.exception?.message, Toast.LENGTH_SHORT).show()
                    }

                }
            }
            if (root.status_input.text.toString() != null && root.status_input.text.toString() != "") {
                val data = hashMapOf(
                    "peck" to status_input.text.toString(),
                    "creation_date" to Timestamp.now(),
                    "user" to auth.currentUser?.email
                )
                db.collection("pecks").document(statusId).set(data).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(activity, "Estado cambiado!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        root.logout_button.setOnClickListener {
            auth.signOut()
            val intent = Intent(root.context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        /*root.status_button.setOnClickListener {
            val data = hashMapOf(
                "peck" to status_input.text.toString(),
                "creation_date" to Timestamp.now(),
                "user" to auth.currentUser?.email
            )
            db.collection("pecks").document(statusId).set(data).addOnCompleteListener {
                    task ->
                if (task.isSuccessful) {
                    Toast.makeText(activity, "Estado cambiado!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }*/

        return root
    }
}
