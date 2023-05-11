package son.mad9132_final

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import son.mad9132_final.databinding.ActivityMainBinding

/**
 * CREATED BY SON TRAN
 * */
class MainActivity : AppCompatActivity() {
    // region Properties
    private lateinit var binding: ActivityMainBinding
    private val localStorage = LocalStorage()

    @Suppress("UNUSED")
    private var searchString = ""

    private val minPage = 1
    private val maxPage = 100
    private val startPage = 30

    private val baseUrl = "https://api.github.com/search/"
    // endregion

    // region onCreate Method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var internetConnection = InternetConnected(this)

        if (!internetConnection.isConnected) {
            AlertDialog.Builder(this)
                .setTitle(R.string.message_title)
                .setMessage(R.string.message_text)
                .setIcon(R.drawable.ic_baseline_network_check_24)
                .setNegativeButton(R.string.quit){ _, _ ->
                    finish()
                }
                .setCancelable(false)
                .show()
        } else {



        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Click Listener for Search Button
        binding.searchButton.setOnClickListener {
            fetchJSONData()
        }

        // Initial set up for our properties.
        binding.perPageNumberPicker.minValue = minPage
        binding.perPageNumberPicker.maxValue = maxPage
        binding.perPageNumberPicker.value = startPage

        // region Local Storage
        if(localStorage.contains(getString(R.string.repos_key))) {
            binding.minReposEditText.setText(localStorage.getValueString(getString(R.string.repos_key)))
            print(getString(R.string.repos_key))
        } else {
            binding.minReposEditText.setText("0")
        }

        if(localStorage.contains(getString(R.string.followers_key))) {
            binding.minFollowersEditText.setText(localStorage.getValueString(getString(R.string.followers_key)))
        } else {
            binding.minFollowersEditText.setText("0")
        }

        if(localStorage.contains(getString(R.string.page_size_key))) {
            binding.perPageNumberPicker.value = localStorage.getValueInt(getString(R.string.page_size_key))
        } else {
            binding.perPageNumberPicker.value = 0
        }
        // endregion
        }
    }

    // region onStop Method
    override fun onStop() {
        super.onStop()
        localStorage.save(getString(R.string.repos_key), binding.minReposEditText.text.toString())
        localStorage.save(getString(R.string.followers_key), binding.minFollowersEditText.text.toString())
        localStorage.save(getString(R.string.page_size_key), binding.perPageNumberPicker.value.toString().toInt())
    }
    // endregion

    // region fetchJSONData
    private fun fetchJSONData() {
        //Retrofit Object
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val restApi = retrofit.create(RestApi::class.java)

        // Check if the search string is empty or not, if it is empty, then we will give an value.
        if(TextUtils.isEmpty(binding.minFollowersEditText.text)){
            binding.minFollowersEditText.setText("0")
        }

        if(TextUtils.isEmpty(binding.minReposEditText.text)){
            binding.minReposEditText.setText("0")
        }

        // Get the minFollowers & minRepos text in an Int format.
        val minNumberOfFollowers = binding.minFollowersEditText.text.toString().toInt()
        val minNumberOfRepos = binding.minReposEditText.text.toString().toInt()

        val searchString = "${binding.searchUser.text} repos:>=$minNumberOfRepos followers:>=$minNumberOfFollowers"

        val call = restApi.getUserData(searchString, binding.perPageNumberPicker.value)

        binding.progressBar.visibility = View.VISIBLE

        call.enqueue(object: Callback<ResponseDataClass> {
            override fun onResponse(
                call: Call<ResponseDataClass>,
                response: Response<ResponseDataClass>
            ) {
                val responseBody = response.body()

                val users = responseBody?.items
                val numberOfUsers = users?.size ?: 0

                binding.progressBar.visibility = View.GONE

                // If we have users, then we will show the recycler view and hide the empty view.
                if(numberOfUsers > 0){
                    val intent = Intent(TheApp.context, ResultsActivity::class.java)
                    intent.putParcelableArrayListExtra(getString(R.string.user_data_key), users)
                    startActivity(intent)
                } else {
                    Toast.makeText(TheApp.context, "No users found!", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<ResponseDataClass>, t: Throwable) {
                toast(t.message.toString())
                binding.progressBar.visibility = View.GONE
            }

        })
    }
    // endregion

    // region Options Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = MenuInflater(this)
        inflater.inflate(R.menu.main_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId ) {
            R.id.menu_about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    // endregion

    // region Keyboard - Hide the soft keyboard when no input control has focus.
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {

        currentFocus?.let {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }

        return super.dispatchTouchEvent(ev)
    }
    // endregion

    // region toast
    fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    // endregion
}