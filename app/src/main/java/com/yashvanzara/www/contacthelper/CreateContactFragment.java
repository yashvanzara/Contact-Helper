package com.yashvanzara.www.contacthelper;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.yashvanzara.www.contacthelper.Utils.Utility;

import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateContactFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, Validator.ValidationListener {


    public CreateContactFragment() {
        // Required empty public constructor
    }

    /*Create contact fields*/
    @NotEmpty(message = "Must enter name")
    @Length(max = 30, message = "Name cannot exceed 30 characters")
    private EditText etNewContactName;
    @Length(min=10, message = "Phone number must be of 10 digits")
    @NotEmpty(message = "Phone number is reqired")
    private EditText etNewContactPhone;
    @NotEmpty(message = "Must enter Nickname")
    @Length(max = 15, message = "Nickname cannot exceed 15 characters")
    private EditText etNewContackNickName;
    private Button btnSaveContact;
    /*End of create contact fields*/

    /*Firebase fields*/
    FirebaseAuth mAuth;
    FirebaseUser user;
    private DatabaseReference mDatabase;

    /*End of Firebase fields*/

    /*Place Picker fields*/
    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;
    private Button btnContactLocation;
    final int INVALID_LAT_LONG = -999;
    private double contactLatitude = INVALID_LAT_LONG;
    private double contactLongtitude = INVALID_LAT_LONG;
    private MapView mapView;
    GoogleMap googleMap;
    SupportMapFragment mapFragment;
    /*End of Place picker fields*/

    Validator validator;//Validator for fields

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_create_contact, container, false);

        Bundle b = getArguments();
        if(b!=null){
            Contact c = (Contact)b.getSerializable("contact");
            if(c!=null){
                Log.d("CCCC", c.toString());
            }
        }

        validator = new Validator(this);
        validator.setValidationListener(this);

        /*Register views*/
        etNewContactName = view.findViewById(R.id.etNewContactName);
        etNewContactPhone = view.findViewById(R.id.etNewContactPhone);
        etNewContackNickName = view.findViewById(R.id.etNewContactNickName);
        btnSaveContact = view.findViewById(R.id.btnSaveContact);
        btnContactLocation = view.findViewById(R.id.btnContactLocation);
        btnSaveContact.setOnClickListener(this);
        btnContactLocation.setOnClickListener(this);

        /*Get Firebase and map references*/
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        return view;
    }

    @Override
    public void onPause() {;
        mGoogleApiClient.disconnect();
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btnSaveContact.getId()){
            /*If Save button clicked, check for valid location and then validate contact fields*/
            if(this.contactLatitude==INVALID_LAT_LONG || this.contactLongtitude == INVALID_LAT_LONG){
                Toast.makeText(getActivity(), "Select a valid location", Toast.LENGTH_SHORT).show();
                return;
            }
            validator.validate();
        }else if(view.getId() == btnContactLocation.getId()){
            /*Open place picker*/
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(getActivity(), data);
                this.contactLatitude = place.getLatLng().latitude;
                this.contactLongtitude = place.getLatLng().longitude;
                mapFragment.getMapAsync(this);
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(contactLongtitude!=INVALID_LAT_LONG&&contactLongtitude!=INVALID_LAT_LONG){
            googleMap.clear();
                LatLng selected = new LatLng(contactLatitude, contactLongtitude);
                googleMap.addMarker(new MarkerOptions().position(selected)
                        .title(etNewContactName.getText().toString()));
                googleMap.setMinZoomPreference(8);
                googleMap.setMaxZoomPreference(15);
                googleMap.setBuildingsEnabled(true);
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(selected));
        }
    }

    @Override
    public void onValidationSucceeded() {
        Contact c = new Contact(etNewContactPhone.getText().toString(), etNewContactName.getText().toString(), etNewContackNickName.getText().toString(), contactLatitude, contactLongtitude);
        ContactDAO db = new ContactDAO();
        db.addContact(c);
        clearFields();
        Utility.SnackShort("Contact Saved!", getActivity().findViewById(android.R.id.content));
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getActivity());

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }
    }
    private void clearFields(){
        etNewContactName.setText("");
        etNewContactPhone.setText("");
        etNewContackNickName.setText("");
        this.contactLatitude = INVALID_LAT_LONG;
        this.contactLongtitude = INVALID_LAT_LONG;
    }
}
