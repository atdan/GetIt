package com.example.eets_nostredame.getit;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.example.eets_nostredame.getit.interfaces.OnPhotoSelectedListener;
import com.example.eets_nostredame.getit.model.Post;
import com.example.eets_nostredame.getit.util.RotateBitmap;
import com.example.eets_nostredame.getit.util.UniversalImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

//
///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link PostFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link PostFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class PostFragment extends Fragment implements OnPhotoSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "PostFragment";

    @Override
    public void getImagePath(Uri imagePath) {
        Log.d(TAG, "getImagePath: setting the image view");
        UniversalImageLoader.setImage(imagePath.toString(),postImage);
        //TODO: assign to global variables
        mSelectedBitmap = null;
        mSelectedUri = imagePath;
    }

    @Override
    public void getImageBitmap(Bitmap bitmap) {
        Log.d(TAG, "getImageBitmap: setting the image to imageview");
        postImage.setImageBitmap(bitmap);
        //TODO: assign to a global variable
        mSelectedUri = null;
        mSelectedBitmap = bitmap;
    }
    // widgets
    private ImageView postImage;
    private EditText mTitle, mDescription, mPrice, mPhone, mState, mContactEmail, mCity;
    private Button postBtn;
    private ProgressBar mProgressbar;

    //vars
    private Bitmap mSelectedBitmap;
    private Uri mSelectedUri;
    private byte[] mUploadByte;
    private double mUploadProgress = 0;

//    private OnFragmentInteractionListener mListener;

