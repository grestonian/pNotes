package com.gurpreet.pnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirestoreRegistrar;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActivityDashboard extends AppCompatActivity {
    boolean checked = false;
    RecyclerView noteList;
//    NoteListAdapter noteListAdapter;

    FirebaseFirestore firestore;
    FirestoreRecyclerAdapter<Note, NoteViewHolder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        String user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        firestore = FirebaseFirestore.getInstance();
        Query query = firestore.collection(user);


        FirestoreRecyclerOptions<Note> notes = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        noteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(notes) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull ActivityDashboard.NoteViewHolder holder,
                                            int position, @NonNull @NotNull Note note) {
                holder.tvNoteTitle.setText(note.getTitle());
                holder.tvNoteContent.setText(note.getContent());
                Log.d("TAG", note.getTitle());
                holder.view.setOnClickListener(v ->
                        Toast.makeText(v.getContext(), "NOTE CLICKED", Toast.LENGTH_SHORT).show());
            }

            @NonNull
            @NotNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_note, parent, false);
                return new NoteViewHolder(view);
            }
        };

        noteList = findViewById(R.id.rv_note_list);

//        List<String> titles = new ArrayList<>();
//        List<String> content = new ArrayList<>();
//
//        titles.add("FIRST");
//        content.add("CONTENT");
//
//        noteListAdapter = new NoteListAdapter(titles, content);
        noteList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        noteList.setAdapter(noteAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;
            case R.id.action_sync:
                Toast.makeText(this, "Syncing notes", Toast.LENGTH_SHORT).show();
            case R.id.action_layout:

                if(checked == false) {
                    item.setIcon(R.drawable.ic_linear_view);
                    noteList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    checked = true;
                }
                else {
                    item.setIcon(R.drawable.ic_grid);
                    noteList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    checked = false;
                }
                noteList.setAdapter(noteAdapter);
                break;
            default:

        }

        return super.onOptionsItemSelected(item);
    }

    public void addNote(View view) {
        startActivity(new Intent(this, ActivityAddNote.class));
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvNoteTitle;
        TextView tvNoteContent;
        View view;
        public NoteViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvNoteTitle = itemView.findViewById(R.id.tv_note_title);
            tvNoteContent = itemView.findViewById(R.id.tv_note_content);
            view = itemView;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }
}