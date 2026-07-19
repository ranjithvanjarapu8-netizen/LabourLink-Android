package com.labourlink.app.activities;
import java.io.ByteArrayOutputStream;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.labourlink.app.models.UserInfo;
import com.labourlink.app.models.WorkerRegisterRequest;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import okhttp3.ResponseBody;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.labourlink.app.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.content.Intent;
import android.content.IntentSender;
import com.google.android.material.chip.Chip;
import com.labourlink.app.api.ApiClient;
import com.labourlink.app.api.ApiService;
import com.labourlink.app.models.Profession;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.activity.result.IntentSenderRequest;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.labourlink.app.api.ApiService;
import com.labourlink.app.models.Profession;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.android.material.textfield.TextInputEditText;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.content.SharedPreferences;
import com.google.android.material.imageview.ShapeableImageView;

public class WorkerRegisterActivity extends AppCompatActivity {

    private MaterialButton btnCurrentLocation;
    private MaterialButton btnSelectMap;
    private TextView txtSelectedAddress;

    private FusedLocationProviderClient fusedLocationClient;

    // Hidden values
    private double latitude;
    private double longitude;

    private String city = "";
    private String district = "";
    private String state = "";
    private String fullAddress = "";
    private ChipGroup chipProfession;
    private ShapeableImageView ivProfilePhoto;
    private MaterialButton btnSelectPhoto;

