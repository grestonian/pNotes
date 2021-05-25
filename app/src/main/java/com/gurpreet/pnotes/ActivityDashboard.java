package com.gurpreet.pnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirestoreRegistrar;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

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
                holder.tvNoteDate.setText(note.getDate().substring(0,11));
                holder.cvNoteCard.setBackgroundColor(holder.view.getResources().getColor(getRandomColor(), null));
                Log.d("TAG", note.getTitle());

                holder.ivNoteShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, note.getTitle());
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, note.getContent());
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    }
                });
                holder.ivNoteDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String docId = noteAdapter.getSnapshots().getSnapshot(position).getId();
                        firestore = FirebaseFirestore.getInstance();
                        firestore.collection(user).document(docId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ActivityDashboard.this, "Note Deleted!", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(ActivityDashboard.this, "Note Deletion Failed!", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String docId = noteAdapter.getSnapshots().getSnapshot(position).getId();
                        Log.d("DOCID", docId);
                        Toast.makeText(v.getContext(), "NOTE CLICKED", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), ActivityNoteDetail.class);
                        i.putExtra("title", note.getTitle());
                        i.putExtra("content", note.getContent());
                        i.putExtra("date", note.getDate());
                        i.putExtra("docId", docId);
                        startActivity(i);
                    }
                });

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
                break;
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
        TextView tvNoteDate;
        ImageView ivNoteDelete;
        ImageView ivNoteShare;
        LinearLayout cvNoteCard;
        View view;
        public NoteViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvNoteTitle = itemView.findViewById(R.id.tv_note_title);
            tvNoteContent = itemView.findViewById(R.id.tv_note_content);
            tvNoteDate = itemView.findViewById(R.id.tv_note_date);
            ivNoteDelete = itemView.findViewById(R.id.iv_note_delete);
            ivNoteShare= itemView.findViewById(R.id.iv_note_share);
            cvNoteCard = itemView.findViewById(R.id.cv_note_card);
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

    private int getRandomColor() {
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.pink);
        colorCode.add(R.color.teal);
        colorCode.add(R.color.blue);
        colorCode.add(R.color.grey);
        colorCode.add(R.color.green);
        colorCode.add(R.color.darkblue);
        colorCode.add(R.color.yellow);

        Random randomColor = new Random();
        int number = randomColor.nextInt(colorCode.size());
        return colorCode.get(number);
    }
}