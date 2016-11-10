package com.ri.dictationlearner.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.ri.dictationlearner.R;
import com.ri.dictationlearner.activity.db.DatabaseHelper;
import com.ri.dictationlearner.domain.Dictation;
import com.ri.dictationlearner.utils.ImageUtils;

import static android.app.Activity.RESULT_OK;
import static com.ri.dictationlearner.utils.ImageUtils.resizeBitmap;


public class DictationDetailFragment extends Fragment {

    private static final String LOG_TAG = "DictationDetailFragment";

    public static final String ARG_DICTATION  = "mDictation";
    public static final String ARG_CUR_OPERATION = "operation";


    private Dictation mDictation;
    private EditText mEtDictionaryName;
    private ImageButton mIbSelectImage;
    private ImageView mIvDictationImage;
    private DatabaseHelper dbHelper;
    private boolean mIsEditing = false;
    private boolean mImageChanged = false;
    private String mCurrentOperation = null;
    private static int RESULT_LOAD_IMAGE = 1;

    public DictationDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DatabaseHelper(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dictation_detail, container, false);

        mImageChanged = false;
        mEtDictionaryName = (EditText) rootView.findViewById(R.id.etDictionaryName);
        mIbSelectImage = (ImageButton) rootView.findViewById(R.id.ibSelectImage);
        mIvDictationImage = (ImageView) rootView.findViewById(R.id.ivDictationImage);

        mIbSelectImage.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View view) {

                                                  if (!isStorageReadPermissionGranted())
                                                      return;

                                                  Intent i = new Intent(
                                                          Intent.ACTION_PICK,
                                                          android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                                                  startActivityForResult(i, RESULT_LOAD_IMAGE);
                                              }
                                          }
        );

        if (getArguments().containsKey(ARG_DICTATION)) {

            mDictation = (Dictation) getArguments().get(ARG_DICTATION);
            mCurrentOperation =   getArguments().getString(ARG_CUR_OPERATION);

            Activity activity = this.getActivity();

            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(getTitle());
            }
        }
        setValues();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentOperation = savedInstanceState.getString("OPERATION");
            mDictation = savedInstanceState.getParcelable("DICTATION");
        }
    }


    private String getTitle(){

        if("NEW".equalsIgnoreCase(mCurrentOperation)){
            return "New Dictation";
        }else if("EDIT".equalsIgnoreCase(mCurrentOperation)) {
            return  "Edit Dictation Details" ;
        }else{
            return "View Dictation Details" ;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_save:
                saveData();
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            case R.id.action_cancel:
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString("OPERATION", mCurrentOperation);
        savedInstanceState.putParcelable( "DICTATION", mDictation);

        super.onSaveInstanceState(savedInstanceState);

    }

    public boolean isStorageReadPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(LOG_TAG, "Permission is granted");
                return true;
            } else {
                Log.v(LOG_TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(LOG_TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Log.d(LOG_TAG,"Image is being retrieved " + picturePath);

            mIvDictationImage.setImageBitmap(resizeBitmap(picturePath, 400, 400));
            mImageChanged = true;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(!"VIEW".equalsIgnoreCase(mCurrentOperation)) {
            getActivity().getMenuInflater().inflate(R.menu.menu_save_cancel_item, menu);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(LOG_TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            mIbSelectImage.performClick();
        }
    }

    private void saveData() {
        Bitmap bm= null;
        if(mImageChanged){
            bm = ((BitmapDrawable)mIvDictationImage.getDrawable()).getBitmap();
        }

        if(!mIsEditing) {
            long id = dbHelper.addDictation(mEtDictionaryName.getText().toString(), bm != null ? ImageUtils.getBytes(bm) : null);
            Toast.makeText(getActivity() ,"Dictation Added",Toast.LENGTH_LONG ).show();
        }else{
            dbHelper.updateDictation(mDictation.getId(),mEtDictionaryName.getText().toString(), null
                    , mImageChanged ? ImageUtils.getBytes(bm) : null);
            Toast.makeText(getActivity(),"Dictation Updated" ,Toast.LENGTH_LONG ).show();
        }

    }

    private void setValues() {

        Log.d(LOG_TAG,"Current operation is " + mCurrentOperation);

        if("NEW".equalsIgnoreCase(mCurrentOperation)) {
            mIvDictationImage.setImageResource(R.drawable.ic_broken_image_black_48dp);
//            mIvDictationImage.setColorFilter(R.color.colorPrimary);

            mIsEditing = false;
        }else{
            mEtDictionaryName.setText(mDictation.getName());
            mIvDictationImage.setImageResource(mDictation.getImageResourceId());
            if(mDictation.getImage() != null) {
                mIvDictationImage.setImageBitmap(ImageUtils.getImage(mDictation.getImage()));
            }
            mIsEditing = true;

            if("VIEW".equalsIgnoreCase(mCurrentOperation)) {
                mEtDictionaryName.setEnabled(false);
                mIbSelectImage.setEnabled(false);
            }

        }

    }
}
