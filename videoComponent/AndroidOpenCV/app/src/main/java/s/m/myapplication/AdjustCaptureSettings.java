package s.m.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class AdjustCaptureSettings extends AppCompatActivity {
    private ImageView picture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("HERE","I am in the Adjust");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_capture_settings);
        picture = (ImageView)findViewById(R.id.pictureFromCamera);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            byte[] value = extras.getByteArray("PICTURE");
            Log.d("HERE","I am in the Adjust " + value.length);
          //  Bitmap bitmap = BitmapFactory.decodeByteArray(value, 0, value.length);
            //picture.setImageBitmap(bitmap);
           // Bitmap imageBitmap = (Bitmap) value;
            //The key argument here must match that used in the other activity
        }
    }
}
