package github.arocketman.awwdroid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SingleImageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SingleImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(entry.getURL().contains("gif"))
            Glide.with(getContext()).load(entry.getURL()).asGif().crossFade().placeholder(R.drawable.load).into(imageView);
        else {
            Glide.with(getContext()).load(entry.getURL()).crossFade().placeholder(R.drawable.load).into(imageView);
        }
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
            Drawable bitmapDrawable = imageView.getDrawable();
            Bitmap bitmap = BitmapFactory.decode
            Bitmap icon = bitmap;
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
            try {
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
            startActivity(Intent.createChooser(share, "Share Image"));
            return true;
        }
    }


}
