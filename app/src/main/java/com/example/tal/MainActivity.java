package com.example.tal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.io.Console;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Duration;

public class MainActivity extends AppCompatActivity {

    public static final String API_KEY = "{email:<jeffcheng1234@gmail.com>}";
    public static final String BASE_LINK="https://api-auth.prod.birdapp.com/api/v1/auth/email";
    public static final String LOG_TAG="NewsApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


}

