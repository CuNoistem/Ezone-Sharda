package com.rohan.ezone_sharda;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDate;

public class Set_OTP extends AppCompatActivity {

    SharedPreferences ezone_data;
    LocalDate presentDate;
    EditText otp;
    Button saveBtn;
    Button cancelBtn;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_set_otp);

        ezone_data = getSharedPreferences("EZONE_DATA", MODE_PRIVATE);
        presentDate = LocalDate.now();
        otp = findViewById(R.id.otpEditText);
        saveBtn = findViewById(R.id.saveBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otp.getText().toString().isEmpty()) {
                    Toast.makeText(Set_OTP.this, "Empty OTP", Toast.LENGTH_SHORT).show();
                } else {
                    if (otp.getText().toString().length() == 6) {
                        SharedPreferences.Editor editor = ezone_data.edit();
                        editor.putString("OTP", otp.getText().toString());
                        editor.putString("CURRENT_DATE", presentDate.toString());
                        editor.apply();
                        Toast.makeText(Set_OTP.this, "OTP Saved", Toast.LENGTH_SHORT).show();
                        Intent restartActivity = new Intent(Set_OTP.this, HomeScreen.class);
                        finish();
                        startActivity(restartActivity);
                    } else {
                        Toast.makeText(Set_OTP.this, "Wrong OTP", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}