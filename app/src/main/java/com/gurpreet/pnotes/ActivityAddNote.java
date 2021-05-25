package com.gurpreet.pnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class ActivityAddNote extends AppCompatActivity {
    FirebaseFirestore firestore;
    EditText etNoteTitle, etNoteContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        firestore = FirebaseFirestore.getInstance();

        etNoteTitle = findViewById(R.id.et_note_title);
        etNoteContent = findViewById(R.id.et_note_content);

        findViewById(R.id.fab_save_note).setOnClickListener(v -> {
            String noteTitle = etNoteTitle.getText().toString();
            String noteContent = etNoteContent.getText().toString();

            if(noteTitle.isEmpty() || noteContent.isEmpty()) {
                Toast.makeText(this, "Empty Fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            DocumentReference documentReference = firestore.collection(user).document();
            Map<String, Object> note = new HashMap<>();
            note.put("title", noteTitle);
            note.put("content", noteContent);

            documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(ActivityAddNote.this, "Note Added", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(ActivityAddNote.this, "Note add failed   ", Toast.LENGTH_SHORT).show();
                }
            });
        });


//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

}