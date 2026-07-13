package com.labourlink.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.labourlink.app.api.ApiClient;
import com.labourlink.app.api.ApiService;
import com.labourlink.app.models.TrackingStatusDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;
public class TrackingStatusManager {

    private final Context context;

    public TrackingStatusManager(Context context) {
        this.context = context;
    }

    // Callback interface
    public interface TrackingCallback {
        void onResult(boolean trackingEnabled);
    }

    public void checkTrackingStatus(TrackingCallback callback) {

        SharedPreferences preferences =
                context.getSharedPreferences("LabourLink", Context.MODE_PRIVATE);

        String token = preferences.getString("token", "");
        Log.d("TRACKING", "Checking tracking status...");
        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        apiService.getTrackingStatus("Bearer " + token)
                .enqueue(new Callback<TrackingStatusDto>() {

                    @Override
                    public void onResponse(Call<TrackingStatusDto> call,
                                           Response<TrackingStatusDto> response) {
                        Log.d("TRACKING", "Response Code: " + response.code());

                        if (response.body() != null) {
                            Log.d("TRACKING", "Tracking Enabled: " +
                                    response.body().getTrackingEnabled());
                        }
                        if (response.isSuccessful() && response.body() != null) {

                            callback.onResult(
                                    Boolean.TRUE.equals(
                                            response.body().getTrackingEnabled()
                                    )
                            );

                        } else {

                            callback.onResult(false);

                        }
                    }

                    @Override
                    public void onFailure(Call<TrackingStatusDto> call,
                                          Throwable t) {
                        Log.e("TRACKING", "API Failed: " + t.getMessage());
                        callback.onResult(false);

                    }
                });
    }
}