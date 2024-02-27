package com.example.usersignup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class registerform extends AppCompatActivity {
    private EditText inputUsername;
    private EditText inputPassword;
    private EditText inputRetype;
    private EditText inputBirthdate;
    private RadioGroup radioGender;
    private CheckBox checkTennis;
    private CheckBox checkFutbal;
    private CheckBox checkOthers;
    private Button btnDateSelect;
    private Button btnSignup;
    private Button btnReset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views
        inputUsername = findViewById(R.id.inputUsername);
        inputPassword = findViewById(R.id.inputPassword);
        inputRetype = findViewById(R.id.inputRetype);
        inputBirthdate = findViewById(R.id.inputBirthdate);
        radioGender = findViewById(R.id.radioGender);
        checkTennis = findViewById(R.id.checkTennis);
        checkFutbal = findViewById(R.id.checkFutbal);
        checkOthers = findViewById(R.id.checkOthers);

        btnDateSelect = findViewById(R.id.btnDateSelect);
        btnReset = findViewById(R.id.btnReset);
        btnSignup = findViewById(R.id.btnSignup);

        // Select date
        btnDateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                    registerform.this,
                    (datePicker, year, month, day) -> {
                        // Handle the selected date here
                        String formattedDate = String.format("%02d/%02d/%04d", day, month + 1, year);
                        inputBirthdate.setText(formattedDate);
                    },
                    // Set initial date to current date
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
            }
        });

        // Reset button
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputUsername.setText("");
                inputPassword.setText("");
                inputRetype.setText("");
                inputBirthdate.setText("");
                radioGender.clearCheck();
                RadioButton radio = findViewById(R.id.radioMale);
                radio.setChecked(true);
                checkTennis.setChecked(false);
                checkFutbal.setChecked(false);
                checkOthers.setChecked(false);
            }
        });

        // Signup button
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = String.valueOf(inputUsername.getText());

                // Check username
                if (username.equals("")) {
                    Toast.makeText(getBaseContext(), "Please enter username!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String password = String.valueOf(inputPassword.getText());
                String retype = String.valueOf(inputRetype.getText());

                // Check password
                if (password.equals("")) {
                    Toast.makeText(getBaseContext(), "Please enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(retype)) {
                    Toast.makeText(getBaseContext(), "Password does not match retype!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String birthdate = String.valueOf(inputBirthdate.getText());

                // Check date
                if (!checkDate(birthdate)) {
                    Toast.makeText(getBaseContext(), "Incorrect date format!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get gender
                int radioID = radioGender.getCheckedRadioButtonId();
                RadioButton radio = findViewById(radioID);
                String gender = (String) radio.getText();

                // Get hobbies
                ArrayList<String> hobbiesArr = new ArrayList<>();
                if (checkTennis.isChecked()) hobbiesArr.add((String) checkTennis.getText());
                if (checkFutbal.isChecked()) hobbiesArr.add((String) checkFutbal.getText());
                if (checkOthers.isChecked()) hobbiesArr.add((String) checkOthers.getText());
                String hobbies;
                if (hobbiesArr.isEmpty()) hobbies = "None";
                else hobbies = String.join(", ", hobbiesArr);

                // Intent
                Intent intent = new Intent(getBaseContext(), resultform.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("PASSWORD", password);
                intent.putExtra("DATE_OF_BIRTH", birthdate);
                intent.putExtra("GENDER", gender);
                intent.putExtra("HOBBIES", hobbies);
                startActivity(intent);
            }
        });
    }
    private boolean checkDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        try {
            Date date = dateFormat.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}