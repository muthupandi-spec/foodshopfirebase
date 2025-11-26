package com.foodpartner.app.appControl

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
//import com.foodpartner.app.BuildConfig
import com.foodpartner.app.R
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import com.foodpartner.app.utility.di.apiModule
import com.foodpartner.app.utility.di.netModule
import com.foodpartner.app.utility.di.repositoryModule
import com.foodpartner.app.utility.di.viewModelModule
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import timber.log.Timber
import timber.log.Timber.DebugTree


class AppController : Application() {
    lateinit var mSocket: Socket
    var isConnected: Boolean = false

    override fun onCreate() {
        super.onCreate()
        createSocketConnection()
        //logging module
    /*    if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }*/

        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
//                            .setDefaultFontPath(getString(R.string.app_font))
//                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )
        startKoin {
            androidContext(this@AppController)
            androidLogger(Level.DEBUG)
            modules(listOf( repositoryModule, netModule, apiModule, viewModelModule))
        }
    }

    fun createSocketConnection() {
        mSocket = IO.socket("https://dev.abserve.tech:4007")
        mSocket.connect()
        mSocket.on(Socket.EVENT_CONNECT, onConnect)
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectingError)
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectingTimeout)
        mSocket.on("new order placed", onNewMessage)

    }

    fun emitcurrentlocation(event: String, emit: JSONObject) {
        try {
            if (isConnected) {
                mSocket?.emit(event, "$emit")
                println("eventEmitted"+emit)
            } else {
                println("Socket Not Connected")
            }

        } catch (e: Exception) {
            println("eventException ${e.message.toString()}")
        }


    }

    private val onConnect = Emitter.Listener {
        Handler(Looper.getMainLooper()).post {
            try {
                isConnected = true
                mSocket.emit("create connection", "part_" + "User_id")
                println("enter the socket id" + mSocket.id())
                Log.e("clientsocket", "connect")
            } catch (e: Exception) {
                println("connectingException ${e.message.toString()}")
            }
        }
    }

    private val onConnectingError = Emitter.Listener {
        try {
            Handler(Looper.getMainLooper()).post {
                onConnect
            }
        } catch (e: Exception) {
            println("connectingErrorException ${e.message.toString()}")
        }
    }

    private val onConnectingTimeout = Emitter.Listener {
        Handler(Looper.getMainLooper()).post {
            println("Socket connecting Timeout held")
            onConnect
        }
    }
    private val onNewMessage =
        Emitter.Listener { args ->
            Handler(Looper.getMainLooper()).post {
                val data = args[0] as JSONObject
                println("Json new order data$data")
                var strPartner_id = ""
//                EventBus.getDefault().postSticky(NewOrders("New orders found in your restaurant"))
                if (data.has("partner_id")) {
                    strPartner_id = data.optString("partner_id")
                    Log.e("Checking socket", data.optString("partner_id"))
                }
                Log.e("ClientSocket", "$strPartner_id neworder")
                /*if (strPartner_id.equals(user_id, ignoreCase = true)) {
                    EventBus.getDefault()
                        .postSticky(NewOrders("New orders found in your restaurant"))
                }*/
            }
        }


    /** A tree which logs important information for crash reporting.  */
    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
        }
    }
}