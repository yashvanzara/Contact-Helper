package com.yashvanzara.www.contacthelper;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

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
    public void addContact(Contact c){
        mDatabase.child("users").child(user.getUid()).child("contacts").child(c.getContactNumber()).setValue(c);
    }
    public void deleteContact(String contactNumber){
        mDatabase.child("users").child(user.getUid()).child("contacts").child(contactNumber).removeValue();
    }
    public Contact getSingleContact(String contactNumber){
        Contact c=null;
        dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("contacts").child(contactNumber);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Contact c = dataSnapshot.getValue(Contact.class);
                Log.d("Grr", c.toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return c;
    }
    public String[] getAllContacts(){
        final String[] items = new String[0];
        dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("contacts");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> contacts = new ArrayList<>();
                for(DataSnapshot contactSnapShop:dataSnapshot.getChildren()){
                    String key = contactSnapShop.getKey();
                    Contact c = contactSnapShop.getValue(Contact.class);
                    contacts.add(c.getContactNumber());
                    Log.d("Data", key);
                    Log.d("Contact", c.toString());
                }
                Arrays.copyOf(items, contacts.size());
                contacts.toArray(items);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return items;
    }
}
