package com.arbeitstein.myapplication;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
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
    }

    private void sendMatNrToServer(String matNr) {
        new Thread(() ->  {
            try {
                Socket socket = new Socket("se2-submission.aau.at", 20080);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println(matNr);
                String response = in.readLine();

                runOnUiThread(() -> {
                    tvResponse.setText(response);
                    tvResponse.setMovementMethod(new ScrollingMovementMethod());
                });

                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

}