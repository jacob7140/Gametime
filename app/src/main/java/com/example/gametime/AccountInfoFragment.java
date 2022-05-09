package com.example.gametime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;

public class AccountInfoFragment extends Fragment {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();


    public AccountInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    EditText editTextFN, editTextEmail, editTextPassword, editTextConfirmPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_info, container, false);

        editTextFN = view.findViewById(R.id.editTextAccountInfoFN);
        editTextEmail = view.findViewById(R.id.editTextAccountInfoEmail);
        editTextPassword = view.findViewById(R.id.editTextAccountInfoPassword);
        editTextConfirmPassword = view.findViewById(R.id.editTextAccountInfoConfirmPassword);

        editTextFN.setText(user.getDisplayName());
        editTextEmail.setText(user.getEmail());

        view.findViewById(R.id.buttonAccountInfoSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle("Are you sure you want to make these changes?");
                builder.setMessage("By confirming, changes to your account will be made!");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String FN = editTextFN.getText().toString();
                                String email = editTextEmail.getText().toString();
                                String password = editTextPassword.getText().toString();
                                String confirmPassword = editTextConfirmPassword.getText().toString();

                                if (FN.isEmpty()) {
                                    Toast.makeText(getActivity(), "First name field can't be empty.", Toast.LENGTH_SHORT).show();
                                } else if(!user.getDisplayName().equals(FN)) {
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(FN).build();
                                    user.updateProfile(profileUpdates);
                                }

                                if (email.isEmpty()) {
                                    Toast.makeText(getActivity(), "Email field can't be empty.", Toast.LENGTH_SHORT).show();
                                } else if(!user.getEmail().equals(email)) {
                                    user.updateEmail(email);
                                }

                                if (password.length() > 0 && !password.equals(confirmPassword)) {
                                    Toast.makeText(getActivity(), "Passwords must match", Toast.LENGTH_SHORT).show();
                                } else if (password.length() > 0 && password.length() < 6) {
                                    Toast.makeText(getActivity(), "Password must be longer than 5 characters.", Toast.LENGTH_SHORT).show();
                                } else if (password.length() > 0) {
                                    user.updatePassword(password);
                                }

                                mListener.goToProfile();
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        view.findViewById(R.id.buttonAccountInfoBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goToProfile();
            }
        });

        return view;
    }

    AccountInfoListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (AccountInfoListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RegisterListener");
        }
    }

    interface AccountInfoListener{
        void goToProfile();
    }
}