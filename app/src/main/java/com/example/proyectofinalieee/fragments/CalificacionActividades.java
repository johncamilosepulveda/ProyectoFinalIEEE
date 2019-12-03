package com.example.proyectofinalieee.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectofinalieee.R;
import com.example.proyectofinalieee.model.Foro;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalificacionActividades.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CalificacionActividades#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalificacionActividades extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static RatingBar ratingBarp1;
    private static RatingBar ratingBarp2;
    private static RatingBar ratingBarp3;
    private static RatingBar ratingBarp4;
    private static RatingBar ratingBarp5;
    private static RatingBar ratingBarp6;
    private static RatingBar ratingBarp7;
    private static RatingBar ratingBarp8;
    private static RatingBar ratingBarp9;
    private static RatingBar ratingBarp10;
    private static RatingBar ratingBarp11;

    private RatingBar[] ratingBars;

    private Button buttonGuardarEncuesta;

    private FirebaseDatabase db;

    private Foro foro;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CalificacionActividades() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalificacionActividades.
     */
    // TODO: Rename and change types and number of parameters
    public static CalificacionActividades newInstance(String param1, String param2) {
        CalificacionActividades fragment = new CalificacionActividades();
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
        db = FirebaseDatabase.getInstance();

        View viewinflate = inflater.inflate(R.layout.fragment_calificacion_actividades, container, false);
        Bundle bundle = getArguments();
        TextView tv_nombre = viewinflate.findViewById(R.id.tv_nombre);
        foro = (Foro) bundle.get("actividad");
        tv_nombre.setText(foro.getNombre());

        ratingBarp1 = viewinflate.findViewById(R.id.pregunta1);
        ratingBarp2 = viewinflate.findViewById(R.id.pregunta2);
        ratingBarp3 = viewinflate.findViewById(R.id.pregunta3);
        ratingBarp4 = viewinflate.findViewById(R.id.pregunta4);
        ratingBarp5 = viewinflate.findViewById(R.id.pregunta5);
        ratingBarp6 = viewinflate.findViewById(R.id.pregunta6);
        ratingBarp7 = viewinflate.findViewById(R.id.pregunta7);
        ratingBarp8 = viewinflate.findViewById(R.id.pregunta8);
        ratingBarp9 = viewinflate.findViewById(R.id.pregunta9);
        ratingBarp10 = viewinflate.findViewById(R.id.pregunta10);
        ratingBarp11 = viewinflate.findViewById(R.id.pregunta11);

        ratingBars = new RatingBar[]{ratingBarp1, ratingBarp2, ratingBarp3, ratingBarp4
                , ratingBarp5, ratingBarp6, ratingBarp7, ratingBarp8, ratingBarp9
                , ratingBarp10, ratingBarp11};


        buttonGuardarEncuesta = viewinflate.findViewById(R.id.bt_calificar_actividad);

        buttonGuardarEncuesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference dbr = db.getReference().child("Actividades")
                        .orderByChild("nombre").equalTo(foro.getNombre()).getRef();
                dbr.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        for (DataSnapshot postSnap :
                                dataSnapshot.getChildren()) {
                            Foro act = postSnap.getValue(Foro.class);
                            if (act.getNombre().equals(foro.getNombre())) {
                                for (int i = 1; i < 12; i++) {
                                    int estrellas = (int) ratingBars[i - 1].getRating();
                                    DatabaseReference raiz = postSnap.getRef().child("Encuestas");
                                    raiz.child("P" + i).child(estrellas + "")
                                            .push().setValue(new Date());
                                }

                                Toast.makeText(getActivity(), "Se ha calificado correctamente la actividad " + foro.getNombre()
                                        , Toast.LENGTH_LONG).show();
                                FragMostrarEvento fep = new FragMostrarEvento();

                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.contenedorFragments, fep);
                                transaction.commit();
                            }
                        }
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
        });

        //para obtener el valor de la calificación
        //ratingbar1.getRating();


        return viewinflate;

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
