package s.m.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class AdjustCaptureSettings extends AppCompatActivity {
    private LinearLayout linearLayout;
    private ImageView picture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("HERE","I am in the Adjust");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_capture_settings);
        picture = (ImageView)findViewById(R.id.pictureFromCamera);
        Bundle extras = getIntent().getExtras();
        //picture.setImageBitmap(new Bitmap());

        if (extras != null) {
            byte[] value = extras.getByteArray("PICTURE");
            Log.d("HERE","I am in the Adjust ");
          //Bitmap bitmap = BitmapFactory.decodeByteArray(value, 0, value.length);
          //picture.setImageBitmap(bitmap);
           // Bitmap imageBitmap = (Bitmap) value;
            //The key argument here must match that used in the other activity
        }
    }

    private void HSR(){
        linearLayout = findViewById(R.id.linearLayoutAdjust);
        ImageView hsr = new ImageView(this);
        hsr.setImageResource(R.mipmap.anotherpool);
        hsr.setLayoutParams(linearLayout.getLayoutParams());
        linearLayout.addView(hsr);

        Mat mat;


    }
}
