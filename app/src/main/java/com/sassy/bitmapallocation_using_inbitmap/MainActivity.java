package com.sassy.bitmapallocation_using_inbitmap;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    int mCurrentIndex = 0;
    Bitmap mCurrentBitmap = null;
    BitmapFactory.Options mBitmapOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int[] imageIDs = {
                R.drawable.test1,
                R.drawable.test2,
                R.drawable.test3,
                R.drawable.test4,
                R.drawable.test5    };

        final CheckBox checkbox = (CheckBox) findViewById(R.id.checkbox);
        final TextView durationTextview = (TextView) findViewById(R.id.loadDuration);
        final ImageView imageview = (ImageView) findViewById(R.id.imageview);

        // Create bitmap to be re-used, based on the size of one of the bitmaps
        mBitmapOptions = new BitmapFactory.Options();
        mBitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.test1, mBitmapOptions);
        mCurrentBitmap = Bitmap.createBitmap(mBitmapOptions.outWidth,
                mBitmapOptions.outHeight, Bitmap.Config.ARGB_8888);
        mBitmapOptions.inJustDecodeBounds = false;
        mBitmapOptions.inBitmap = mCurrentBitmap;

        //for java.lang.IllegalArgumentException: Problem decoding into existing bitmap - use following
        mBitmapOptions.inDensity = Bitmap.DENSITY_NONE;
        mBitmapOptions.inScaled = false;
        //for java.lang.IllegalArgumentException: Problem decoding into existing bitmap - use above

        mBitmapOptions.inSampleSize = 1;
        BitmapFactory.decodeResource(getResources(), R.drawable.test1, mBitmapOptions);
        imageview.setImageBitmap(mCurrentBitmap);

        // When the user clicks on the image, load the next one in the list
        imageview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % imageIDs.length;
                BitmapFactory.Options bitmapOptions = null;
                if (checkbox.isChecked()) {
                    // Re-use the bitmap by using BitmapOptions.inBitmap
                    bitmapOptions = mBitmapOptions;
                    bitmapOptions.inBitmap = mCurrentBitmap;
                }
                long startTime = System.currentTimeMillis();
                mCurrentBitmap = BitmapFactory.decodeResource(getResources(),
                        imageIDs[mCurrentIndex], bitmapOptions);
                imageview.setImageBitmap(mCurrentBitmap);

                // One way you can see the difference between reusing and not is through the
                // timing reported here. But you can also see a huge impact in the garbage
                // collector if you look at logcat with and without reuse. Avoiding garbage
                // collection when possible, especially for large items like bitmaps,
                // is always a good idea.
                durationTextview.setText("Load took " +
                        (System.currentTimeMillis() - startTime));
            }
        });
    }

}
