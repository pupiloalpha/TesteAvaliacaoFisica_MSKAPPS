package com.msk.taf.info;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.msk.taf.TesteAvaliacaoFisica;

/**
 * Created by msk on 30/05/16.
 */
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, TesteAvaliacaoFisica.class);
        startActivity(intent);
        finish();
    }
}
