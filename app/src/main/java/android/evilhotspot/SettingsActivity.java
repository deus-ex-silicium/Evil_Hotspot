package android.evilhotspot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    private Uri imageCaptureUri;
    private ImageView mImageView;
    Button button_choose_img;
    private static final int PICK_FROM_CAMERA=1;
    private static final int PICK_FROM_FILE=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Evil Hotspot");
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true); //creating home button for going up to main activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String[] items=new String[] {"From Camere","From SD Card"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
        public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file=new File(Environment.getExternalStorageDirectory(),"tmp_avatar" + String.valueOf(System.currentTimeMillis())+ ".jpg");
                    imageCaptureUri=Uri.fromFile(file);
                    try{
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageCaptureUri);
                        intent.putExtra("return data", true);

                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                    dialog.cancel();
                }else{
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);

                }
            }
        });
        final AlertDialog dialog = builder.create();
        mImageView = (ImageView) findViewById(R.id.image);
        button_choose_img= (Button) findViewById(R.id.pick);
        button_choose_img.setOnClickListener(new View.OnClickListener(){
            @Override
        public void onClick(View v) {
                dialog.show();
            }

        });


        final EditText myEdit = (EditText) findViewById(R.id.editssid);//part for checking number of chars-NOT WORKING
        myEdit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (myEdit.getText().length() < 4) {
                        Toast.makeText(SettingsActivity.this, "Not enough characters", Toast.LENGTH_SHORT);
                    } else {
                        Toast.makeText(SettingsActivity.this, "Ok characters", Toast.LENGTH_SHORT);
                    }
                    return true;
                }
                return false;
            }
        });

    }

    public String getRealPathFromURI(Uri contentURI){
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentURI, proj, null, null, null);
        if(cursor==null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        return cursor.getString(column_index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        Bitmap bitmap = null;
        String path="";
        if (requestCode == PICK_FROM_FILE){
            imageCaptureUri = data.getData();
            path=getRealPathFromURI(imageCaptureUri);
            if(path==null)
                path=imageCaptureUri.getPath();
            if(path!=null)
                bitmap = BitmapFactory.decodeFile(path);
        }else{
            path=imageCaptureUri.getPath();
            bitmap=BitmapFactory.decodeFile(path);
        }
        mImageView.setImageBitmap(bitmap);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) { //going back from settings
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
