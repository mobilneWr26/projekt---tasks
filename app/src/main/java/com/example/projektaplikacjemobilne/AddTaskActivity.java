package com.example.projektaplikacjemobilne;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {

    EditText poleNazwa, poleOpis;
    Spinner spinnerKategoria, spinnerPriorytet;
    Button przyciskData, przyciskDodaj;

    String deadline = null;

    BazaDanychZadania baza;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        poleNazwa = findViewById(R.id.poleNazwa);
        poleOpis = findViewById(R.id.poleOpis);
        spinnerKategoria = findViewById(R.id.spinnerKategoria);
        spinnerPriorytet = findViewById(R.id.spinnerPriorytet);
        przyciskData = findViewById(R.id.przyciskData);
        przyciskDodaj = findViewById(R.id.przyciskDodaj);

        baza = new BazaDanychZadania(this);

        // id uzytkownika pobrane z poprzedniej Activity
        userId = getIntent().getIntExtra("user_id", -1);

        String[] kategorie = {"Szkoła", "Dom", "Praca", "Inne"};
        ArrayAdapter<String> adapterKategoria = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, kategorie);
        spinnerKategoria.setAdapter(adapterKategoria);

        String[] priorytety = {"Niski", "Średni", "Wysoki"};
        ArrayAdapter<String> adapterPriorytet = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, priorytety);
        spinnerPriorytet.setAdapter(adapterPriorytet);

        przyciskData.setOnClickListener(
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        wybierzDate();
                    }
                });


        przyciskDodaj.setOnClickListener(
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        dodajZadanie();
                    }
                });
    }
    private void wybierzDate() {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, final int year, final int month, final int day) {
                        TimePickerDialog timePicker = new TimePickerDialog(AddTaskActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(android.widget.TimePicker view, int hour, int minute) {
                                        deadline = year + "-" + (month + 1) + "-" + day + " " + hour + ":" + minute;

                                        przyciskData.setText("Deadline: " + deadline);
                                    }
                                },
                                c.get(Calendar.HOUR_OF_DAY),
                                c.get(Calendar.MINUTE),
                                true
                        );

                        timePicker.show();
                    }
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );

        datePicker.show();
    }

    private void dodajZadanie() {

        String nazwa = poleNazwa.getText().toString();
        String opis = poleOpis.getText().toString();
        String kategoria = spinnerKategoria.getSelectedItem().toString();

        // 0 - niski 1 - średni 2 - wysoki
        int priorytet = spinnerPriorytet.getSelectedItemPosition();

        String dataDodania = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());

        baza.getWritableDatabase().execSQL(
                "INSERT INTO zadania(user_id,nazwa,opis,data_dodania,deadline,priorytet,kategoria,status) VALUES(?,?,?,?,?,?,?,0)",
                new Object[]{userId, nazwa, opis, dataDodania, deadline, priorytet, kategoria}
        );

        finish();
        // finish zamyka aktywnosci i wraca do poprzedniej
    }
}