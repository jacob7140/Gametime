package com.example.gametime;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class CreateAccountFragment extends Fragment {

    private FirebaseAuth mAuth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final private String TAG = "data";

    public CreateAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    EditText editTextName, editTextEmail, editTextPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Register new account");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);

        editTextName = view.findViewById(R.id.editTextRegisterName);
        editTextEmail = view.findViewById(R.id.editTextRegisterEmail);
        editTextPassword = view.findViewById(R.id.editTextRegisterPassword);

        view.findViewById(R.id.buttonRegisterSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String name = editTextName.getText().toString();

                if (email.isEmpty() | name.isEmpty() | password.isEmpty()){
                    Toast.makeText(getActivity(), "Cannot have empty fields", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6){
                    Toast.makeText(getActivity(), "Password must be longer than 6 characters", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Log.d(TAG, "onComplete: Successfully created new account");
                                Log.d(TAG, "onComplete: " + name);
                                FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name).build();
                                user1.updateProfile(profileUpdates);
                                mListener.gotoHome();

                            } else{
                                Log.d(TAG, "onComplete: Could not create new account" + task.getException().getMessage());
                                Toast.makeText(getActivity(), "Error creating account", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }

            }
        });

        view.findViewById(R.id.buttonRegisterCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.gotoLogin();
            }
        });

        return view;
    }

    RegisterListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (RegisterListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RegisterListener");
        }
    }

    interface RegisterListener{
        void gotoLogin();
        void gotoHome();
    }
}