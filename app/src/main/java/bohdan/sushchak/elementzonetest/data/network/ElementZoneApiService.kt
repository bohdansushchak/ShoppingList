package bohdan.sushchak.elementzonetest.data.network

import bohdan.sushchak.elementzonetest.data.network.responces.LoginData
import bohdan.sushchak.elementzonetest.data.network.responces.MyResponse
import bohdan.sushchak.elementzonetest.data.network.responces.Order
import bohdan.sushchak.elementzonetest.internal.Constants
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.http.*

interface ElementZoneApiService {

    @FormUrlEncoded
    @POST("/login")
    fun logInAsync(@Field("email") email: String,
                   @Field("password") password: String
    ):Deferred<Response<MyResponse<LoginData>>>

    @FormUrlEncoded
    @POST("/refresh")
    fun refreshTokenAsync(@Field("api_token") apiToken: String
    ):Deferred<Response<MyResponse<Boolean>>>

    @FormUrlEncoded
    @POST("/orders")
    fun getOrdersAsync(@Field("api_token") apiToken: String
    ):Deferred<Response<MyResponse<List<Order>>>>

    @FormUrlEncoded
    @POST("/addOrder")
    fun addOrderAsync(@Field("api_token") apiToken: String,
                      @Field("date") date: String,
                      @Field("location") location: String,
                      @Field("price") price: Float,
                      @Field("items[]") items: List<String>,
                      @Field("shop_name") shopName: String
    ):Deferred<Response<MyResponse<Order>>>

    @FormUrlEncoded
    @POST("/order/{link}")
    fun getOrderByLinkAsync(@Field("api_token") apiToken: String,
                       @Path("link") link: String
    ):Deferred<Response<MyResponse<Any>>>

    @FormUrlEncoded
    @POST("/generate")
    fun generateLinkAsync(@Field("api_token") apiToken: String,
                                 @Field("id") id: Long
    ):Deferred<Response<MyResponse<Map<String, String>>>>

    companion object {
        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor
        ): ElementZoneApiService {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(logging)
            httpClient.addInterceptor(connectivityInterceptor)

            return Retrofit.Builder()
                .client(httpClient.build())
                .baseUrl(Constants.BASE_URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ElementZoneApiService::class.java)
        }
    }
}