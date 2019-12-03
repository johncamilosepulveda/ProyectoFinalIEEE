package com.example.proyectofinalieee.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.proyectofinalieee.Adapters.HorarioAdapter;
import com.example.proyectofinalieee.R;
import com.example.proyectofinalieee.model.Foro;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragForo.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragForo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragForo extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView nombre;
    private ImageView imagenActividad;
    private TextView descripcion;
    private Button botonCalificarActividad;

    private CalificacionActividades calificacionActividades;

    private Foro actividad;

    private ListView view_horarios;
    private HorarioAdapter horarioAdapter;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragForo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragActividad.
     */
    // TODO: Rename and change types and number of parameters
    public static FragForo newInstance(String param1, String param2) {
        FragForo fragment = new FragForo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        actividad = (Foro) bundle.getSerializable("actividad");
        Log.e(">>: ", actividad.getNombre());

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    FirebaseDatabase db;
    FirebaseAuth auth;
    FirebaseStorage storage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        calificacionActividades = new CalificacionActividades();

        final View viewInflater = inflater.inflate(R.layout.fragment_frag_foro, null);
        imagenActividad = viewInflater.findViewById(R.id.iv_imagen);
        nombre = viewInflater.findViewById(R.id.tv_nombre);
        descripcion = viewInflater.findViewById(R.id.tv_descripcion);

        view_horarios = viewInflater.findViewById(R.id.list_horarios);
        horarioAdapter = new HorarioAdapter(this.getActivity());
        view_horarios.setAdapter(horarioAdapter);
        horarioAdapter.setHorarios(actividad.getHorarios());

        nombre.setText(actividad.getNombre());
        descripcion.setText(actividad.getDescripcion());

        botonCalificarActividad = viewInflater.findViewById(R.id.bt_pasar_calificar_foro);
        botonCalificarActividad.setEnabled(false);
        Log.e(">>>", auth.getCurrentUser().getEmail());

        StorageReference storageReference = storage.getReference().child("fotos").child(actividad.getImg());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                int sCorner = 15;
                int sMargin = 2;
                int sBorder = 10;
                String sColor = "#2874A6";
                List<Transformation<Bitmap>> transforms = new LinkedList<>();
                transforms.add(new CenterCrop(getContext()));
                transforms.add(new RoundedCornersTransformation(getContext(), sCorner, sMargin, sColor, sBorder));

                MultiTransformation transformation = new MultiTransformation<Bitmap>(transforms);

                Glide.with(getActivity()).load(uri)
                        .apply(new RequestOptions()
                                .bitmapTransform(transformation))
                        .into(imagenActividad);
                botonCalificarActividad.setEnabled(true);
            }
        });
        //borrar

        botonCalificarActividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalificacionActividades fragmento = new CalificacionActividades();

                Bundle bundle = new Bundle();
                bundle.putSerializable("actividad", actividad);
                fragmento.setArguments(bundle);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.contenedorFragments, fragmento);
                transaction.commit();
            }
        });


        //

        // Inflate the layout for this fragment
        return viewInflater;
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