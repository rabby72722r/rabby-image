package com.example.rabby.imageupload;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class ImagesActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;

    private ProgressBar mProgressCircle;
    private FirebaseStorage mStorage;

    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;


    private List<Upload> mUploads;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        android.support.v7.app.ActionBar ab=getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);




        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressCircle = findViewById(R.id.progress_circle);

        mUploads = new ArrayList<>();

        mAdapter = new ImageAdapter(ImagesActivity.this, mUploads);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(ImagesActivity.this);

        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUploads.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    upload.setKey(postSnapshot.getKey());
                    mUploads.add(upload);
                }
                mAdapter.notifyDataSetChanged();

                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ImagesActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });


    }

    @Override
    public void onItemClick(int position) {

        Toast.makeText(this, "Normal click at position: " + position, Toast.LENGTH_SHORT).show();

    }



    @Override
    public void onWhatEverClick(int position) {
     Toast.makeText(this, "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onDeleteClick(int position) {
        Upload selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(ImagesActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });


        // Toast.makeText(this, "Delete click at position: " + position, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.homeBtnId) {

            // -- home button click from actionbar
            Intent home= new Intent(ImagesActivity.this,MainActivity.class);
            startActivity(home);

            Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.aboutUsId) {
            // --- about us button click from actionbar
            Intent about= new Intent(this,HelpActivity.class);
            startActivity(about);
            return true;
        }

        if (id == R.id.SettingsId) {
            // ---- log out button click from actionbar
            Intent settings= new Intent(this,SettingsActivity.class);
            startActivity(settings);

            return true;
        }

        if (id == R.id.FeedbackId) {
            // ---- log out button click from actionbar
            Intent feedback= new Intent(this,FeedBack.class);
            startActivity(feedback);

            return true;
        }

        if (id == R.id.exitId) {

            // ---- exit button click from actionbar
            showDailog();
            // finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Intent home= new Intent(ImagesActivity.this,MainActivity.class);
        startActivity(home);

        //Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
        //return true;
    }
    private void showDailog(){
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.exit);
        builder.setTitle("Exit ?");
        builder.setMessage("Are you sure to Exit ?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
    }
}


