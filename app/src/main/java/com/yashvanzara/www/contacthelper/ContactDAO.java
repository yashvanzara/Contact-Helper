package com.yashvanzara.www.contacthelper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by User on 19-03-2018.
 */

public class ContactDAO {
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    ArrayList<Contact> contacts;
    DatabaseReference dbRef;
    public ContactDAO(){
        contacts = new ArrayList<Contact>();
        mAuth = FirebaseAuth.getInstance();
        this.user = mAuth.getCurrentUser();
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void addOrUpdateContact(Contact c){
        mDatabase.child("users").child(user.getUid()).child("contacts").child(c.getContactNumber()).setValue(c);
    }
    public void deleteContact(String contactNumber){
        mDatabase.child("users").child(user.getUid()).child("contacts").child(contactNumber).removeValue();
    }


}
