package com.labourlink.app.activities;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.labourlink.app.R;
import com.labourlink.app.api.ApiClient;
import com.labourlink.app.api.ApiService;
import com.labourlink.app.models.SendRequest;
import com.labourlink.app.models.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;import okhttp3.ResponseBody;

public class BookingActivity extends AppCompatActivity {

    private ImageButton btnBack;

    private TextView txtWorkerName;
    private TextView txtProfession;
    private TextView txtWorkDate;
    private TextView txtLocation;

    private AutoCompleteTextView spinnerService;

    private TextInputEditText edtTitle;

    private Button btnStartTime;
    private Button btnEndTime;

    private MaterialButton btnSendRequest;

    private ApiService apiService;
    private String token;

    private Service selectedService;

    private final List<Service> serviceList = new ArrayList<>();

    private Long workerId;
    private Long professionId;

    private double latitude;
    private double longitude;

    private String startTime = "";
    private String endTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_booking);

        initViews();

        SharedPreferences preferences =
                getSharedPreferences("LabourLink", MODE_PRIVATE);

        token = preferences.getString("token", "");

        apiService =
                ApiClient.getClient().create(ApiService.class);

        loadWorkerData();

        loadServices();

        clickListeners();

    }

    private void initViews() {

        btnBack = findViewById(R.id.btnBack);

        txtWorkerName = findViewById(R.id.txtWorkerName);
        txtProfession = findViewById(R.id.txtProfession);
        txtWorkDate = findViewById(R.id.txtWorkDate);
        txtLocation = findViewById(R.id.txtLocation);

        spinnerService = findViewById(R.id.spinnerService);

        edtTitle = findViewById(R.id.edtTitle);

        btnStartTime = findViewById(R.id.btnStartTime);
        btnEndTime = findViewById(R.id.btnEndTime);

        btnSendRequest = findViewById(R.id.btnSendRequest);

    }

    private void loadWorkerData() {

        Intent intent = getIntent();

        workerId =
                intent.getLongExtra("workerId", 0);

        professionId =
                intent.getLongExtra("professionId", 0);

        latitude =
                intent.getDoubleExtra("latitude", 0);

        longitude =
                intent.getDoubleExtra("longitude", 0);

        txtWorkerName.setText(
                intent.getStringExtra("workerName")
        );

        txtProfession.setText(
                intent.getStringExtra("profession")
        );

        txtLocation.setText(
                intent.getStringExtra("location")
        );

        String workDate =
                intent.getStringExtra("workDate");

        if (workDate != null && !workDate.isEmpty()) {

            txtWorkDate.setText(workDate);

        } else {

            txtWorkDate.setText("Select Date");

        }

    }private void loadServices() {

        String profession =
                getIntent().getStringExtra("profession");

        apiService.getProfessionServices(
                profession,
                "Bearer " + token
        ).enqueue(new Callback<List<Service>>() {

            @Override
            public void onResponse(Call<List<Service>> call,
                                   Response<List<Service>> response) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    serviceList.clear();

                    serviceList.addAll(response.body());

                    List<String> serviceNames =
                            new ArrayList<>();

                    for (Service service : serviceList) {

                        serviceNames.add(service.getName());

                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(
                                    BookingActivity.this,
                                    android.R.layout.simple_dropdown_item_1line,
                                    serviceNames
                            );

                    spinnerService.setAdapter(adapter);

                } else {

                    Toast.makeText(
                            BookingActivity.this,
                            "No services found.",
                            Toast.LENGTH_SHORT
                    ).show();

                }

            }

            @Override
            public void onFailure(Call<List<Service>> call,
                                  Throwable t) {

                Toast.makeText(
                        BookingActivity.this,
                        "Unable to load services.",
                        Toast.LENGTH_SHORT
                ).show();

            }

        });

    }

    private void clickListeners() {

        btnBack.setOnClickListener(v -> finish());

        txtProfession.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            BookingActivity.this,
                            ProfessionServicesActivity.class
                    );

            intent.putExtra(
                    "profession",
                    txtProfession.getText().toString()
            );

            startActivity(intent);

        });

        spinnerService.setOnItemClickListener((parent, view, position, id) -> {

            selectedService = serviceList.get(position);

            edtTitle.setText(
                    selectedService.getName()
            );

        });

        btnStartTime.setOnClickListener(v ->
                openTimePicker(btnStartTime, true));

        btnEndTime.setOnClickListener(v ->
                openTimePicker(btnEndTime, false));

        btnSendRequest.setOnClickListener(v ->
                sendWorkRequest());

    }

    private void openTimePicker(Button button,
                                boolean isStart) {

        Calendar calendar = Calendar.getInstance();

        int hour =
                calendar.get(Calendar.HOUR_OF_DAY);

        int minute =
                calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog =
                new TimePickerDialog(
                        this,
                        (view, selectedHour, selectedMinute) -> {

                            String time =
                                    String.format(
                                            Locale.getDefault(),
                                            "%02d:%02d",
                                            selectedHour,
                                            selectedMinute
                                    );

                            button.setText(time);

                            if (isStart) {

                                startTime = time;

                            } else {

                                endTime = time;

                            }

                        },
                        hour,
                        minute,
                        false
                );

        dialog.show();

    }
    private void sendWorkRequest() {

        if (selectedService == null) {

            Toast.makeText(
                    this,
                    "Please select a work type.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (edtTitle.getText().toString().trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter work title.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (startTime.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please select start time.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (endTime.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please select end time.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        SendRequest request = new SendRequest();

        request.setWorkerId(workerId);
        request.setProfessionId(professionId);
        request.setTitle(edtTitle.getText().toString().trim());
        request.setDescription("");
        request.setAddress(txtLocation.getText().toString());
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        request.setWorkDate(txtWorkDate.getText().toString());
        request.setStartTime(startTime);
        request.setEndTime(endTime);

        // ===== Log request values =====
        android.util.Log.d("SEND_REQUEST", "WorkerId: " + workerId);
        android.util.Log.d("SEND_REQUEST", "ProfessionId: " + professionId);
        android.util.Log.d("SEND_REQUEST", "Title: " + request.getTitle());
        android.util.Log.d("SEND_REQUEST", "Address: " + request.getAddress());
        android.util.Log.d("SEND_REQUEST", "Latitude: " + request.getLatitude());
        android.util.Log.d("SEND_REQUEST", "Longitude: " + request.getLongitude());
        android.util.Log.d("SEND_REQUEST", "WorkDate: " + request.getWorkDate());
        android.util.Log.d("SEND_REQUEST", "StartTime: " + request.getStartTime());
        android.util.Log.d("SEND_REQUEST", "EndTime: " + request.getEndTime());

        apiService.sendRequest(
                "Bearer " + token,
                request
        ).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {

                android.util.Log.d("SEND_REQUEST",
                        "Response Code: " + response.code());

                if (response.isSuccessful()) {

                    try {

                        String message = response.body() != null
                                ? response.body().string()
                                : "Success";

                        android.util.Log.d("SEND_REQUEST", message);

                        Toast.makeText(
                                BookingActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();

                        finish();

                    } catch (Exception e) {

                        e.printStackTrace();
                    }

                } else {

                    try {

                        String error = response.errorBody() != null
                                ? response.errorBody().string()
                                : "Unknown Error";

                        android.util.Log.e("SEND_REQUEST",
                                "Error: " + error);

                        Toast.makeText(
                                BookingActivity.this,
                                "Error " + response.code() + "\n" + error,
                                Toast.LENGTH_LONG
                        ).show();

                    } catch (Exception e) {

                        e.printStackTrace();

                        Toast.makeText(
                                BookingActivity.this,
                                "HTTP Error: " + response.code(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call,
                                  Throwable t) {

                android.util.Log.e("SEND_REQUEST",
                        "Failure", t);

                Toast.makeText(
                        BookingActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }

        });



    }}