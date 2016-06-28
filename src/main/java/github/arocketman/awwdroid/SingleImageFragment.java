package github.arocketman.awwdroid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SingleImageFragment extends Fragment {

    private static final String ARG_PARAM_ENTRY = "IMAGE_ENTRY";
    private static final String ARG_PARAM_FRAG_ID = "FRAGMENT_ID";

    private ImageEntry entry;
    private int fragmentId;
    private ImageView imageView;
    private TextView imageTitleTextView;
    private ImageButton mShareButton;

    private OnFragmentInteractionListener mListener;

    public SingleImageFragment() {
        // Required empty public constructor
    }

    /**
     * Static factory method that creates a fragment instance with the given parameters.
     * @param entry the ImageEntry to be shown on the fragment.
     * @param fragId the Fragment id.
     * @return
     */
    public static SingleImageFragment newInstance(ImageEntry entry, int fragId) {
        SingleImageFragment fragment = new SingleImageFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_ENTRY,entry);
        args.putInt(ARG_PARAM_FRAG_ID,fragId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageView = (ImageView) getView().findViewById(R.id.imageView);
        imageTitleTextView = (TextView)getView().findViewById(R.id.textView);
        mShareButton = (ImageButton)getView().findViewById(R.id.shareButton);
        mShareButton.setOnTouchListener(new shareListener());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            entry = (ImageEntry) getArguments().getSerializable(ARG_PARAM_ENTRY);
            fragmentId = getArguments().getInt(ARG_PARAM_FRAG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top_hits, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(entry.getURL().contains("gif")) {
            Glide.with(getContext()).load(entry.getURL()).asGif().crossFade().placeholder(R.drawable.load).into(imageView);
            mShareButton.setActivated(false);
        }
        else
            Glide.with(getContext()).load(entry.getURL()).asBitmap().placeholder(R.drawable.load).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    imageView.setImageBitmap(resource);
                }
            });
        imageTitleTextView.setText(entry.getTitle());
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private class shareListener implements OnTouchListener{

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            //Getting the bitmap representation of the imageView, saving it to file and opening the
            //share intent.
            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            saveToFile(bitmap);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");
            share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file4.jpg"));
            startActivity(Intent.createChooser(share, "Share Image"));
            return true;
        }
    }

    /**
     * Saves the given bitmap to a file.
     * @param bitmap bitmap to save.
     * @return the saved file.
     */
    private File saveToFile(Bitmap bitmap){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file4.jpg");
        try {
            if(f.exists())
                f.delete();
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }


}
