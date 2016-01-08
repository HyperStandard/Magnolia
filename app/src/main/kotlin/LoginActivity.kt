package rose.blossom.magnolia

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import de.halfbit.tinybus.Bus
import de.halfbit.tinybus.Subscribe
import de.halfbit.tinybus.TinyBus
import okhttp3.*
import kotlin.concurrent.thread

data class LoginEvent(var success: Boolean, var message: String = "default")

public class OkHttpClientManager private constructor() {
    public var client: OkHttpClient;

    init {
        client = OkHttpClient()
    }

    private object Holder {
        val INSTANCE = OkHttpClientManager()
    }

    companion object {
        val instance: OkHttpClientManager by lazy { Holder.INSTANCE }
    }


}

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private val mTag: String = "Magnolia${'$'}Login"

    // UI references.
    private var mEmailView: AutoCompleteTextView? = null
    private var mPasswordView: EditText? = null
    private var mProgressView: View? = null
    private var mLoginFormView: View? = null

    private var prefs: SharedPreferences? = null;

    private var mBus: Bus? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Set up the login form.
        mEmailView = findViewById(R.id.email) as AutoCompleteTextView

        prefs = this.getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        mBus = TinyBus.from(this);

        populateAutoComplete()

        mPasswordView = findViewById(R.id.password) as EditText
        mPasswordView!!.setOnEditorActionListener(TextView.OnEditorActionListener { textView, id, keyEvent ->
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        val mEmailSignInButton = findViewById(R.id.email_sign_in_button) as Button
        mEmailSignInButton.setOnClickListener { attemptLogin() }

        mLoginFormView = findViewById(R.id.login_form)
        mProgressView = findViewById(R.id.login_progress)
    }

    override fun onStart() {
        super.onStart();
        mBus?.register(this);
    }

    override fun onStop() {
        mBus?.unregister(this);
        super.onStop();
    }

    private fun populateAutoComplete() {
        //TODO: populate later
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        //TODO: stuff later

        val password: String = mPasswordView?.text.toString()
        val username: String = mEmailView?.text.toString()
        val formBody: RequestBody

        if ( username == "" || password == "") {
            Snackbar.make(mEmailView, R.string.error_field_required, Snackbar.LENGTH_SHORT)
        } else {
            var LoginURL: String;
            if (true) {
                Log.i(mTag, "Using iPhone login")
                LoginURL = "https://iphone.endoftheinter.net/"
                formBody = FormBody.Builder().add("username", username).add("password", password).build()
            } else {
                Log.i(mTag, "Using Desktop login")
                LoginURL = "https://endoftheinter.net/"
                formBody = FormBody.Builder().add("b", username).add("p", password).add("r", "").build()
            }

            val request: Request = Request.Builder()
                    .url(LoginURL)
                    .post(formBody)
                    .build();


            showProgress(true)
            thread {
                var response: Response = OkHttpClientManager.instance.client.newCall(request).execute();
                val test: String = response.body().string()
                Log.e(mTag, test)
                if ( test.contains("<script>document.location.href=\"/\";</script>")) {
                    mBus?.post(LoginEvent(true, "logged in"))
                } else {
                    if(test.contains("Invalid username or password."))
                    {
                        mBus?.post(LoginEvent(false, "wrong password"))
                    } else {
                        mBus?.post(LoginEvent(false))
                    }

                }
            }
        }

    }

    @Subscribe
    public fun onReceiveLoginEvent(message: LoginEvent) {
        showProgress(false)
        Log.e(mTag, "got event from subscriber")
        val (successful, text) = message
        if ( successful) {
            Log.e(mTag, "Success")
            val intent: Intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        } else {
            Snackbar.make(mEmailView, "Wrong password or user account", Snackbar.LENGTH_INDEFINITE).setAction("OK", View.OnClickListener { /* */ }).show()
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

            mLoginFormView!!.visibility = if (show) View.GONE else View.VISIBLE
            mLoginFormView!!.animate().setDuration(shortAnimTime.toLong()).alpha(
                    (if (show) 0 else 1).toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mLoginFormView!!.visibility = if (show) View.GONE else View.VISIBLE
                }
            })

            mProgressView!!.visibility = if (show) View.VISIBLE else View.GONE
            mProgressView!!.animate().setDuration(shortAnimTime.toLong()).alpha(
                    (if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mProgressView!!.visibility = if (show) View.VISIBLE else View.GONE
                }
            })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView!!.visibility = if (show) View.VISIBLE else View.GONE
            mLoginFormView!!.visibility = if (show) View.GONE else View.VISIBLE
        }
    }
}

