package com.example.proyectofinalieee.fragments;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectofinalieee.R;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragRegistro.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragRegistro#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragRegistro extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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
    private ImageView img_foto;


    private CheckBox cb_terminos;


    private FirebaseAuth.AuthStateListener authStateListener;

    private String path;


    public FragRegistro() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragRegistro.
     */
    // TODO: Rename and change types and number of parameters
    public static FragRegistro newInstance(String param1, String param2) {
        FragRegistro fragment = new FragRegistro();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_registro, container, false);

        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        DatabaseReference dbr = db.getReference().child("estudiantes");

        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, 11);

        btn_anhadirFoto = view.findViewById(R.id.btn_anhadirFoto);
        btn_anhadirFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i, REQUEST_GALLERY);

            }
        });

        loadComponents(view);


        return view;
    }

    private void loadComponents(View view) {
        et_con_pwd = view.findViewById(R.id.et_con_pwd);
        et_email = view.findViewById(R.id.et_email);
        et_name = view.findViewById(R.id.et_name);
        et_pwd = view.findViewById(R.id.et_pwd);
        et_usr = view.findViewById(R.id.et_usr);
        img_foto = view.findViewById(R.id.foto_registro);


        btn_registrar = view.findViewById(R.id.btn_registrar);
        cb_terminos = view.findViewById(R.id.cb_terminos);

        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean aceptado = cb_terminos.isChecked();


                if (aceptado) {
                    Usuario user = new Usuario();
                    user.setFoto(path);
                    user.setUsuario(et_usr.getText().toString());
                    user.setArea("Bienestar Universitario");
                    user.setNombre(et_name.getText().toString());
                    user.setCorreo(et_email.getText().toString());
                    user.setUid("");
                    user.setTipoUsuario("Trabajador");
                    user.setAdmin(true);
                    registrarUsuario(user);
                } else {
                    Toast.makeText(getContext(), "Debe aceptar los términos y condiciones", Toast.LENGTH_SHORT).show();
                }

            }
        });
        TextView tv_verterminos= view.findViewById(R.id.tv_verTerminos);
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

    private void registrarUsuario(Usuario user) {
        if (et_pwd.getText().toString().length() <= 5) {
            Toast.makeText(getContext(), "La contraseña debe ser de mínimo 6 o más caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!et_pwd.getText().toString().equals(et_con_pwd.getText().toString())) {
            Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(user.getCorreo(), et_pwd.getText().toString()).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();
                    user.setUid(auth.getCurrentUser().getUid());
                    Log.i("USUARIO", "onComplete: " + user.getNombre());
                    DatabaseReference dbr = db.getReference().child("Usuarios").child(user.getUid());

                    dbr.setValue(user);

                    if (path != null) {
                        try {
                            StorageReference ref = storage.getReference().child("fotos").child(user.getFoto());
                            FileInputStream file = new FileInputStream(new File(path));
                            Task tarea = ref.putStream(file);
                            tarea.addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    FragMostrarEvento fragmento = new FragMostrarEvento();

                                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                    transaction.replace(R.id.contenedorFragments, fragmento);
                                    transaction.commit();
                                }
                            });
                        } catch (FileNotFoundException ex) {

                        }
                    }


                    //Aquí va para el perfil


                } else {
                    Toast.makeText(getContext(), "Registro fallido: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GALLERY && resultCode == getActivity().RESULT_OK) {
            path = UtilDomi.getPath(getContext(), data.getData());
            Bitmap m = BitmapFactory.decodeFile(path);
            img_foto.setImageBitmap(m);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}