//    public PostFragment() {
//        // Required empty public constructor
//    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostFragment newInstance(String param1, String param2) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        mTitle = (EditText)view.findViewById(R.id.input_title);
        mContactEmail = view.findViewById(R.id.input_email);
        mDescription = view.findViewById(R.id.input_description);
        mPhone = view.findViewById(R.id.input_phone_number);
        mPrice = view.findViewById(R.id.input_price);
        mProgressbar = view.findViewById(R.id.progressBar);
        mState = view.findViewById(R.id.input_state_province);
        postImage= view.findViewById(R.id.post_image);

        mCity = view.findViewById(R.id.input_city);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        init();

        return view;
    }



    public void init(){
        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: opening dialog to choose photo");
                SelectPhotoDialog dialog = new SelectPhotoDialog();
                dialog.show(getFragmentManager(),getString(R.string.dialog_select_photo));
                dialog.setTargetFragment(PostFragment.this,1);
            }
        });
        try {
            postBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: attempting to post...");
                    if (!isEmpty(mTitle.getText().toString()) && !isEmpty(mDescription.getText().toString())
                            && !isEmpty(mPrice.getText().toString()) && !isEmpty(mPrice.getText().toString()
                    ) && !isEmpty(mPhone.getText().toString()) && !isEmpty(mState.getText().toString())
                            && !isEmpty(mContactEmail.getText().toString())){

                        //TODO: if we have a bitmap and no URI
                        if (mSelectedBitmap != null && mSelectedUri == null){
                            uploadNewPhoto(mSelectedBitmap);
                        }//if we have no bitmap and a URI
                        else if (mSelectedUri != null && mSelectedBitmap == null){
                            uploadNewPhoto(mSelectedUri);
                        }else {
                            Toast.makeText(getActivity(),"please insert a photo",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getActivity(),"You must fill out all the fields",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }catch (NullPointerException e){
            Log.d(TAG, "init: post button exception " + e);
        }

    }

    private void uploadNewPhoto(Bitmap mSelectedBitmap) {
        Log.d(TAG, "uploadNewPhoto: Uploading a new image bitmap to storage");
        BackgroundImageResize resize = new BackgroundImageResize(mSelectedBitmap);
        Uri uri = null;
        resize.execute(uri);
    }
    private void uploadNewPhoto(Uri imagePath){
        Log.d(TAG, "uploadNewPhoto: uploading a new image Uri to storage");
        BackgroundImageResize resize = new BackgroundImageResize( null);
        resize.execute(imagePath);
    }
    public class BackgroundImageResize extends AsyncTask<Uri,Integer,byte[]> {
        Bitmap mBitmap;
        public BackgroundImageResize(Bitmap bitmap){
            if (bitmap != null){
                this.mBitmap = bitmap;
            }
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getActivity(),"Compressing image", Toast.LENGTH_SHORT).show();
            showProgressBar();
        }
        @Override
        protected byte[] doInBackground(Uri... uris) {
            Log.d(TAG, "doInBackground: started");

            if (mBitmap == null){
                try {
                    RotateBitmap rotateBitmap = new RotateBitmap();
                    //TODO: convert the uri to a bitmap
                    mBitmap = rotateBitmap.HandleSamplingAndRotationBimap(getActivity(),uris[0]);
                }catch (IOException e){
                    Log.e(TAG, "doInBackground: IOException "+ e.getMessage() );
                }
            }
            byte[] bytes = null;
            Log.d(TAG, "doInBackground: Size in megabytes before compression is " + mBitmap.getByteCount()/1000000);
            bytes = getBytesFromBitmap(mBitmap,100);
            Log.d(TAG, "doInBackground: Size in megabytes after compression is " + bytes.length/1000000);
            return bytes;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            mUploadByte = bytes;
            hideProgressBar();
            //TODO: execute the upload task
            executeUploadTask();
        }
    }
    private void executeUploadTask(){
        Log.d(TAG, "executeUploadTask: started");
        Toast.makeText(getActivity(),"Uploading image",Toast.LENGTH_SHORT).show();

        final String postId = FirebaseDatabase.getInstance().getReference().push().getKey();

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("posts/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() +
                        "/" +postId + "/post_image");

        UploadTask uploadTask = storageReference.putBytes(mUploadByte);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: started");
                Toast.makeText(getActivity(), "Upload Successful", Toast.LENGTH_SHORT).show();

                //TODO: get the path as the pointer to where the image is stored in the storage
                //insert the download url inside the firebase database
                Uri firebaseUri = taskSnapshot.getDownloadUrl();

                Log.d(TAG, "onSuccess: firebase download url "+ firebaseUri.toString());
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                Post post = new Post();
                post.setImage(firebaseUri.toString());
                post.setContact_email(mContactEmail.getText().toString());
                post.setDescription(mDescription.getText().toString());
                post.setPost_id(postId);
                post.setPhoneNumber(mPhone.getText().toString());
                post.setTitle(mTitle.getText().toString());
                post.setState(mState.getText().toString());
                post.setCity(mCity.getText().toString());
                post.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

                reference.child(getString(R.string.node_post))
                        .child(postId)
                        .setValue(post);

                resetFields();
                Log.d(TAG, "onSuccess: Ends");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "could not upload photo",Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double currentProgress = (100 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                if (currentProgress > (mUploadProgress + 15)){
                    mUploadProgress = (100 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    Log.d(TAG, "onProgress: Upload is " + mUploadProgress + "% done");
                    Toast.makeText(getActivity(), mUploadProgress +"%", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,quality,stream);
        return stream.toByteArray();
    }


    //TODO: create a class that resizes the image in th background


    private void resetFields(){
        UniversalImageLoader.setImage("",postImage);
        mTitle.setText("");
        mDescription.setText("");
        mCity.setText("");
        mPrice.setText("");
        mPhone.setText("");
        mState.setText("");
        mContactEmail.setText("");
    }

    public void showProgressBar(){
        mProgressbar.setVisibility(View.VISIBLE);
    }
    public void hideProgressBar(){
        if (mProgressbar.getVisibility()== View.VISIBLE)
        mProgressbar.setVisibility(View.INVISIBLE);
    }
    private boolean isEmpty(String s){
        return s.equals("");
    }



//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }




    
}
