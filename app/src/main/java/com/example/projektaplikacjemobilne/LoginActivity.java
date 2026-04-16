package com.example.projektaplikacjemobilne;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText poleLogin, poleHaslo;
    Button przyciskLogin, przyciskRejestracja;

    BazaDanychZadania baza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        poleLogin = findViewById(R.id.poleLogin);
        poleHaslo = findViewById(R.id.poleHaslo);
        przyciskLogin = findViewById(R.id.przyciskLogin);
        przyciskRejestracja = findViewById(R.id.przyciskRejestracja);

        baza = new BazaDanychZadania(this);

        przyciskLogin.setOnClickListener(
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        zalogujUzytkownika();
                    }
                });

        przyciskRejestracja.setOnClickListener(
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    }
                });
    }

    private void zalogujUzytkownika() {
        String login = poleLogin.getText().toString();
        String haslo = poleHaslo.getText().toString();

        Cursor cursor = baza.getReadableDatabase().rawQuery("SELECT * FROM uzytkownicy WHERE login=? AND haslo=?", new String[]{login, haslo});

        //moveToFirst - sprawdza czy coś znaleziono
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String imie = cursor.getString(1);
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("user_id", id);
            i.putExtra("imie", imie);

            startActivity(i);
            finish();
        }
        else {
            Toast.makeText(this, "Błędny login lub hasło", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }
}