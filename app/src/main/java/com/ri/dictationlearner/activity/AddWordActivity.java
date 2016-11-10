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
import com.ri.dictationlearner.domain.Word;
import com.ri.dictationlearner.utils.ImageUtils;

import static com.ri.dictationlearner.utils.ImageUtils.resizeBitmap;

public class AddWordActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AddWordActivity";
    private static int RESULT_LOAD_IMAGE = 1;
    private Word word;
    private EditText mEtWord;
    private ImageButton mIbSelectImage;
    private ImageView mIvWordImage;
    private DatabaseHelper dbHelper;
    private boolean mImageChanged = false;
    private boolean mIsEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                word =  (Word) extras.get("WORD");
            }
        }

        mImageChanged = false;

        Log.d("TT", "Received word "  + word);

        dbHelper = new DatabaseHelper(this);

        mEtWord = (EditText) findViewById(R.id.etNewWord);
        mIbSelectImage = (ImageButton) findViewById(R.id.ibSelectNewWordImage);
        mIvWordImage = (ImageView) findViewById(R.id.ivNewWordImage);

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_save_cancel_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_save:
                Log.d("TT","Save called");
                saveData();
                if(mIsEditing) {
                    NavUtils.navigateUpFromSameTask(this);
                }else{
                    clearFields();
                }
                return true;
            case R.id.action_cancel:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void saveData() {

        Bitmap bm= null;
        if(mImageChanged){
            bm = ((BitmapDrawable)mIvWordImage.getDrawable()).getBitmap();
        }

        if(!mIsEditing) {
            long id = dbHelper.addWord(word.getDictationId(),  mEtWord.getText().toString(), word.getSerialNo(), bm != null ? ImageUtils.getBytes(bm) : null);
            Toast.makeText(this,"added word successfully",Toast.LENGTH_LONG ).show();
        }else{
            boolean updateFlag = dbHelper.updateWord(word.getDictationId(), word.getWordId(),mEtWord.getText().toString(), null
                    , mImageChanged ? ImageUtils.getBytes(bm) : null);
            Toast.makeText(this,"updated word successfully",Toast.LENGTH_LONG ).show();
        }
    }



    public boolean isStorageReadPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(LOG_TAG, "Permission is granted");
                return true;
            } else {
                Log.v(LOG_TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(LOG_TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Log.d("MAIN", "Image is being retrieved " + picturePath);

            mIvWordImage.setImageBitmap(resizeBitmap(picturePath, 400, 400));
            mImageChanged = true;
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

    private void setValues() {
        if (word != null && word.getWord() !=null ) {
            mEtWord.setText(word.getWord());
            mIvWordImage.setImageResource(word.getImageResourceId());
            mIvWordImage.setColorFilter(R.color.colorPrimary);
            setTitle("Edit Word");
            if(word.getImage() != null) {
                mIvWordImage.setImageBitmap(ImageUtils.getImage(word.getImage()));
            }
            mIsEditing = true;

        } else {
            mIvWordImage.setImageResource(R.drawable.ic_broken_image_white_48dp);
            setTitle("New Word");
            mIsEditing = false;
        }
    }

    private void clearFields() {
        mEtWord.setText("");
        mIvWordImage.setImageResource(R.drawable.ic_broken_image_white_48dp);
        mIvWordImage.setColorFilter(R.color.colorPrimary);
        mImageChanged = false;
     }


}