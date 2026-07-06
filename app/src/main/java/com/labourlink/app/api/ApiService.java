package com.labourlink.app.api;

import com.labourlink.app.models.LoginRequest;
import com.labourlink.app.models.LoginResponse;
import com.labourlink.app.models.UserInfo;
import com.labourlink.app.models.VerifyForgotOtpRequest;
import com.labourlink.app.models.ResetPasswordRequest;
import com.labourlink.app.models.VerifyOtpRequest;
import com.labourlink.app.models.PhoneRequest;
import okhttp3.ResponseBody;
import com.labourlink.app.models.Service;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import com.labourlink.app.models.NearbyWorker;
import com.labourlink.app.models.Profession;
import retrofit2.http.Path;
import java.util.List;
import com.labourlink.app.models.WorkerProfile;
import com.labourlink.app.models.OwnerCompletedRequest;
import retrofit2.Call;
import java.util.List;
import com.labourlink.app.models.WorkerRatingRequest;
import com.labourlink.app.models.AcceptedRequest;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import com.labourlink.app.models.SendRequest;
import com.labourlink.app.models.OwnerProfile;
import com.labourlink.app.models.OwnerRequestsResponse;
import com.labourlink.app.models.IncomingRequest;
import com.labourlink.app.models.Worker;import com.labourlink.app.models.WorkerRegisterRequest;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
public interface ApiService {

    @POST("api/auth/login")
    Call<LoginResponse> login(
            @Body LoginRequest request
    );
    @POST("otp/send-otp")
    Call<ResponseBody> sendOtp(@Body PhoneRequest request);

    @POST("otp/verify-otp")
    Call<ResponseBody> verifyOtp(@Body VerifyOtpRequest request);
    @POST("forgot-password/send-otp")
    Call<ResponseBody> sendForgotOtp(@Body PhoneRequest request);

    @POST("forgot-password/verify-otp")
    Call<ResponseBody> verifyForgotOtp(@Body VerifyForgotOtpRequest request);

    @POST("forgot-password/reset-password")
    Call<ResponseBody> resetPassword(@Body ResetPasswordRequest request);

    // ===============================
// Get All Professions
// ===============================

    @GET("/api/profession/all")
    Call<List<Profession>> getAllProfessions(
            @Header("Authorization") String token
    );

// ===============================
// Get Nearby Workers
// ===============================
@GET("/api/worker/nearby")
Call<List<NearbyWorker>> getNearbyWorkers(
        @Header("Authorization") String token,
        @Query("lat") double lat,
        @Query("lon") double lon,
        @Query("profession") String profession,
        @Query("date") String date
);
    @GET("/api/worker/profile/{id}")
    Call<WorkerProfile> getWorkerProfile(
            @Header("Authorization") String token,
            @Path("id") Long workerId
    );
    @GET("api/wages/{profession}")
    Call<List<Service>> getProfessionServices(
            @Path("profession") String profession,
            @Header("Authorization") String token
    );


    @POST("api/work/send")
    Call<ResponseBody> sendRequest(
            @Header("Authorization") String token,
            @Body SendRequest request
    );
    @GET("api/owner/profile")
    Call<OwnerProfile> getOwnerProfile(
            @Header("Authorization") String token
    );
    @GET("api/owner/requests")
    Call<OwnerRequestsResponse> getOwnerRequests(
            @Header("Authorization") String token
    );
    @GET("api/worker/me")
    Call<Worker> getMyWorkerProfile(
            @Header("Authorization") String token
    );

    @GET("api/work/accepted")
    Call<List<AcceptedRequest>> getAcceptedRequests(
            @Header("Authorization") String token
    );
    @GET("api/work/completed")
    Call<List<AcceptedRequest>> getCompletedRequests(
            @Header("Authorization") String token
    );
    @GET("api/work/incoming")
    Call<List<IncomingRequest>> getIncomingRequests(
            @Header("Authorization") String token
    );

    @PUT("api/work/accept/{requestId}")
    Call<ResponseBody> acceptRequest(
            @Header("Authorization") String token,
            @Path("requestId") Long requestId
    );

    @PUT("api/work/reject/{requestId}")
    Call<ResponseBody> rejectRequest(
            @Header("Authorization") String token,
            @Path("requestId") Long requestId
    );
    @POST("api/ownerrates/worker")
    Call<ResponseBody> rateWorker(
            @Header("Authorization") String token,
            @Body WorkerRatingRequest request
    );
    @GET("api/owner/completed")
    Call<List<OwnerCompletedRequest>> getOwnerCompletedRequests(
            @Header("Authorization") String token
    );
    @Multipart
    @POST("api/worker/register")
    Call<ResponseBody> registerWorker(
            @Header("Authorization") String token,
            @Part("worker") RequestBody worker,
            @Part MultipartBody.Part photo
    );
    @GET("/api/worker/userinfo")
    Call<UserInfo> getUserInfo(
            @Header("Authorization") String token
    );
}