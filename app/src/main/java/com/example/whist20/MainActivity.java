package com.example.whist20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView sign_up, forgot_password;
    private EditText email_edit_text, password_edit_text;
    private Button log_in_button;
    private FirebaseAuth mAuth;
    private ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        progress_bar = (ProgressBar) findViewById(R.id.mainActivityProgressBar);
        progress_bar.setVisibility(View.INVISIBLE);

        email_edit_text = (EditText) findViewById(R.id.logInEmailAddress);
        password_edit_text = (EditText) findViewById(R.id.logInPassword);

        sign_up = (TextView) findViewById(R.id.signUp);
        sign_up.setOnClickListener(this);

        forgot_password = (TextView) findViewById(R.id.forgotPassword);
        forgot_password.setOnClickListener(this);

        log_in_button = (Button) findViewById(R.id.logInButton);
        log_in_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signUp:
                startActivity(new Intent(MainActivity.this, SignUp.class));
                break;
            case R.id.logInButton:
                logInUser();
                break;
            case R.id.forgotPassword:
                startActivity(new Intent(MainActivity.this, ForgotPassword.class));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    private void logInUser() {
        String email = email_edit_text.getText().toString().trim();
        String password = password_edit_text.getText().toString().trim();
        boolean error = email.isEmpty() || password.isEmpty();

        if (email.isEmpty()) email_edit_text.setError("Email is required!");
        if (password.isEmpty()) password_edit_text.setError("Password is required!");
        if (error) return;

        progress_bar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null) return;
                    if (user.isEmailVerified()) startActivity(new Intent(MainActivity.this, Profile.class));
                    else Toast.makeText(MainActivity.this, "Please verify your email first", Toast.LENGTH_LONG).show();
                } else Toast.makeText(MainActivity.this, "Wrong email or password", Toast.LENGTH_LONG).show();
                progress_bar.setVisibility(View.INVISIBLE);
            }
        });
    }
}