package com.example.whist20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ForgotPassword extends AppCompatActivity {
    private Button reset;
    private EditText email_edit_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }
        setContentView(R.layout.activity_forgot_password);
        email_edit_text = (EditText) findViewById(R.id.forgetPasswordEmailAddress);

        reset = (Button) findViewById(R.id.forgotPasswordResetButton);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_edit_text.getText().toString().trim();
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String toast = "Something went wrong, make sure your email is correct";
                        if (task.isSuccessful()) toast = "Check your email to reset your password";
                        Toast.makeText(ForgotPassword.this, toast, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}