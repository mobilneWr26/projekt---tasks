package com.example.projektaplikacjemobilne;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView tekstWitaj;
    ListView listaZadan;
    Button przyciskDodaj;

    BazaDanychZadania baza;
    ArrayList<String> lista;
    ArrayList<Integer> listaId;

    ArrayAdapter<String> adapter;

    int userId;
    String imie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tekstWitaj = findViewById(R.id.tekstWitaj);
        listaZadan = findViewById(R.id.listaZadan);
        przyciskDodaj = findViewById(R.id.przyciskDodaj);

        baza = new BazaDanychZadania(this);
        userId = getIntent().getIntExtra("user_id", -1);
        imie = getIntent().getStringExtra("imie");
        tekstWitaj.setText("Witaj, " + imie);

        przyciskDodaj.setOnClickListener(
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                        intent.putExtra("user_id", userId);
                        startActivity(intent);
                    }
                });

        listaZadan.setOnItemClickListener(
                new android.widget.AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                        // jak kliknie sie kategorie to nic sie nie dzieje
                        if (listaId.get(position) == -1) return;

                        Intent i = new Intent(MainActivity.this, TaskDetailActivity.class);
                        i.putExtra("task_id", listaId.get(position));
                        startActivity(i);
                    }
                });
        wczytaj();
    }

    // to uruchamia sie po powrocie do tej aktywnosci/ekranu
    @Override
    protected void onResume() {
        super.onResume();
        wczytaj();
    }
    private void wczytaj() {
        lista = new ArrayList<>();
        listaId = new ArrayList<>();

        Cursor cursor = baza.getReadableDatabase().rawQuery("SELECT * FROM zadania WHERE user_id=? ORDER BY kategoria ASC, priorytet DESC", new String[]{String.valueOf(userId)});
        String lastKat = "";

        //moveToNext - przechodzi po wszystkich rekordach
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String nazwa = cursor.getString(2);
            String kategoria = cursor.getString(7);
            int priorytet = cursor.getInt(6);
            int status = cursor.getInt(8);

            if (!kategoria.equals(lastKat)) {
                lista.add(kategoria);
                listaId.add(-1);
                lastKat = kategoria;
            }
            String kropka = "";
            if (priorytet == 0) kropka = "🟢";
            if (priorytet == 1) kropka = "🟡";
            if (priorytet == 2) kropka = "🔴";

            String statusString;
            if (status == 1) {
                statusString = "✔"; // jesli zrobione
            } else {
                statusString = "✖"; // jesli nie zrobione
            }

            lista.add(kropka + "   " + nazwa + " " + statusString);
            listaId.add(id);
        }
        cursor.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);
        listaZadan.setAdapter(adapter);
    }
}