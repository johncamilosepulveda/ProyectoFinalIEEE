package com.example.proyectofinalieee;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectofinalieee.model.Constantes;
import com.example.proyectofinalieee.model.Usuario;
import com.example.proyectofinalieee.utilities.UtilDomi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Registro extends AppCompatActivity {

    FirebaseDatabase db;
    FirebaseAuth auth;
    FirebaseStorage storage;

    private static final int REQUEST_GALLERY = 101;
    private EditText et_usr;
    private EditText et_email;
    private EditText et_name;
    private EditText et_pwd;
    private EditText et_con_pwd;

    private Button btn_registrar;
    private ImageButton btn_anhadirFoto;


    private RadioGroup rg_tipoUsuario;
    private CheckBox cb_terminos;

    private Spinner sp_tipo_area;

    private FirebaseAuth.AuthStateListener authStateListener;

    private String path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        DatabaseReference dbr = db.getReference().child("estudiantes");

        sp_tipo_area = findViewById(R.id.sp_tipo_area);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, 11);


        String[] mensaje = {"Seleccionar tipo usuario"};
        final List<String> plantsList = new ArrayList<>(Arrays.asList(mensaje));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, plantsList);

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        sp_tipo_area.setAdapter(spinnerArrayAdapter);

        btn_anhadirFoto = findViewById(R.id.btn_anhadirFoto);
        btn_anhadirFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i, REQUEST_GALLERY);

            }
        });

        loadComponents();

        validarGoogle();
    }

    private void validarGoogle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            Usuario user = (Usuario) bundle.get("usuario");
            if (user != null) {
                et_email.setText(user.getCorreo());
                et_name.setText(user.getNombre());

                et_email.setEnabled(false);
                et_name.setEnabled(false);
                et_con_pwd.setVisibility(View.GONE);
                et_pwd.setVisibility(View.GONE);

                btn_registrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean aceptado = cb_terminos.isChecked();

                        if (rg_tipoUsuario.getCheckedRadioButtonId() == -1) {
                            Toast.makeText(Registro.this, "Debe seleccionar ser Estudiante/Trabajador", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (aceptado) {
                            RadioButton rb = findViewById(rg_tipoUsuario.getCheckedRadioButtonId());
                            Usuario user = new Usuario();
                            user.setFoto(path);
                            user.setUsuario(et_usr.getText().toString());
                            user.setArea(sp_tipo_area.getSelectedItem().toString());
                            user.setNombre(et_name.getText().toString());
                            user.setCorreo(et_email.getText().toString());
                            user.setUid("");
                            user.setTipoUsuario(rb.getText().toString());
                            user.setAdmin(false);
                            user.setUid(auth.getCurrentUser().getUid());
                            Log.i("USUARIO", "onComplete: " + user.getNombre());
                            DatabaseReference dbr = db.getReference().child("Usuarios").child(user.getUid());

                            //String id_imagen = dbr.getKey();
                            //user.setFoto(id_imagen);

                            dbr.setValue(user);

                            if (path != null) {
                                try {
                                    StorageReference ref = storage.getReference().child("fotos").child(user.getFoto());
                                    FileInputStream file = new FileInputStream(new File(path));
                                    Task res = ref.putStream(file);
                                    res.addOnSuccessListener(new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            user.setUid(auth.getCurrentUser().getUid());
                                            Log.i("USUARIO", "onComplete: " + user.getNombre());
                                            DatabaseReference dbr = db.getReference().child("Usuarios").child(user.getUid());

                                            //String id_imagen = dbr.getKey();
                                            //user.setFoto(id_imagen);

                                            dbr.setValue(user);

                                            if (path != null) {
                                                try {
                                                    StorageReference ref = storage.getReference().child("fotos").child(user.getFoto());
                                                    FileInputStream file = new FileInputStream(new File(path));
                                                    Task tarea = ref.putStream(file);
                                                    tarea.addOnSuccessListener(new OnSuccessListener() {
                                                        @Override
                                                        public void onSuccess(Object o) {
                                                            Intent i = new Intent(Registro.this, MenuIEEE.class);
                                                            startActivity(i);
                                                            finish();
                                                        }
                                                    });
                                                } catch (FileNotFoundException ex) {

                                                }
                                            }


                                        }
                                    });
                                } catch (FileNotFoundException ex) {

                                }
                            }
                        } else {
                            Toast.makeText(Registro.this, "Debe aceptar los términos y condiciones", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        } else {
            btn_registrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean aceptado = cb_terminos.isChecked();

                    if (rg_tipoUsuario.getCheckedRadioButtonId() == -1) {
                        Toast.makeText(Registro.this, "Debe seleccionar ser Estudiante/Trabajador", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (aceptado) {
                        RadioButton rb = findViewById(rg_tipoUsuario.getCheckedRadioButtonId());
                        Usuario user = new Usuario();
                        user.setFoto(path);
                        user.setUsuario(et_usr.getText().toString());
                        user.setArea(sp_tipo_area.getSelectedItem().toString());
                        user.setNombre(et_name.getText().toString());
                        user.setCorreo(et_email.getText().toString());
                        user.setUid("");
                        user.setTipoUsuario(rb.getText().toString());
                        user.setAdmin(false);
                        registrarUsuario(user);
                    } else {
                        Toast.makeText(Registro.this, "Debe aceptar los términos y condiciones", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    private void loadComponents() {
        et_con_pwd = findViewById(R.id.et_con_pwd);
        et_email = findViewById(R.id.et_email);
        et_name = findViewById(R.id.et_name);
        et_pwd = findViewById(R.id.et_pwd);
        et_usr = findViewById(R.id.et_usr);

        rg_tipoUsuario = findViewById(R.id.rg_tipo_usuario);
        rg_tipoUsuario.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_estudiante) {
                    final List<String> plantsList = new ArrayList<>(Arrays.asList(Constantes.ESTUDIANTES));

                    // Initializing an ArrayAdapter
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                            Registro.this, R.layout.spinner_item, plantsList);

                    spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                    sp_tipo_area.setAdapter(spinnerArrayAdapter);
                } else {
                    final List<String> plantsList = new ArrayList<>(Arrays.asList(Constantes.TRABAJADORES));

                    // Initializing an ArrayAdapter
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                            Registro.this, R.layout.spinner_item, plantsList);

                    spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                    sp_tipo_area.setAdapter(spinnerArrayAdapter);
                }
            }
        });
        int selected = rg_tipoUsuario.getCheckedRadioButtonId();

        btn_registrar = findViewById(R.id.btn_registrar);


        cb_terminos = findViewById(R.id.cb_terminos);

        TextView tv_verterminos = findViewById(R.id.tv_verTerminos);
        tv_verterminos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storage.getReference().child("archivos").child("Términos y condiciones.pdf")
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "application/pdf");

                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            //if user doesn't have pdf reader instructing to download a pdf reader
                        }
                    }
                });

            }
        });


    }

    private void registrarUsuario(final Usuario user) {
        if (et_pwd.getText().toString().length() <= 5) {
            Toast.makeText(this, "La contraseña debe ser de mínimo 6 o más caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!et_pwd.getText().toString().equals(et_con_pwd.getText().toString())) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(user.getCorreo(), et_pwd.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Registro.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                    user.setUid(auth.getCurrentUser().getUid());
                    Log.i("USUARIO", "onComplete: " + user.getNombre());
                    DatabaseReference dbr = db.getReference().child("Usuarios").child(user.getUid());

                    //String id_imagen = dbr.getKey();
                    //user.setFoto(id_imagen);

                    dbr.setValue(user);

                    if (path != null) {
                        try {
                            StorageReference ref = storage.getReference().child("fotos").child(user.getFoto());
                            FileInputStream file = new FileInputStream(new File(path));
                            Task tarea = ref.putStream(file);
                            tarea.addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    Intent i = new Intent(Registro.this, MenuIEEE.class);
                                    startActivity(i);
                                    finish();
                                }
                            });
                        } catch (FileNotFoundException ex) {

                        }
                    }


                    //Aquí va para el perfil


                } else {
                    Toast.makeText(Registro.this, "Registro fallido: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            path = UtilDomi.getPath(Registro.this, data.getData());
            Bitmap m = BitmapFactory.decodeFile(path);
            ImageView img_foto = findViewById(R.id.foto_registro);
            img_foto.setImageBitmap(m);
        }
    }
}
