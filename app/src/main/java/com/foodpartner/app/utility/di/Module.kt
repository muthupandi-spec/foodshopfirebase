package com.foodpartner.app.utility.di

import android.annotation.SuppressLint
import android.app.Application
import com.foodpartner.app.utility.CommonFunction
import com.foodpartner.app.utility.SharedHelper
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.foodpartner.app.network.ApiInterface
import com.foodpartner.app.network.CommonApi
import com.foodpartner.app.utility.rx.SchedulersFacade
import com.foodpartner.app.viewModel.HomeViewModel
import com.foodpartner.app.viewModel.LoginViewModel
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

val viewModelModule = module {
 ///   private val loginAndRegisterViewModel by viewModel<LoginAndRegisterViewModel>()  --> inject activity or fragment and so on
    single { HomeViewModel(get()) }
    single { LoginViewModel(get()) }
}

val apiModule = module {

    fun provideUserApi(retrofit: Retrofit): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }

    single { provideUserApi(get()) }
}
val netModule = module {

    fun getRxJavaRetrofit(build : OkHttpClient.Builder): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://margot-nonscandalous-untunably.ngrok-free.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(build.build())
            .build()
    }

    fun getUnsafeOkHttpClient(sharedHelper: SharedHelper): OkHttpClient.Builder? {
        return try { // Create a trust manager that does not validate certificate chains
            val trustAllCerts =
                arrayOf<TrustManager>(
                    object : X509TrustManager {
                        @SuppressLint("TrustAllX509TrustManager")
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return arrayOf()
                        }
                    }
                )
            println("emter access token"+sharedHelper.getFromUser("token"))
            val interceptor =
                Interceptor { chain: Interceptor.Chain ->
                    val newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", sharedHelper.getFromUser("token"))
                        .build()
                    chain.proceed(newRequest)
                }
            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // or use Level.BASIC if you only want URL and status
            }
/*

            if (BuildConfig.DEBUG)
                logging.level = HttpLoggingInterceptor.Level.BODY
            else
                logging.level = HttpLoggingInterceptor.Level.NONE
*/

            builder.addInterceptor(loggingInterceptor)
            builder.addInterceptor(interceptor)
            builder.retryOnConnectionFailure(true)
            builder.writeTimeout(60, TimeUnit.SECONDS)
            builder.connectTimeout(60, TimeUnit.SECONDS)
            builder.readTimeout(60, TimeUnit.SECONDS).build()
            builder.sslSocketFactory(
                sslSocketFactory,
                (trustAllCerts[0] as X509TrustManager)
            )
            builder.hostnameVerifier(HostnameVerifier { hostname: String?, session: SSLSession? -> true })
            builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    single { getRxJavaRetrofit(get()) }
    single { getUnsafeOkHttpClient(get()) }
}

val repositoryModule = module {
    single { SharedHelper(get()) }
    single { SchedulersFacade() }
    single { CommonFunction(get()) }
    fun CommonApiCALL(application: Application,
                      sharedHelper: SharedHelper,
                      api: ApiInterface,
                      schedulersFacade: SchedulersFacade
    ): CommonApi {
        return CommonApi(application, sharedHelper,api,schedulersFacade)
    }
    single { CommonApiCALL(get(),get(),get(),get()) }
}