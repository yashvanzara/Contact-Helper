package com.yashvanzara.www.contacthelper;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yashvanzara.www.contacthelper.Utils.Utility;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewContactsFragment extends Fragment {


    public ViewContactsFragment() {
        // Required empty public constructor
    }
    /*Firebase fields*/
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    DatabaseReference dbRef;
    private FirebaseUser user;

    MaterialListView mListView;
    ValueEventListener vlistener;
    ProgressBar pb;
    protected BottomSheetLayout bottomSheetLayout;
    TextView tvNoData;//Placeholder if no contact exists
    Contact contactForEditing;//Keeps track of the clicked contact for editing
    NavigationView navigationView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_contacts, container, false);
        /*References*/
        navigationView = getActivity().findViewById(R.id.nav_view);
        bottomSheetLayout =  view.findViewById(R.id.bottomsheet);
        tvNoData = view.findViewById(R.id.tvNoData);
        mListView = view.findViewById(R.id.material_listview);
        pb = view.findViewById(R.id.loaderViewContacts);

        navigationView.setCheckedItem(R.id.nav_view_contacts);
        bottomSheetLayout.setUseHardwareLayerWhileAnimating(true);

        startAnim();
        /*Firebase*/
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("contacts");
        vlistener = new ValueEventListener() {
            /*Listener to keep sync with data on firebase*/
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mListView.getAdapter().clearAll();
                final ArrayList<Contact> contacts = new ArrayList<>();
                for(DataSnapshot contactSnapShop:dataSnapshot.getChildren()){
                    final String key = contactSnapShop.getKey();
                    Contact c = contactSnapShop.getValue(Contact.class);
                    contacts.add(c);
                    /*Loop through the updated tree and update local copy as a card*/
                    final Card card = new Card.Builder(getActivity())
                            .setTag(c.getContactNumber())
                            .withProvider(new CardProvider())
                            .setLayout(R.layout.custom_contact_card)
                            .setDescription("Phone: " + c.getContactNumber())
                            .setTitle(c.getContactName() + " ("+c.getContactNickName() + ")")
                            .addAction(R.id.right_text_button, new TextViewAction(getActivity())
                                    /*Action to handle display of contact*/
                                    .setText("View")
                                    .setTextResourceColor(R.color.accent_material_dark)
                                    .setListener(new OnActionClickListener() {
                                        @Override
                                        public void onActionClicked(View view, Card card) {
                                            ContactDetailsFragment fgmt = new ContactDetailsFragment();
                                            Bundle bundle = new Bundle();
                                            bundle.putString("contact", key);
                                            fgmt.setArguments(bundle);
                                            fgmt.show(getActivity().getSupportFragmentManager(), R.id.bottomsheet);
                                        }
                                    }))
                            .addAction(R.id.left_text_button, new TextViewAction(getActivity())
                                    /*Action to handle editing of a contact*/
                                    .setText("Edit")
                                    .setTextResourceColor(R.color.accent_material_dark)
                                    .setListener(new OnActionClickListener() {
                                        @Override
                                        public void onActionClicked(View view, Card card) {
                                            final String contact = card.getTag().toString();
                                            Bundle args = new Bundle();
                                            args.putSerializable("contact", contactForEditing);
                                            CreateContactFragment fgmt = new CreateContactFragment();
                                            fgmt.setArguments(args);
                                            navigationView.setCheckedItem(R.id.nav_add_contact);
                                            Utility.changeFragment(fgmt, getActivity());
                                        }
                                    }))
                            .endConfig()
                            .build();
                    mListView.getAdapter().add(card);
                }
                bottomSheetLayout.dismissSheet();
                mListView.scrollToPosition(0);

                /*Keep track of selected item for editing*/
                mListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
                    /*Keeps track of the most recently interacted contact for updating purpose*/
                    @Override
                    public void onItemClick(@NonNull Card card, int position) {
                        contactForEditing = contacts.get(position);
                    }

                    @Override
                    public void onItemLongClick(@NonNull Card card, int position) {

                    }
                });

                /*Change views if there are no items to display*/
                if(mListView.getAdapter().getItemCount()==0){
                    tvNoData.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                }else{
                    tvNoData.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                }

                stopAnim();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.addValueEventListener(vlistener);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        dbRef.removeEventListener(vlistener);
    }
    void startAnim(){
        pb.setVisibility(View.VISIBLE);
    }

    void stopAnim(){
        pb.setVisibility(View.GONE);
    }
}
