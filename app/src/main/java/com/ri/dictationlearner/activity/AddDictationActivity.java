package com.ri.dictationlearner.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.ri.dictationlearner.R;
import com.ri.dictationlearner.activity.db.DatabaseHelper;
import com.ri.dictationlearner.domain.Dictation;
import com.ri.dictationlearner.utils.Utils;

import static com.ri.dictationlearner.utils.Utils.resizeBitmap;

public class AddDictationActivity extends AppCompatActivity {

    private static final String TAG = "AddDictationActivity";
    private static int RESULT_LOAD_IMAGE = 1;

    private Dictation dictation;

    private EditText mEtDictionaryName;

    private ImageButton mIbSelectImage;

    private ImageView mIvDictationImage;

    private DatabaseHelper dbHelper;

    private boolean mIsEditing = false;

    private boolean mImageChanged = false;

    private String currentOperation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dictation);

        dbHelper = new DatabaseHelper(this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                dictation =  (Dictation) extras.get("DICTATION");
                currentOperation = extras.getString("OPERATION");
            }
        }else{
            currentOperation = savedInstanceState.getString("OPERATION");
            dictation = savedInstanceState.getParcelable("DICTATION");
        }

        mImageChanged = false;
        mEtDictionaryName = (EditText) findViewById(R.id.etDictionaryName);
        mIbSelectImage = (ImageButton) findViewById(R.id.ibSelectImage);
        mIvDictationImage = (ImageView) findViewById(R.id.ivDictationImage);

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

        setValues();

    }


    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(!"VIEW".equalsIgnoreCase(currentOperation)) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_add_new_item, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_save:
                Log.d("TT","Save called");
                saveData();
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_cancel:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString("OPERATION", currentOperation);
        savedInstanceState.putParcelable( "DICTATION", dictation);

        super.onSaveInstanceState(savedInstanceState);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentOperation = savedInstanceState.getString("OPERATION");
        dictation = savedInstanceState.getParcelable("DICTATION");
        setCurrentTitle();
    }

    private void setCurrentTitle(){

        if("NEW".equalsIgnoreCase(currentOperation)){
            setTitle("New Dictation");
        }else if("EDIT".equalsIgnoreCase(currentOperation)) {
            setTitle("Edit Dictation Details");
        }else{
            setTitle("View Dictation Details");
        }

    }


    private void saveData() {
        Bitmap bm= null;
        if(mImageChanged){
            bm = ((BitmapDrawable)mIvDictationImage.getDrawable()).getBitmap();
        }

        if(!mIsEditing) {
            long id = dbHelper.addDictation(mEtDictionaryName.getText().toString(), bm != null ? Utils.getBytes(bm) : null);
            Toast.makeText(this,"Dictation Added " + id,Toast.LENGTH_LONG ).show();
        }else{
            dbHelper.updateDictation(dictation.getId(),mEtDictionaryName.getText().toString(), null
                    , mImageChanged ? Utils.getBytes(bm) : null);
            Toast.makeText(this,"Dictation Updated " + dictation.getId(),Toast.LENGTH_LONG ).show();
        }

    }

    public boolean isStorageReadPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Log.d("MAIN","Image is being retrieved " + picturePath);

            mIvDictationImage.setImageBitmap(resizeBitmap(picturePath, 400, 400));
            mImageChanged = true;
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            mIbSelectImage.performClick();
        }
    }

    private void setValues() {


        if("NEW".equalsIgnoreCase(currentOperation)) {
            mIvDictationImage.setImageResource(R.drawable.ic_broken_image_white_48dp);
            mIvDictationImage.setColorFilter(R.color.colorPrimary);

            mIsEditing = false;
        }else{
            mEtDictionaryName.setText(dictation.getName());
            mIvDictationImage.setImageResource(dictation.getImageResourceId());
            if(dictation.getImage() != null) {
                mIvDictationImage.setImageBitmap(Utils.getImage(dictation.getImage()));
            }
            mIsEditing = true;

            if("VIEW".equalsIgnoreCase(currentOperation)) {
                mEtDictionaryName.setEnabled(false);
                mIbSelectImage.setEnabled(false);
            }

        }
        setCurrentTitle();
    }
}