    private Uri selectedImageUri;
    private ApiService apiService;
    private MaterialButton btnRegister;
    private TextInputEditText etExperience;
    private TextInputEditText etLanguages;
    private TextInputEditText etDescription;
    private TextInputEditText etAadhaar;
    private TextInputEditText etName;
    private TextInputEditText etPhone;
    private List<Long> selectedProfessionIds = new ArrayList<>();
    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            checkGps();
                        } else {
                            Toast.makeText(this,
                                    "Location permission denied",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
    private final ActivityResultLauncher<Intent> mapLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK &&
                                result.getData() != null) {

                            Intent data = result.getData();

                            latitude = data.getDoubleExtra("latitude", 0);
                            longitude = data.getDoubleExtra("longitude", 0);

                            city = data.getStringExtra("city");
                            district = data.getStringExtra("district");
                            state = data.getStringExtra("state");

                            String location = data.getStringExtra("location");

                            txtSelectedAddress.setText(location);
                        }
                    });
    private final ActivityResultLauncher<IntentSenderRequest> gpsLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartIntentSenderForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK) {

                            getCurrentLocation();

                        }

                    });
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK &&
                                result.getData() != null) {

                            selectedImageUri = result.getData().getData();

                            ivProfilePhoto.setImageURI(selectedImageUri);

                        }

                    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_worker_register);

        btnCurrentLocation = findViewById(R.id.btnCurrentLocation);
        btnSelectMap = findViewById(R.id.btnSelectMap);
        txtSelectedAddress = findViewById(R.id.txtSelectedAddress);
        chipProfession = findViewById(R.id.chipProfession);
        btnRegister = findViewById(R.id.btnRegister);
        apiService = ApiClient.getClient().create(ApiService.class);
        etExperience = findViewById(R.id.etExperience);
        etLanguages = findViewById(R.id.etLanguages);
        etDescription = findViewById(R.id.etDescription);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAadhaar = findViewById(R.id.etAadhaar);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto);
        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

        btnCurrentLocation.setOnClickListener(v -> checkPermission());

        btnSelectMap.setOnClickListener(v -> {

            Intent intent = new Intent(
                    WorkerRegisterActivity.this,
                    MapPickerActivity.class);

            mapLauncher.launch(intent);

        });
        loadProfessions();
        loadUserInfo();
        btnRegister.setOnClickListener(v -> registerWorker());
        btnSelectPhoto.setOnClickListener(v -> {

            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            );

            imagePickerLauncher.launch(intent);

        });
    }
    private void loadUserInfo() {

        String token = "Bearer " + getSharedPreferences(
                "LabourLink",
                MODE_PRIVATE
        ).getString("token", "");

        apiService.getUserInfo(token)
                .enqueue(new Callback<UserInfo>() {

                    @Override
                    public void onResponse(Call<UserInfo> call,
                                           Response<UserInfo> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            UserInfo user = response.body();

                            etName.setText(user.getName());
                            etPhone.setText(user.getPhoneNumber());

                        } else {

                            Toast.makeText(
                                    WorkerRegisterActivity.this,
                                    "Unable to load user details",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<UserInfo> call,
                                          Throwable t) {

                        Toast.makeText(
                                WorkerRegisterActivity.this,
                                "Failed to connect: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }
                });
    }
    private void loadProfessions() {

        String token = "Bearer " + getSharedPreferences(
                "LabourLink",
                MODE_PRIVATE
        ).getString("token", "");

        apiService.getAllProfessions(token)
                .enqueue(new Callback<List<Profession>>() {

                    @Override
                    public void onResponse(
                            Call<List<Profession>> call,
                            Response<List<Profession>> response) {

                        if (!response.isSuccessful() ||
                                response.body() == null)
                            return;

                        chipProfession.removeAllViews();

                        for (Profession profession : response.body()) {

                            Chip chip =
                                    new Chip(WorkerRegisterActivity.this);

                            chip.setText(profession.getName());

                            chip.setCheckable(true);

                            chip.setTag(profession.getId());

                            chip.setOnCheckedChangeListener(
                                    (button, checked) -> {

                                        Long id =
                                                (Long) button.getTag();

                                        if (checked) {

                                            selectedProfessionIds.add(id);

                                        } else {

                                            selectedProfessionIds.remove(id);

                                        }

                                    });

                            chipProfession.addView(chip);

                        }

                    }

                    @Override
                    public void onFailure(
                            Call<List<Profession>> call,
                            Throwable t) {

                        Toast.makeText(
                                WorkerRegisterActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();

                    }

                });

    }
    private void registerWorker() {

        if (selectedProfessionIds.isEmpty()) {
            Toast.makeText(this,
                    "Please select at least one profession",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String experienceText = etExperience.getText().toString().trim();

        int experience = experienceText.isEmpty()
                ? 0
                : Integer.parseInt(experienceText);

        String languages = etLanguages.getText().toString().trim();

        String description = etDescription.getText().toString().trim();

        String aadhaar = etAadhaar.getText().toString().trim();
        WorkerRegisterRequest request = new WorkerRegisterRequest();

        request.setExperience(experience);

        request.setLatitude(latitude);
        request.setLongitude(longitude);

        request.setCity(city);
        request.setDistrict(district);
        request.setState(state);

        request.setLanguages(languages);
        request.setDescription(description);
        request.setAadhaarNumber(aadhaar);

        request.setProfessionIds(selectedProfessionIds);
        String savedToken = getSharedPreferences(
                "LabourLink",
                MODE_PRIVATE
        ).getString("token", "");

        android.util.Log.d("TOKEN", savedToken);
        String token = "Bearer " + savedToken;

        Gson gson = new Gson();

        RequestBody workerBody = RequestBody.create(
                gson.toJson(request),
                MediaType.parse("application/json")
        );

// Read default image from drawable
        if (selectedImageUri == null) {

            Toast.makeText(this,
                    "Please select a profile photo",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        MultipartBody.Part photo;

        try {

            InputStream inputStream =
                    getContentResolver().openInputStream(selectedImageUri);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            byte[] data = new byte[4096];

            int nRead;

            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {

                buffer.write(data, 0, nRead);

            }

            buffer.flush();

            inputStream.close();

            byte[] bytes = buffer.toByteArray();

            String mimeType = getContentResolver().getType(selectedImageUri);

            if (mimeType == null) {
                mimeType = "image/jpeg";
            }

            RequestBody imageBody = RequestBody.create(
                    bytes,
                    MediaType.parse(mimeType)
            );

            photo = MultipartBody.Part.createFormData(
                    "photo",
                    "profile.jpg",
                    imageBody
            );

        } catch (Exception e) {

            e.printStackTrace();

            Toast.makeText(this,
                    "Unable to read selected image",
                    Toast.LENGTH_SHORT).show();

            return;
        }
        apiService.registerWorker(
                token,
                workerBody,
                photo
        ).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(
                            WorkerRegisterActivity.this,
                            "Registration Successful",
                            Toast.LENGTH_SHORT
                    ).show();

                    Intent intent = new Intent(
                            WorkerRegisterActivity.this,
                            MyWorkerProfileActivity.class
                    );
                    startActivity(intent);
                    finish();

                } else {

                    Toast.makeText(
                            WorkerRegisterActivity.this,
                            "Registration Failed : " + response.code(),
                            Toast.LENGTH_LONG
                    ).show();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call,
                                  Throwable t) {

                Toast.makeText(
                        WorkerRegisterActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void checkGps() {

        LocationRequest locationRequest =
                new LocationRequest.Builder(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        1000
                ).build();

        LocationSettingsRequest request =
                new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest)
                        .setAlwaysShow(true)
                        .build();

        SettingsClient client =
                LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> task =
                client.checkLocationSettings(request);

        task.addOnSuccessListener(response -> {

            getCurrentLocation();

        });

        task.addOnFailureListener(e -> {

            if (e instanceof ResolvableApiException) {

                ResolvableApiException resolvable = (ResolvableApiException) e;

                IntentSenderRequest senderRequest =
                        new IntentSenderRequest.Builder(resolvable.getResolution())
                                .build();

                gpsLauncher.launch(senderRequest);

            }

        });

    }
    private void checkPermission() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            checkGps();

        } else {

            permissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION);

        }
    }

    private void getCurrentLocation() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                null
        ).addOnSuccessListener(location -> {

            if (location != null) {

                latitude = location.getLatitude();
                longitude = location.getLongitude();

                getAddress(latitude, longitude);

            } else {

                Toast.makeText(
                        this,
                        "Unable to get current location",
                        Toast.LENGTH_SHORT
                ).show();

            }

        });

    }

    private void getAddress(double lat, double lng) {

        Geocoder geocoder =
                new Geocoder(this, Locale.getDefault());

        try {

            List<Address> addresses =
                    geocoder.getFromLocation(lat, lng, 1);

            if (addresses != null && !addresses.isEmpty()) {

                Address address = addresses.get(0);

                city = address.getLocality();

                district = address.getSubAdminArea();

                state = address.getAdminArea();

                fullAddress = address.getAddressLine(0);

                txtSelectedAddress.setText(fullAddress);

            }

        } catch (IOException e) {

            e.printStackTrace();

            Toast.makeText(this,
                    "Unable to fetch address.",
                    Toast.LENGTH_SHORT).show();

        }

    }

    // Hidden values for API

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getState() {
        return state;
    }

    public String getFullAddress() {
        return fullAddress;
    }
}