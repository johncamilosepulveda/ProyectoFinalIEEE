package com.example.proyectofinalieee.fragments;

import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.proyectofinalieee.Adapters.ForoAdapter;
import com.example.proyectofinalieee.R;
import com.example.proyectofinalieee.model.Constantes;
import com.example.proyectofinalieee.model.Foro;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragSalud.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragSalud#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragSalud extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private TextView tv_item;
    private ListView lv_items;

    private FirebaseStorage storage;
    private FirebaseDatabase db;


    private ForoAdapter actividadAdapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragSalud() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragSalud.
     */
    // TODO: Rename and change types and number of parameters
    public static FragSalud newInstance(String param1, String param2) {
        FragSalud fragment = new FragSalud();
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
        View inflaterView = inflater.inflate(R.layout.fragment_frag_salud, null);

        //tv_item = inflaterView.findViewById(R.id.tv_item);

        actividadAdapter = new ForoAdapter(this.getContext());


        db = FirebaseDatabase.getInstance();
        db.getReference().child("Actividades").child(Constantes.TIPOS_FOROS[2]).addChildEventListener
                (new ChildEventListener() {
                     @Override
                     public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                         Foro actividad = dataSnapshot.getValue(Foro.class);
                         Log.e(">>", actividad.getNombre());
                         actividadAdapter.addActividad(actividad);
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
                 }
                );

        lv_items = inflaterView.findViewById(R.id.lv_deportes);
        lv_items.setAdapter(actividadAdapter);
        lv_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FragForo fragmento = new FragForo();
                Foro actividad = actividadAdapter.getItem(i);

                Bundle bundle = new Bundle();
                bundle.putSerializable("actividad", actividad);
                fragmento.setArguments(bundle);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.contenedorFragments, fragmento);
                transaction.commit();
            }
        });
        // Inflate the layout for this fragment
        return inflaterView;
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
