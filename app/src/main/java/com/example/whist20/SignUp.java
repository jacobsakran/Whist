package com.example.whist20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private EditText email_edit_text, password_edit_text, confirm_password_edit_text, username_edit_text;
    private Button sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        sign_up = (Button) findViewById(R.id.signUpButton);
        sign_up.setOnClickListener(this);

        email_edit_text = (EditText) findViewById(R.id.signUpEmailAddress);
        password_edit_text = (EditText) findViewById(R.id.signUpPassword);
        confirm_password_edit_text = (EditText) findViewById(R.id.signUpConfirmPassword);
        username_edit_text = (EditText) findViewById(R.id.signUpUsername);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signUpButton:
                try {
                    signUpUser();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    private void signUpUser() throws InterruptedException {
        String username = username_edit_text.getText().toString().trim();
        String email = email_edit_text.getText().toString().trim();
        String password = password_edit_text.getText().toString().trim();
        String confirm_password = confirm_password_edit_text.getText().toString().trim();
        boolean error = email.isEmpty() || password.isEmpty() || confirm_password.isEmpty() ||
                !password.equals(confirm_password) || username.isEmpty();

        if (username.isEmpty()) username_edit_text.setError("Username is required!");
        if (email.isEmpty()) email_edit_text.setError("Email is required!");
        if (password.isEmpty()) password_edit_text.setError("Password is required!");
        if (confirm_password.isEmpty()) confirm_password_edit_text.setError("Confirm Password is required!");
        if (!password.equals(confirm_password)) confirm_password_edit_text.setError("Confirm Password and Password do not match!");
        if (error) return;


        mAuth.fetchSignInMethodsForEmail(email).addOnSuccessListener(new OnSuccessListener<SignInMethodQueryResult>() {
            @Override
            public void onSuccess(SignInMethodQueryResult signInMethodQueryResult) {
                if (signInMethodQueryResult.getSignInMethods() == null) return;
                if (signInMethodQueryResult.getSignInMethods().size() > 0) email_edit_text.setError("Email already exists!");
            }
        });

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser fire_base_user = FirebaseAuth.getInstance().getCurrentUser();
                    User user = new User(email, username, fire_base_user.getUid());
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(fire_base_user.getUid()).setValue(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        DatabaseReference mapping = FirebaseDatabase.getInstance().getReference("UsernameToUid");
                                        mapping.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (!snapshot.hasChild(username)) {
                                                    mapping.child(username).setValue(fire_base_user.getUid());
                                                    fire_base_user.sendEmailVerification();
                                                    Toast.makeText(SignUp.this, "Verify your email", Toast.LENGTH_LONG).show();
                                                    return;
                                                }

                                                username_edit_text.setError("Username already exists!");
                                                fire_base_user.delete();
                                                FirebaseDatabase.getInstance().getReference("Users")
                                                        .child(fire_base_user.getUid()).removeValue();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });

                                    }
                                    else Toast.makeText(SignUp.this, "Failed to create user", Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
    }
}