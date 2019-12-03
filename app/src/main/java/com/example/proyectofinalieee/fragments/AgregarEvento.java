package com.example.proyectofinalieee.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.proyectofinalieee.R;
import com.example.proyectofinalieee.model.Constantes;
import com.example.proyectofinalieee.model.Evento;
import com.example.proyectofinalieee.utilities.UtilDomi;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AgregarEvento.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AgregarEvento#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AgregarEvento extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String path;

    private DatePickerDialog.OnDateSetListener mDataSetListener;
    private TextView mDisplayDate;

    private TimePickerDialog.OnTimeSetListener onTimeSetListener;
    private TextView mDisplayTime;

    private Spinner sp_tipo_area;

    private FirebaseDatabase db;
    private FirebaseStorage storage;
    private OnFragmentInteractionListener mListener;

    public AgregarEvento() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AgregarEvento.
     */
    // TODO: Rename and change types and number of parameters
    public static AgregarEvento newInstance(String param1, String param2) {
        AgregarEvento fragment = new AgregarEvento();
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

    private FirebaseDatabase x;
    private FirebaseAuth auth;

    public int mYear;
    public int mMonth;
    public int mDay;

    public int hour;
    public int min;

    private Button btn_AgregarEvento;
    private ImageView btn_anhadirFoto;

    private static final int REQUEST_GALLERY = 101;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        final View view = inflater.inflate(R.layout.fragment_agregar_evento, container, false);
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

        inicializarComponentes(view);

        db = FirebaseDatabase.getInstance();
        storage= FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        btn_anhadirFoto = view.findViewById(R.id.btn_aniadirFoto);
        btn_anhadirFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i, REQUEST_GALLERY);

            }
        });

        btn_AgregarEvento = view.findViewById(R.id.btn_guardar_evento);
        btn_AgregarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dbr = db.getReference().child("Eventos").push();

                EditText nombreEvento = view.findViewById(R.id.et_name);
                EditText descripcion = view.findViewById(R.id.et_descripcion);
                EditText lugar = view.findViewById(R.id.et_lugar);
                String area = sp_tipo_area.getSelectedItem().toString();
                Evento evento = new Evento();

                evento.setNombre(nombreEvento.getText().toString());
                evento.setArea(area);
                evento.setLugar(lugar.getText().toString());
                evento.setDescripcion(descripcion.getText().toString());
                evento.setHour(hour);
                evento.setmYear(mYear);
                evento.setmMonth(mMonth);
                evento.setImg(path);
                evento.setmDay(mDay);
                evento.setMin(min);




                if (path != null) {
                    try {
                        StorageReference ref = storage.getReference().child("fotos").child(evento.getImg());
                        FileInputStream file = new FileInputStream(new File(path));
                        Task tarea=ref.putStream(file);
                        tarea.addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                dbr.setValue(evento);

                                nombreEvento.setText("");
                                descripcion.setText("");
                                lugar.setText("");

                                Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.contenedorFragments);
                                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                fragmentTransaction.detach(currentFragment);
                                fragmentTransaction.attach(currentFragment);
                                fragmentTransaction.commit();

                                Toast.makeText(getContext(), "El evento se creó exitosamente!", Toast.LENGTH_SHORT).show();

                            }
                        });
                        Toast.makeText(getContext(), "El evento se ha creado exitosamente! Espere mientras se sube la foto", Toast.LENGTH_LONG).show();

                    } catch (FileNotFoundException ex) {

                    }
                }


            }
        });

        return view;
    }

    private void inicializarComponentes(final View view) {
        mDisplayDate = view.findViewById(R.id.etDate);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        view.getContext(),
                        R.style.Theme_AppCompat_DayNight_Dialog,
                        mDataSetListener,
                        year, month, day);

                //datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();

            }
        });

        mDataSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                mDisplayDate.setText(year + "/" + month + "/" + dayOfMonth);
                mYear = year;
                mMonth = month;
                mDay = dayOfMonth;
            }
        };


        mDisplayTime = view.findViewById(R.id.etHour);
        mDisplayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        view.getContext(),
                        onTimeSetListener,
                        hour,
                        minute,
                        false
                );

                timePickerDialog.show();
            }
        });

        onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if(minute<10) {
                    mDisplayTime.setText(hourOfDay + ":0" + minute);
                }else{
                    mDisplayTime.setText(hourOfDay + ":" + minute);
                }
                hour = hourOfDay;
                min = minute;
            }
        };

        // Initializing an ArrayAdapter
        sp_tipo_area = view.findViewById(R.id.sp_tipo_area);

        String[] mensaje = Constantes.TIPOS_FOROS;
        final List<String> plantsList = new ArrayList<>(Arrays.asList(mensaje));
        plantsList.add("PSU");
        plantsList.add("General");
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                view.getContext(), R.layout.spinner_item, plantsList);

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        sp_tipo_area.setAdapter(spinnerArrayAdapter);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GALLERY && resultCode == this.getActivity().RESULT_OK) {
            path = UtilDomi.getPath(this.getContext(), data.getData());
            Bitmap m = BitmapFactory.decodeFile(path);
            ImageView img_foto = this.getActivity().findViewById(R.id.foto);
            img_foto.setImageBitmap(m);
        }
    }
}
