package com.gurpreet.pnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ActivityNoteDetail extends AppCompatActivity {
    private static final String TAG = ActivityNoteDetail.class.getSimpleName();
    EditText etNoteTitle, etNoteContent;
    TextView tvNoteDate;
    Intent data;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        data = getIntent();

        etNoteTitle = findViewById(R.id.et_note_title);
        etNoteContent = findViewById(R.id.et_note_content);
        tvNoteDate = findViewById(R.id.tv_note_date);

        etNoteTitle.setText(data.getStringExtra("title"));
        etNoteContent.setText(data.getStringExtra("content"));
        tvNoteDate.setText(data.getStringExtra("date"));
        Log.d(TAG, data.getStringExtra("docId"));

        findViewById(R.id.fab_edit_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNoteTitle.setFocusableInTouchMode(true);
                etNoteContent.setFocusableInTouchMode(true);
                findViewById(R.id.fab_edit_note).setVisibility(View.INVISIBLE);
                Toast.makeText(ActivityNoteDetail.this, "EDITING NOW", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.fab_save_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityNoteDetail.this, "UPDATING NOW", Toast.LENGTH_SHORT).show();

                String noteTitle = etNoteTitle.getText().toString().trim();
                String noteContent = etNoteContent.getText().toString().trim();

                if(noteTitle.isEmpty() || noteContent.isEmpty()) {
                    Toast.makeText(ActivityNoteDetail.this, "Empty Fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                String user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                Log.d(TAG, user);
                firestore = FirebaseFirestore.getInstance();
                DocumentReference documentReference = firestore.collection(user).document(data.getStringExtra("docId"));
                Map<String, Object> note = new HashMap<>();
                note.put("title", noteTitle);
                note.put("content", noteContent);

                documentReference.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ActivityNoteDetail.this, "Note Added",
                                Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(ActivityNoteDetail.this, "Note add failed   ",
                                Toast.LENGTH_SHORT).show();
                    }
                });


                findViewById(R.id.fab_edit_note).setVisibility(View.VISIBLE);

            }
        });
    }
}