package us.example.entregable1.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import us.example.entregable1.MainActivity
import us.example.entregable1.R


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1  // The request code
    private lateinit var auth: FirebaseAuth
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance();

        sign_in_button.setSize(SignInButton.SIZE_STANDARD)

        sign_in_button.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                auth.signInWithEmailAndPassword(username.text.toString(),password.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            var uid = auth.currentUser?.uid
                            loading.visibility = View.INVISIBLE
                            Toast.makeText(
                                applicationContext,
                                "Welcome! $uid",
                                Toast.LENGTH_LONG
                            ).show()
                            updateUI(auth.currentUser!!)
                        } else {
                            loading.visibility = View.INVISIBLE
                            if (task.exception is FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(
                                    applicationContext,
                                    "Failed to login with provided credentials",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else if (task.exception is FirebaseAuthInvalidUserException) {
                                auth.createUserWithEmailAndPassword(
                                    username.text.toString(), password.text.toString()
                                )
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            var uid = auth.currentUser?.uid
                                            loading.visibility = View.INVISIBLE
                                            Toast.makeText(
                                                applicationContext,
                                                "Signed id! Your UID is $uid",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            updateUI(auth.currentUser!!)
                                        } else {
                                            Toast.makeText(
                                                applicationContext,
                                                "Error while signing in: ${task.exception?.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Error while loging in: ${task.exception?.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
            }

        }
    }

    override fun onStart() {
        super.onStart()

        //val account = GoogleSignIn.getLastSignedInAccount(this)
        val account = auth.currentUser
        if (account != null) {
            Toast.makeText(applicationContext, "Login Successful!" , Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        //updateUI(account)
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("TAG", "ACCOUNT ------> ${account.serverAuthCode}")
                Toast.makeText(
                    applicationContext,
                    "Signed in with google: ${account.id}",
                    Toast.LENGTH_LONG
                ).show()
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.d("TAG","Sign in with google failed: $e")
                Toast.makeText(
                    applicationContext,
                    "Sign in with google failed: ${e}",
                    Toast.LENGTH_LONG
                ).show()
                // ...
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    Toast.makeText(
                        applicationContext,
                        "Signed in! ${user?.uid}",
                        Toast.LENGTH_LONG
                    ).show()
                    updateUI(user!!)
                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "signInWithCredential:failure", task.exception)
                    // ...
                    Toast.makeText(
                        applicationContext,
                        "Error while loging with firebase: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
    }

    private fun updateUI(user: FirebaseUser) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account =
                completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            //updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(applicationContext, "There has been an error", Toast.LENGTH_SHORT).show()
            //updateUI(null)
        }
    }

}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
