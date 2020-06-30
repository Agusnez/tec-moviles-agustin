package us.example.entregable1.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class DashboardViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _text = MutableLiveData<String>().apply {
        value = "Bienvenido! ${auth.currentUser?.displayName}"
    }
    val text: LiveData<String> = _text
}