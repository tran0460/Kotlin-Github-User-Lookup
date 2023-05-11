package son.mad9132_final

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import son.mad9132_final.databinding.ActivityResultsBinding

/**
 * CREATED BY SON TRAN
 * */

class ResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultsBinding
    // region onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the Intent that started this activity and extract the string
        val data: ArrayList<Users>?

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            data = intent.getParcelableArrayListExtra(getString(R.string.user_data_key), Users::class.java)
        } else {
            @Suppress("DEPRECATION")
            data = intent.getParcelableArrayListExtra(getString(R.string.user_data_key))
        }

        supportActionBar?.title = "${data?.size} Results"

        // Create the adapter and set it to the RecyclerView
        binding.recyclerViewMain.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMain.adapter = data?.let {
            CustomViewHolderClass.MainAdapter(it)
        }
    }
    // endregion
}