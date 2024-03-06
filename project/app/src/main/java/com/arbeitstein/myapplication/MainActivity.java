package com.arbeitstein.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editTextNumber;
    private TextView tvResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        Button serverButton = findViewById(R.id.sendButton1);
        Button gcdButton = findViewById(R.id.sendButton2);

        editTextNumber = findViewById(R.id.editTextNumber);
        tvResponse = findViewById(R.id.tvResponse);

        setSupportActionBar(toolbar);

        serverButton.setOnClickListener(view -> sendMatNrToServer(editTextNumber.getText().toString()));
        gcdButton.setOnClickListener(view -> getGcdOfPairs(editTextNumber.getText().toString()));

    }

    private void sendMatNrToServer(String matNr) {
        new Thread(() ->  {
            try {
                Socket socket = new Socket("se2-submission.aau.at", 20080);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println(matNr);
                String response = in.readLine();

                runOnUiThread(() -> tvResponse.setText(response));

                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void getGcdOfPairs(String matNr) {
        if (matNr.length() != 8){
            tvResponse.setText(R.string.errorMessage1);
        } else {
            List<String> pairs = new ArrayList<>();
            for (int i = 0; i < matNr.length(); i++) {
                for (int j = i + 1; j < matNr.length(); j++) {
                    int gcd = findGcdOfPair(Character.getNumericValue(matNr.charAt(i)), Character.getNumericValue(matNr.charAt(j)));
                    if (gcd > 1) {
                        pairs.add("(I1: " + i + ", I2: " + j + ") - GCD: " + gcd);
                    }
                }
            }
            if (!pairs.isEmpty()){
                StringBuilder result = new StringBuilder();
                for (String pair : pairs) {
                    result.append(pair).append("\n");
                }
                tvResponse.setText(result.toString());
            } else {
                tvResponse.setText(R.string.errorMessage2);
            }

        }

    }

    private int findGcdOfPair(int num1, int num2) {
        int min = Math.min(num1, num2);
        for (int i = min; i >= 2; i--) {
            if (num1 % i == 0 && num2 % i == 0) {
                return i;
            }
        }
        return 1;
    }
}