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
import java.util.Objects;

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

        serverButton.setOnClickListener(view -> getAnswerFromServer(editTextNumber.getText().toString()));
        gcdButton.setOnClickListener(view -> getGcdOfPairs(editTextNumber.getText().toString()));

    }

    private void getAnswerFromServer(String matNr) {
        new Thread(() ->  {
            String response = createSocketConnection(matNr);
            runOnUiThread(() -> tvResponse.setText(response));
        }).start();
    }

    private String createSocketConnection(String matNr){
        String response;
        try {
            Socket socket = new Socket("se2-submission.aau.at", 20080);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(matNr);
            response = in.readLine();

            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    private void getGcdOfPairs(String matNr) {
        new Thread(() ->  {
            String response = createSocketConnection(matNr);
            System.out.println(response);
            if (Objects.equals(response, "Dies ist keine gueltige Matrikelnummer")){
                runOnUiThread(() -> tvResponse.setText(R.string.serverError1));
            } else {
                List<String> resultPairs = getGcdList(matNr);
                printGcdPairs(resultPairs);
            }
        }).start();
    }

    private List<String> getGcdList(String matNr) {
        List<String> pairs = new ArrayList<>();
        for (int i = 0; i < matNr.length(); i++) {
            for (int j = i + 1; j < matNr.length(); j++) {
                int gcd = findGcdOfPair(Character.getNumericValue(matNr.charAt(i)), Character.getNumericValue(matNr.charAt(j)));
                if (gcd > 1) {
                    pairs.add("(I1: " + i + ", I2: " + j + ") - GCD: " + gcd);
                }
            }
        }
        return pairs;
    }

    private void printGcdPairs(List<String> pairs) {
        if (!pairs.isEmpty()){
            StringBuilder result = new StringBuilder();
            for (String pair : pairs) {
                result.append(pair).append("\n");
            }
            runOnUiThread(() -> tvResponse.setText(result.toString()));
        } else {
            runOnUiThread(() -> tvResponse.setText(R.string.errorMessage1));
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