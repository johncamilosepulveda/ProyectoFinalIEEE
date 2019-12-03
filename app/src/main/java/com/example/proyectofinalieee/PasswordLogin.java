package com.example.proyectofinalieee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordLogin extends AppCompatActivity {

    private EditText et_email;
    private ImageButton btn_sendEmail;
    private ActionCodeSettings actionCodeSettings;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_login);

        auth = FirebaseAuth.getInstance();

        et_email=findViewById(R.id.et_emailPass);

        btn_sendEmail=findViewById(R.id.btn_sendEmail);
        btn_sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email=et_email.getText().toString();
                auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(PasswordLogin.this, "El correo se envió a "+email, Toast.LENGTH_LONG).show();
                            finish();
                        }else{
                            Toast.makeText(PasswordLogin.this, "Ocurrió un error", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }
}
