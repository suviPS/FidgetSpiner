package tk.ksfdev.httpwww.fidgetspiner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Random;

import static tk.ksfdev.httpwww.fidgetspiner.MyUtils.spinerCount;
import static tk.ksfdev.httpwww.fidgetspiner.MyUtils.spinerIDs;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView)findViewById(R.id.imageView1);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setUpPreferences();
    }


    private float x1,x2, y1, y2;
    static final int MIN_DISTANCE = 50;
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = transformX(event.getX());
                y1 = transformY(event.getY());
                break;
            case MotionEvent.ACTION_UP:
                x2 = transformX(event.getX());
                y2 = transformY(event.getY());
                float deltaX = Math.abs(x2 - x1);
                float deltaY = Math.abs(y2 - y1);

                if(deltaX > MIN_DISTANCE || deltaY > MIN_DISTANCE){
                    determinRotation();
                }
                break;
        }
        return super.onTouchEvent(event);
    }


    //transform to system with (0,0) in center of screen
    private float screenCenterX = -1;
    private float screenCenterY = -1;
    private void getCenter(){
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        screenCenterY = size.y/2;
        screenCenterX = size.x/2;
    }

    private float transformX(float myX){
        if(screenCenterX == -1 || screenCenterY == -1)
            getCenter();
        if(myX < screenCenterX)
            return -(screenCenterX - myX);
        else
            return myX - screenCenterX;
    }

    private float transformY(float myY){
        if(screenCenterX == -1 || screenCenterY == -1)
            getCenter();
        if(myY < screenCenterY)
            return screenCenterY - myY;
        else
            return -(myY - screenCenterY);
    }

    //determin location of dot
    private int getQuadrant(float x1, float y1){
        if(x1 >= 0 && y1 >= 0)
            return 1;
        if(x1 <= 0 && y1 >= 0)
            return 2;
        if(x1 <= 0 && y1 <= 0)
            return 3;
        if(x1 >= 0 && y1 <= 0)
            return 4;
        else{
            Toast.makeText(this, "Error in getQuadrant()", Toast.LENGTH_SHORT).show();
            return -1;
        }
    }



    //determin rotation based on swipe and quadrant
    private boolean bigX1 = true;
    private boolean bigY1 = true;
    private void determinRotation(){

        if(x1 < x2)
            bigX1 = false;
        else
            bigX1 = true;
        if(y1 < y2)
            bigY1 = false;
        else
            bigY1 = true;

        switch (getQuadrant(x1, y1)){
            case 1:
                if(  (!bigX1) && (bigY1) )
                    rotateRight();
                else
                    rotateLeft();
                break;
            case 2:
                //quadrant 2
                if(  (!bigX1) && (!bigY1) )
                    rotateRight();
                else
                    rotateLeft();
                break;
            case 3:
                //quadrant 3
                if(  (bigX1) && (!bigY1) )
                    rotateRight();
                else
                    rotateLeft();
                break;
            case 4:
                //quadrant 4
                if(  (bigX1) && (bigY1) )
                    rotateRight();
                else
                    rotateLeft();
                break;

        }
        Log.d("TAG++", "determinRotation() - done");
    }


    // increment speed and number of rotations
    private static void speedUp(int i){
        switch (i){
            case 1:
                if(durationRight > 160)
                    durationRight-=50;
                repeatRight+=2;
                break;
            case -1:
                if(durationLeft > 160)
                    durationLeft-=50;
                repeatLeft+=2;
                break;
        }
    }

    //reset animation changes
    private static void resetSpeed(int i){
        switch (i){
            case 1:
                //reset -1
                repeatLeft = 3;
                durationLeft = 400;
                break;
            case -1:
                repeatRight = 3;
                durationRight = 400;
                break;
        }
    }


    //do rotation
    private static int lastRotation = 0;
    private static LinearInterpolator li = new LinearInterpolator();

    private static RotateAnimation rotateRight = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    private static int durationRight = 400;
    private static int repeatRight = 3;
    private void rotateRight(){
        if(lastRotation == 1)
            speedUp(1);
        else
            resetSpeed(1);
        rotateRight.setDuration(durationRight);
        rotateRight.setInterpolator(li);
        rotateRight.setRepeatCount(repeatRight);
        imageView.clearAnimation();
        imageView.startAnimation(rotateRight);
        lastRotation = 1;

    }

    private static RotateAnimation rotateLeft = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    private static int durationLeft = 400;
    private static int repeatLeft = 3;
    private void rotateLeft(){
        if(lastRotation == -1)
            speedUp(-1);
        else
            resetSpeed(-1);
        rotateLeft.setInterpolator(li);
        rotateLeft.setDuration(durationLeft);
        rotateLeft.setRepeatCount(repeatLeft);
        imageView.clearAnimation();
        imageView.startAnimation(rotateLeft);
        lastRotation = -1;
    }



    //deafult spiner
    private void setUpPreferences(){
        int imageId = PreferenceManager.getDefaultSharedPreferences(this).getInt(MyUtils.PREF_IMAGE_ID, R.drawable.fs01);
        imageView.setImageResource(imageId);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        setUpPreferences();
    }



    //button methods
    public void lockSpiner(View v){
        if(imageView.getAnimation() == null)
            return;
        imageView.getAnimation().setRepeatCount(180);
        imageView.animate();
        Toast.makeText(this, "Spiner Locked in current state", Toast.LENGTH_SHORT).show();
    }

    public void goToSettings(View v){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void randomSpiner(View v){
        Random r = new Random(Calendar.getInstance().getTimeInMillis());
        // r.nextInt(3) -> {0, 1, 2}

        imageView.setImageResource(spinerIDs[r.nextInt(spinerCount)]);
    }
}
