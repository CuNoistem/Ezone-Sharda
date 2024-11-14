package com.rohan.ezone_sharda;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Change_ID extends AppCompatActivity {

    SharedPreferences ezone_data;
    EditText systemIdText;
    Button saveBtn;
    Button cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_id);

        ezone_data = getSharedPreferences("EZONE_DATA", MODE_PRIVATE);
        systemIdText = findViewById(R.id.systemIdText);
        saveBtn = findViewById(R.id.saveBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        // Saving
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (systemIdText.getText().toString().isEmpty()) {
                    Log.d("Change_ID", "No System ID");
                    Toast.makeText(Change_ID.this, "Enter System ID", Toast.LENGTH_SHORT).show();
                } else {
                    if  (systemIdText.getText().toString().length() == 10) {
                        SharedPreferences.Editor editor = ezone_data.edit();
                        editor.putString("SYSTEM_ID", systemIdText.getText().toString());
                        editor.apply();
                        Toast.makeText(Change_ID.this, "System ID Saved", Toast.LENGTH_SHORT).show();
                        Log.d("Change_ID", "Saved");
                        Intent restartActivity = new Intent(Change_ID.this, HomeScreen.class);
                        finish();
                        startActivity(restartActivity);
                    } else {
                        Toast.makeText(Change_ID.this, "Wrong System ID", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        // Cancel
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ezone_data.getString("SYSTEM_ID", "").isEmpty()) {
                    Toast.makeText(Change_ID.this, "Enter System ID", Toast.LENGTH_SHORT).show();
                    Log.d("Change_ID", "No System ID");
                } else {
                    Log.d("Change_ID", "Cancelled");
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (ezone_data.getString("SYSTEM_ID", "").isEmpty()) {
                        Toast.makeText(Change_ID.this, "Enter System ID", Toast.LENGTH_SHORT).show();
                        Log.d("Change_ID", "No System ID");
                        return false;
                    } else {
                        Log.d("Change_ID", "Cancelled");
                        return true;
                    }
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}