package com.example.projektaplikacjemobilne;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    EditText poleImie, poleLogin, poleHaslo;
    Button przyciskZarejestruj;

    BazaDanychZadania baza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        poleImie = findViewById(R.id.poleImie);
        poleLogin = findViewById(R.id.poleLogin);
        poleHaslo = findViewById(R.id.poleHaslo);
        przyciskZarejestruj = findViewById(R.id.przyciskZarejestruj);

        baza = new BazaDanychZadania(this);

        przyciskZarejestruj.setOnClickListener(
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        dodajUzytkownika();
                    }
                });
    }


    private void dodajUzytkownika() {
        String imie = poleImie.getText().toString();
        String login = poleLogin.getText().toString();
        String haslo = poleHaslo.getText().toString();

        SQLiteDatabase db = baza.getWritableDatabase();
        db.execSQL("INSERT INTO uzytkownicy(imie, login, haslo) VALUES(?,?,?)", new Object[]{imie, login, haslo});
        Toast.makeText(this, "Zarejestrowano!", Toast.LENGTH_SHORT).show();

        finish();
    }
}