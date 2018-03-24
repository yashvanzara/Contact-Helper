package com.yashvanzara.www.contacthelper;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.flipboard.bottomsheet.commons.BottomSheetFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yashvanzara.www.contacthelper.Utils.Utility;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactDetailsFragment extends BottomSheetFragment implements OnMapReadyCallback, View.OnClickListener {


    public ContactDetailsFragment() {
        // Required empty public constructor
    }

    private DatabaseReference dbRef;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String contactNumber;
    private int PLACE_PICKER_REQUEST = 1;
    private MapView mapView;
    Contact contact;
    GoogleMap map;
    Button btnCallContact;
    Button btnDeleteContact;
    TextView tvContactDetails;
    TextView tvContactDetailsPhone;
    ValueEventListener listener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_details, container, false);
        /*References*/
        btnCallContact = view.findViewById(R.id.btnCallContact);
        tvContactDetails = view.findViewById(R.id.tvContactDetailsHeader);
        tvContactDetailsPhone = view.findViewById(R.id.tvContactDetailsPhone);
        mapView = view.findViewById(R.id.mapDisplay);
        btnDeleteContact = view.findViewById(R.id.btnDeleteContact);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        btnCallContact.setOnClickListener(this);
        btnDeleteContact.setOnClickListener(this);
        mapView.onCreate(savedInstanceState);
        contactNumber = getArguments().getString("contact");
        MapsInitializer.initialize(this.getActivity());
        dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("contacts").child(contactNumber);
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Contact c = dataSnapshot.getValue(Contact.class);
                contact = c;
                if(contact != null){
                    loadMap();
                    tvContactDetails.setText(contact.getContactName() + " (" + c.getContactNickName() + ")");
                    tvContactDetailsPhone.setText("Phone: " + c.getContactNumber());
                }else{
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.addValueEventListener(listener);

        return view;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void loadMap(){
        mapView.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng selected = new LatLng(contact.getContactLatitude(), contact.getContactLongtitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(selected, 10);
        googleMap.addMarker(new MarkerOptions().position(selected).title(contact.getContactName())).showInfoWindow();
        googleMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onClick(View view) {
        if(btnCallContact.getId() == view.getId()){
            /*Handle call action*/
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            String phone = "tel:" + contact.getContactNumber();
                            intent.setData(Uri.parse(phone));
                            startActivity(intent);
        }else if(btnDeleteContact.getId() == view.getId()){
            /*Handle Delete contact action*/
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle("Delete");
            alert.setMessage("Are you sure you want to delete?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ContactDAO db = new ContactDAO();
                    db.deleteContact(contact.getContactNumber());
                    dbRef.removeEventListener(listener);
                    dialog.dismiss();
                    Utility.SnackShort("Contact Deleted!", getActivity().findViewById(android.R.id.content));
                }
            });

            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alert.show();
        }
    }
}

