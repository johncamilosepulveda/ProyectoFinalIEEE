package com.example.proyectofinalieee.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.proyectofinalieee.R;
import com.example.proyectofinalieee.model.Foro;
import com.example.proyectofinalieee.utilities.RoundedCornersTransformation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ForoAdapter extends BaseAdapter {


    private ArrayList<Foro> foros;
    private Context context;

    private FirebaseStorage storage;


    public ForoAdapter(Context context) {

        this.context = context;
        foros = new ArrayList<Foro>();
    }


    public ForoAdapter() {
        super();
    }

    @Override
    public int getCount() {
        return foros.size();
    }

    @Override
    public Foro getItem(int i) {
        return foros.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View renglonActividad = inflater.inflate(R.layout.renglon_foro, null);
        Foro actividad = foros.get(i);


        TextView tv_valoracion = renglonActividad.findViewById(R.id.tv_valoracion);
        TextView tv_titulo = renglonActividad.findViewById(R.id.tv_titulo);
        final ImageView iv_actividad = renglonActividad.findViewById(R.id.iv_foro);

        storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("fotos").child(actividad.getImg());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                int sCorner = 15;
                int sMargin = 2;
                int sBorder = 10;
                String sColor = "#2874A6";
                List<Transformation<Bitmap>> transforms = new LinkedList<>();
                transforms.add(new CenterCrop(context));
                transforms.add(new RoundedCornersTransformation(context, sCorner, sMargin, sColor, sBorder));

                MultiTransformation transformation = new MultiTransformation<Bitmap>(transforms);

                Glide.with(context).load(uri)
                        .apply(new RequestOptions()
                                .bitmapTransform(transformation))
                        .into(iv_actividad);
            }
        });

        tv_valoracion.setText(actividad.getLugar());
        tv_titulo.setText(actividad.getNombre());


        return renglonActividad;
    }

    public void addActividad(Foro actividad) {
        foros.add(actividad);
        notifyDataSetChanged();
    }
}
