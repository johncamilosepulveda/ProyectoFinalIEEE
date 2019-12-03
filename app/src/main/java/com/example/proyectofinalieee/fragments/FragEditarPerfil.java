package com.example.proyectofinalieee.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.proyectofinalieee.R;
import com.example.proyectofinalieee.model.Constantes;
import com.example.proyectofinalieee.model.Usuario;
import com.example.proyectofinalieee.utilities.UtilDomi;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragEditarPerfil.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragEditarPerfil#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragEditarPerfil extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_GALLERY = 101;

    private String path;


    private EditText editTextNombre;
    private EditText editTextUsuario;
    private EditText editTextCorreoElectronico;
    private EditText et_tipo_user;
    private Spinner spinnerAreadeTrabajo;
    private Spinner spinnerRol;

    private ImageView iv_foto;
    private ImageButton btn_cambiarFoto;
    private ImageButton imageButtonGuardar;
    private FirebaseDatabase db;
    private FirebaseAuth auth;
    private FirebaseStorage storage;

    private Usuario currentUser;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragEditarPerfil() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragEditarPerfil.
     */
    // TODO: Rename and change types and number of parameters
    public static FragEditarPerfil newInstance(String param1, String param2) {
        FragEditarPerfil fragment = new FragEditarPerfil();
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

        final View viewInflater = inflater.inflate(R.layout.fragment_frag_editar_perfil, container, false);

        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        editTextUsuario = viewInflater.findViewById(R.id.et_user);
        editTextCorreoElectronico = viewInflater.findViewById(R.id.et_correo);
        et_tipo_user = viewInflater.findViewById(R.id.et_tipo_user);

        editTextNombre = viewInflater.findViewById(R.id.et_nombre);
        btn_cambiarFoto = viewInflater.findViewById(R.id.btn_cambiarFoto);
        iv_foto = viewInflater.findViewById(R.id.foto);

        imageButtonGuardar = viewInflater.findViewById(R.id.btn_save_changes);

        spinnerAreadeTrabajo = viewInflater.findViewById(R.id.sp_area);

        btn_cambiarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i, REQUEST_GALLERY);
            }
        });

        imageButtonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference myRef = db.getReference("Usuarios");
                myRef.orderByChild("correo").equalTo(auth.getCurrentUser().getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.e(">>", "Entra al boton guardar");
                        for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                            String key = postsnapshot.getKey();
                            Usuario user = postsnapshot.getValue(Usuario.class);
                            user.setNombre(editTextNombre.getText().toString());
                            user.setUsuario(editTextUsuario.getText().toString());
                            user.setArea(spinnerAreadeTrabajo.getSelectedItem().toString());

                            if (path != null) {
                                try {
                                    StorageReference ref = storage.getReference().child("fotos").child(user.getFoto());
                                    ref.delete();

                                    user.setFoto(path);

                                    postsnapshot.getRef().setValue(user);
                                    ref = storage.getReference().child("fotos").child(user.getFoto());
                                    FileInputStream file = new FileInputStream(new File(path));
                                    Task progress = ref.putStream(file);
                                    progress.addOnSuccessListener(new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {

                                            NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                                            navigationView.removeHeaderView(navigationView.getHeaderView(0));
                                            View headerLayout =
                                                    navigationView.inflateHeaderView(R.layout.nav_header_menu_ieee);
                                            ImageView img_usuario = headerLayout.findViewById(R.id.img_usuario);
                                            StorageReference storageReference = storage.getReference().child("fotos").child(user.getFoto());
                                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {

                                                    Glide.with(getActivity()).load(uri)
                                                            .apply(RequestOptions.circleCropTransform())
                                                            .into(img_usuario);

                                                    FragMostrarEvento fragmento = new FragMostrarEvento();

                                                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                                    transaction.replace(R.id.contenedorFragments, fragmento);
                                                    transaction.commit();
                                                    Log.e(">>>", "La foto se ha actualizado correctamente");
                                                }
                                            });


                                        }
                                    });
                                } catch (FileNotFoundException ex) {

                                }
                            } else {
                                postsnapshot.getRef().setValue(user);
                                FragMostrarEvento fragmento = new FragMostrarEvento();

                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.contenedorFragments, fragmento);
                                transaction.commit();
                            }


                            Log.e(">>", user.getNombre());
                            Log.e(">> Que elimine?", "" + key);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        cargarDatos();


        return viewInflater;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GALLERY && resultCode == getActivity().RESULT_OK) {
            path = UtilDomi.getPath(getContext(), data.getData());
            Bitmap m = BitmapFactory.decodeFile(path);
            iv_foto.setImageBitmap(m);
        }
    }

    private void cargarDatos() {
        DatabaseReference myRef = db.getReference("Usuarios");

        myRef.orderByChild("correo").equalTo(auth.getCurrentUser().getEmail()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Usuario user = dataSnapshot.getValue(Usuario.class);
                currentUser = user;

                final ArrayList listaOpciones = new ArrayList<>();
                final String[] opcionesEstudiantes = Constantes.ESTUDIANTES;
                final String[] opcionesTrabadores = Constantes.TRABAJADORES;

                editTextNombre.setText(user.getNombre());
                editTextCorreoElectronico.setText(user.getCorreo());
                editTextUsuario.setText(user.getUsuario());
                et_tipo_user.setText(user.getTipoUsuario());

                storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference().child("fotos").child(user.getFoto());
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getContext()).load(uri)
                                .apply(new RequestOptions()
                                        .centerCrop())
                                .into(iv_foto);
                    }
                });
                ArrayAdapter<String> spinnerArrayAdapter;
                if (user.getTipoUsuario().equals("Estudiante")) {
                    spinnerArrayAdapter = new ArrayAdapter<String>(
                            getActivity(), R.layout.spinner_item, opcionesEstudiantes);
                } else {
                    spinnerArrayAdapter = new ArrayAdapter<String>(
                            getActivity(), R.layout.spinner_item, opcionesTrabadores);
                }


                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                spinnerAreadeTrabajo.setAdapter(spinnerArrayAdapter);

                int index = spinnerArrayAdapter.getPosition(user.getArea());
                Log.e(">>Index", index + "");
                spinnerAreadeTrabajo.setSelection(index);

                //


                if (user.getTipoUsuario().equals("Estudiante")) {

                    Collections.addAll(listaOpciones, opcionesEstudiantes);
                } else {
                    Collections.addAll(listaOpciones, opcionesTrabadores);
                }


                //Implemento el adapter con el contexto, layout, listaFrutas

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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