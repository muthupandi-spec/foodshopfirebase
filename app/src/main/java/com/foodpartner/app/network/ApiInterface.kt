package com.foodpartner.app.network

import com.foodpartner.app.ResponseMOdel.AcceptOrderResponsemodel
import com.foodpartner.app.ResponseMOdel.AssignorderModel
import com.foodpartner.app.ResponseMOdel.CancelOrderResponseModel
import com.foodpartner.app.ResponseMOdel.CancelledOrderResponsemodel
import com.foodpartner.app.ResponseMOdel.CompletedOrderResponseModel
import com.foodpartner.app.ResponseMOdel.CreateFoodResponseModel
import com.foodpartner.app.ResponseMOdel.EnablePackingModel
import com.foodpartner.app.ResponseMOdel.FoodItemResponemodel
import com.foodpartner.app.ResponseMOdel.GetallCategoryResponseModel
import com.foodpartner.app.ResponseMOdel.HomeMOdel
import com.foodpartner.app.ResponseMOdel.OrderDetailResponsemodel
import com.foodpartner.app.ResponseMOdel.OrderRecieveModell
import com.foodpartner.app.ResponseMOdel.RestaurantResponsemodel
import com.foodpartner.app.ResponseMOdel.TrackorderRespossemodel
import com.foodpartner.app.ResponseMOdel.UpdateFoodResponseModel
import com.foodpartner.app.ResponseMOdel.UpdatecategoryResponsemodel
import com.foodpartner.app.view.fragment.ShopCreateFragment
import com.foodpartner.app.view.requestmodel.CreateCategoryRequest
import com.foodpartner.app.view.responsemodel.CreateCategoryResponseModel
import com.foodpartner.app.view.responsemodel.LoginResponseModel
import com.foodpartner.app.view.responsemodel.Notificationmodel
import com.foodpartner.app.view.responsemodel.OtpResponseModel
import com.foodpartner.app.view.responsemodel.ShopCreateResponsemodel
import com.foodpartner.app.view.responsemodel.UserregisterResponseModel
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiInterface {
    @GET("user")
    fun getuser(@QueryMap params: HashMap<String, String>): Observable<HomeMOdel>
    @GET("restaurant/api/orders/vieworderrestaurantid/{id}/{type}")
    fun ordererrecieve(@Path("id") id: String,@Path("type") type: String): Observable<OrderRecieveModell>
    @GET("restaurant/api/restaurantcatagory/getallcatagory/{id}")
    fun getallcategory(@Path("id") id: String): Observable<GetallCategoryResponseModel>
    @GET("restaurant/api/restaurant/getrestaurant/{id}")
    fun getrestaurant(@Path("id") id: String): Observable<RestaurantResponsemodel>
    @POST("restaurant/api/auth/login")
    fun login(@Body params: HashMap<String, String>): Observable<LoginResponseModel>   @PUT("verifyotp")
    fun otp(@Body params: HashMap<String, String>): Observable<OtpResponseModel>
    @POST("userregister")
    fun register(@Body params: HashMap<String, String>): Observable<UserregisterResponseModel>
    @POST("restaurant/api/restaurant/createRestaurant")
    fun shopcreate(@Body params: HashMap<String, String>): Observable<ShopCreateResponsemodel>
    @POST("restaurant/api/restaurant/updaterestaurant/{id}")
    fun updaterestaurat(@Path("id") id: String,@Body params: HashMap<String, String>): Observable<ShopCreateResponsemodel>
    @GET("orderdetail")
    fun orderdetail(@QueryMap params: HashMap<String, String>): Observable<HomeMOdel>
    @PUT("restaurant/api/orders/acceptorder/{id}")
    fun acceptorder(@Path("id") id: String): Observable<AcceptOrderResponsemodel>
    @PUT("restaurant/api/orders/enablePacking/{id}")
    fun enablepacking(@Path("id") id: String): Observable<EnablePackingModel>
    @PUT("restaurant/api/orders/assign-partner/{id}")
    fun assignpartner(@Path("id") id: String): Observable<AssignorderModel>
    @GET("restaurant/api/food/getcatagoryid")
    fun getcategoryfooditem(@QueryMap params: HashMap<String, String>): Observable<FoodItemResponemodel>
    @POST("restaurant/api/restaurantcatagory/createcatagory")
    fun createcategory(
        @Body request: CreateCategoryRequest
    ): Observable<CreateCategoryResponseModel>
    @DELETE("restaurant/api/orders/cancelorder/{id}")
    fun cancelorder(@Path("id") id: String): Observable<CancelledOrderResponsemodel>
    @POST("orderstatuschange")
    fun statuschange(@Body params: HashMap<String, String>): Observable<UserregisterResponseModel>

    @GET("restaurant/api/location/track{id}")
    fun trackorder(@Path("id") id: String): Observable<TrackorderRespossemodel>
    @PUT("updateshop")
    fun updateshop(@Body params: HashMap<String, String>): Observable<HomeMOdel>
    @PATCH("restaurant/api/food/update/isActive/{id}")
    fun updatecategory(@Path("id") id: String,@QueryMap params: HashMap<String, String>): Observable<UpdatecategoryResponsemodel>
    @Multipart
    @POST("restaurant/api/food/createfood")
    fun addfooditem(@Part avatar: MultipartBody.Part, @Part("food") food: RequestBody): Observable<CreateFoodResponseModel>
    @Multipart
    @POST("restaurant/api/food/update/{id}")
    fun updatefood(@Path("id") id: String, @Part avatar: MultipartBody.Part, @Part("food") food: RequestBody): Observable<UpdateFoodResponseModel>
    @GET("restaurant/api/orders/vieworderrestaurantid/{id}/{type}")
    fun completedorders(@Path("id") id: String,@Path("type") type: String,): Observable<CompletedOrderResponseModel>
    @GET("restaurant/api/orders/vieworderid/{orderId}")
    fun vieworder(@Path("orderId") id: String): Observable<OrderDetailResponsemodel>
    @GET("getfooditem")
    fun getfooditem(@QueryMap params: HashMap<String, String>): Observable<HomeMOdel>
    @GET("restaurant/api/orders/vieworderrestaurantid/{id}/{type}")
    fun cancelledorders(@Path("id") id: String,@Path("type") type: String): Observable<CancelOrderResponseModel>
    @PUT("updateprofile")
    fun updateprofile(@Body params: HashMap<String, String>): Observable<HomeMOdel>
    @PUT("updatebusines")
    fun updatebusines(@Body params: HashMap<String, String>): Observable<HomeMOdel>
    @PUT("updatebank")
    fun updatebank(@Body params: HashMap<String, String>): Observable<HomeMOdel>
    @PUT("updateshop")
    fun updateshopp(@Body params: HashMap<String, String>): Observable<HomeMOdel>
    @DELETE("Deleteaccount")
    fun deleteaccount(@Body params: HashMap<String, String>): Observable<HomeMOdel>
    @GET("restaurant/api/notifications/{token}")
    fun getnotification(@Path("token") id: String): Observable<Notificationmodel>

    @POST("logout")
    fun logout(@Body params: HashMap<String, String>): Observable<HomeMOdel>
    @POST("restaurant/api/restaurant/verify-otp")
    fun verifyOtp(
        @Query("mobileNumber") mobileNumber: String,
        @Query("otp") otp: String
    ): Observable<OtpResponseModel>

}