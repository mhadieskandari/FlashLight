package com.mhs.asus.flashlight;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {
    ToggleButton tglBtnLight;
    private Camera camera;
    Parameters p;
    RadioGroup rgMode ;
    RadioButton rdbSOS,rdbLight;
    Timer timer;
    TimerTask timerTask;
    boolean flag=false;
    TextView tvsb ;
    SeekBar seekBar ;
    int interv=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tglBtnLight=(ToggleButton)findViewById(R.id.tglBtnLight);
        rgMode=(RadioGroup)findViewById(R.id.rgMode);
        rdbLight=(RadioButton)findViewById(R.id.rdbLight);
        rdbSOS=(RadioButton)findViewById(R.id.rdbSos);
        seekBar =(SeekBar)findViewById(R.id.seekBar);
        tvsb=(TextView)findViewById(R.id.tvsb);

        camera = Camera.open();
        p = camera.getParameters();

        seekBar.setProgress(0);
        tvsb.setText(String.valueOf(seekBar.getProgress()));
        seekBar.setEnabled(false);




        tglBtnLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                ChangeState(!isChecked);
                LightState(isChecked);

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvsb.setText(String.valueOf(progress));
                interv = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        rgMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == rdbLight.getId()) {
                    seekBar.setEnabled(false);
                } else if (checkedId == rdbSOS.getId()) {
                    seekBar.setEnabled(true);
                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (camera != null) {
            camera.release();
        }
    }
    
    private void ChangeState(boolean state){

        rdbLight.setEnabled(state);
        rdbSOS.setEnabled(state);
        if(rdbSOS.isChecked()&& state){
            seekBar.setEnabled(state);
        }
        else {
            seekBar.setEnabled(false);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void LightState(boolean isChecked) {

        if(isChecked){


            rgMode.setEnabled(false);
            if(rdbSOS.isChecked()){
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if(flag) {
                            p.setFlashMode(Parameters.FLASH_MODE_TORCH);
                            camera.setParameters(p);
                            camera.startPreview();
                            flag=false;
                        }else {
                            p.setFlashMode(Parameters.FLASH_MODE_OFF);
                            camera.setParameters(p);
                            camera.stopPreview();
                            flag=true;
                        }
                    }
                };
                timer = new Timer();
                if (interv!=0) {
                    timer.scheduleAtFixedRate(timerTask, 0, 1000 / interv);
                }
                else {
                    p.setFlashMode(Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(p);
                    camera.startPreview();
                }


            }
            else {
                p.setFlashMode(Parameters.FLASH_MODE_TORCH);
                camera.setParameters(p);
                camera.startPreview();
            }
        }
        else{
            rgMode.setEnabled(true);

            if(rdbSOS.isChecked()){
                timer.cancel();

                p.setFlashMode(Parameters.FLASH_MODE_OFF);
                camera.setParameters(p);
                camera.stopPreview();
            }
            else {
                p.setFlashMode(Parameters.FLASH_MODE_OFF);
                camera.setParameters(p);
                camera.stopPreview();
            }

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
