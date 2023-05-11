package son.mad9132_final

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * CREATED BY SON TRAN
 * */

class TheApp: Application() {
    // region onCreate
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
    // endregion

    // region companion object
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set
    }
    // endregion
}