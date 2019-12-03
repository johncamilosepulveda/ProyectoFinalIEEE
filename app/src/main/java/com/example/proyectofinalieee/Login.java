package com.example.proyectofinalieee;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyectofinalieee.model.Usuario;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class Login extends AppCompatActivity {

    private FirebaseDatabase db;
    private FirebaseAuth auth;
    private FirebaseStorage storage;

    private FirebaseAuth.AuthStateListener authStateListener;

    private EditText et_email;
    private EditText et_pwd;

    private Button btn_login;
    private Button btn_registrar;
    private Button btn_olvideContra;
    private SignInButton btn_google;

    private final static int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //LLAMAR UN SERVICIO


        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Toast.makeText(getApplicationContext(), "Succed to connect", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Login.this, MenuIEEE.class);
                    startActivity(i);
                    finish();
                }
            }
        };



        et_email = findViewById(R.id.et_email);
        et_pwd = findViewById(R.id.et_pwd);

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(et_email.getText().toString(), et_pwd.getText().toString());
            }
        });

        btn_google = (SignInButton) findViewById(R.id.sign_in_google);

        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.app_name))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        btn_registrar=findViewById(R.id.btn_registrar);
        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Registro.class);
                startActivity(i);
                finish();
            }
        });
        btn_olvideContra=findViewById(R.id.btn_olvidarContra);
        btn_olvideContra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Login.this, PasswordLogin.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    private void login(String user, String password) {
        auth.signInWithEmailAndPassword(user, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "Inicio de sesión exitoso: ", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Login.this, MenuIEEE.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(Login.this, "Inicio de sesión fallido" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                Log.w("EXITO", "Login exitoso google");

            }
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {

                // Google Sign In failed, update UI appropriately
                Log.w("FAILED", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.e("msg",account.getDisplayName()+"|"+account.getEmail()+"|"+account.getFamilyName());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Succed", "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            db.getReference().child("Usuarios").orderByChild("correo").equalTo(user.getEmail()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Usuario user = dataSnapshot.getValue(Usuario.class);


                                    if (user==null){

                                        Usuario usuario= new Usuario();
                                        usuario.setCorreo(account.getEmail());
                                        usuario.setNombre(account.getDisplayName());

                                        Intent i = new Intent(Login.this,Registro.class);
                                        Bundle bundle= new Bundle();
                                        bundle.putSerializable("usuario",usuario);

                                        i.putExtras(bundle);
                                        startActivity(i);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FAILED", "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this, "Aut Fail", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }
}
