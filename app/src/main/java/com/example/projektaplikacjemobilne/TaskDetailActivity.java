package com.example.projektaplikacjemobilne;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskDetailActivity extends AppCompatActivity {

    TextView tekstNazwa, tekstOpis, tekstData, tekstDeadline, tekstCzas;
    CheckBox checkStatus;
    Button przyciskUsun, btnBack, przyciskEdytuj;

    BazaDanychZadania baza;

    int taskId;
    int status;
    String deadline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        taskId = getIntent().getIntExtra("task_id", -1);

        if (taskId == -1) {
            finish();
            return;
        }

        baza = new BazaDanychZadania(this);


        tekstNazwa = findViewById(R.id.tekstNazwa);
        tekstOpis = findViewById(R.id.tekstOpis);
        tekstData = findViewById(R.id.tekstData);
        tekstDeadline = findViewById(R.id.tekstDeadline);
        tekstCzas = findViewById(R.id.tekstCzas);
        checkStatus = findViewById(R.id.checkStatus);
        przyciskUsun = findViewById(R.id.przyciskUsun);
        btnBack = findViewById(R.id.btnBack);
        przyciskEdytuj = findViewById(R.id.przyciskEdytuj);

        checkStatus.setOnCheckedChangeListener(
                new android.widget.CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {

                        if (isChecked) {
                            status = 1;
                        } else {
                            status = 0;
                        }
                        SQLiteDatabase db = baza.getWritableDatabase();
                        db.execSQL("UPDATE zadania SET status=? WHERE id=?", new Object[]{status, taskId}
                        );
                        if (isChecked) {
                            checkStatus.setText("Zrobione");
                        } else {
                            checkStatus.setText("Do zrobienia");
                        }
                    }
                });
        przyciskUsun.setOnClickListener(
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        SQLiteDatabase db = baza.getWritableDatabase();
                        int wynik = db.delete("zadania", "id=?", new String[]{String.valueOf(taskId)});

                        if (wynik > 0) {
                            Toast.makeText(TaskDetailActivity.this, "Usunięto zadanie", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(TaskDetailActivity.this, "Błąd usuwania", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        btnBack.setOnClickListener(
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        finish();
                    }
                });

        przyciskEdytuj.setOnClickListener(
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        pokazDialogEdycji();
                    }
                });
        wczytajZadanie();
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    private void wczytajZadanie() {

        SQLiteDatabase db = baza.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM zadania WHERE id=?", new String[]{String.valueOf(taskId)});
        if (c.moveToFirst()) {
            tekstNazwa.setText(c.getString(2));
            tekstOpis.setText(c.getString(3));
            tekstData.setText("Dodano: " + c.getString(4));
            deadline = c.getString(5);

            if (deadline != null && !deadline.isEmpty()) {
                tekstDeadline.setText("Deadline: " + deadline);
                tekstCzas.setText(obliczCzas(deadline));
            } else {
                tekstDeadline.setText("Brak deadline");
                tekstCzas.setText("");
            }
            status = c.getInt(8);
            checkStatus.setChecked(status == 1);

            if (status == 1) {
                checkStatus.setText("Zrobione");
            } else {
                checkStatus.setText("Do zrobienia");
            }
        }
        c.close();
    }
    private String obliczCzas(String deadlineStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date deadlineDate = sdf.parse(deadlineStr);

            long czasDoKonca = deadlineDate.getTime() - System.currentTimeMillis();

            if (czasDoKonca <= 0) {
                return "⛔ Termin minął";
            }

            long godz = czasDoKonca / (1000 * 60 * 60);
            long min = (czasDoKonca / (1000 * 60)) % 60;

            return "Pozostało: " + godz + "h " + min + "min";

        } catch (Exception e) {
            return "";
        }
    }

    private void pokazDialogEdycji() {
        //builder jest potrzebny zeby stworzyc okno dialogowe (alert dialog)
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);

        final android.widget.EditText inputNazwa = new android.widget.EditText(this);
        final android.widget.EditText inputOpis = new android.widget.EditText(this);

        inputNazwa.setHint("Nowa nazwa");
        inputOpis.setHint("Nowy opis");

        final android.widget.Spinner spinnerPriorytet = new android.widget.Spinner(this);

        String[] priorytety = {"Niski 🟢", "Średni 🟡", "Wysoki 🔴"};

        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, priorytety);
        spinnerPriorytet.setAdapter(adapter);

        layout.addView(inputNazwa);
        layout.addView(inputOpis);
        layout.addView(spinnerPriorytet);

        builder.setTitle("Edytuj zadanie");
        builder.setView(layout);

        builder.setPositiveButton("Zapisz", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                String nowaNazwa = inputNazwa.getText().toString();
                String nowyOpis = inputOpis.getText().toString();
                int nowyPriorytet = spinnerPriorytet.getSelectedItemPosition();
                SQLiteDatabase db = baza.getWritableDatabase();

                if(!nowaNazwa.equals("") && !nowyOpis.equals("")){
                    db.execSQL("UPDATE zadania SET nazwa=?, opis=?, priorytet=? WHERE id=?", new Object[]{nowaNazwa, nowyOpis, nowyPriorytet, taskId});
                } else if (nowaNazwa.equals("") && !nowyOpis.equals("")) {
                    db.execSQL("UPDATE zadania SET opis=?, priorytet=? WHERE id=?", new Object[]{nowyOpis, nowyPriorytet, taskId});
                } else if (nowyOpis.equals("") && !nowaNazwa.equals("")) {
                    db.execSQL("UPDATE zadania SET nazwa=?, priorytet=? WHERE id=?", new Object[]{nowaNazwa,  nowyPriorytet, taskId});
                } else if (nowyOpis.equals("") && nowaNazwa.equals("")) {
                    db.execSQL("UPDATE zadania SET priorytet=? WHERE id=?", new Object[]{nowyPriorytet, taskId});
                }


                wczytajZadanie();

                Toast.makeText(TaskDetailActivity.this, "Zaktualizowano zadanie", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Anuluj", null);

        builder.show();
    }
}