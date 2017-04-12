import android.util.Log
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.*

/**
 * Created by Andrew on 1/9/2016.
 */
/*class PersistentCookieJar private constructor() : CookieJar {
    override fun saveFromResponse(url: HttpUrl?, cookies: MutableList<Cookie>?) {
        if(url != null && cookies != null) {
            localCookieStore.put(url.host(), cookies)
        }
    }

    override fun loadForRequest(url: HttpUrl?): MutableList<Cookie>? {
        if(url != null && localCookieStore.containsKey(url.host())) {
            return localCookieStore.get(url.host())
        } else return null
    }

    private var localCookieStore: HashMap<String, MutableList<Cookie>>

    init {
        Log.e("cookies", "stored")
        localCookieStore = HashMap<String, MutableList<Cookie>>()
    }

    private object Holder {
        val INSTANCE = PersistentCookieJar()
    }

    companion object {
        val instance: PersistentCookieJar by lazy { Holder.INSTANCE }
    }

}*/