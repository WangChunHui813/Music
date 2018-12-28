package com.example.administrator.musicplayer;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    String tag = MainActivity.class.getSimpleName();
    private ImageView backIv;
    private ImageView nextIv;
    private int position;
    private ImageView circleIv;
    private ImageView discsmap;//图片
    private ImageView pauseIv;
    private TextView currentTime;
    private TextView totalTime;
    private SeekBar mseekBar;
    private boolean isStop;
    private ImageView needle;
    private ObjectAnimator animator1;
    private ObjectAnimator discObjectAnimator,neddleObjectAnimator;
    private SimpleDateFormat time = new SimpleDateFormat("mm:ss");
    private MediaPlayer mp;
    private int flag; //0为顺序播放，1为循环播放




    private String formatTime(int length) {
        Date date = new Date(length);
        //时间格式化工具
        String totalTime = time.format(date);
        return totalTime;
    }

    //对seekBar设置监听，方便用户在拖动进度条时能到达相应的位置，歌曲能够连贯:
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            mseekBar.setProgress(msg.what);
            currentTime.setText(formatTime(msg.what));
        }
    };


    @SuppressLint({"WrongConstant", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent=getIntent();
        mp = MediaPlayer.create(this,R.raw.bios);

        //position=((Intent) intent).getIntExtra("i",0);
        //mp=new MediaPlayer();

        flag = 1;
        Link();
        play();
        shape();

        circleIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 0){
                    circleIv.setImageResource(R.drawable.circles);
                    Log.d(tag,"点到了这");
                    mp.setLooping(false);
                    Toast.makeText(MainActivity.this,"顺序播放",Toast.LENGTH_SHORT).show();
                    flag = 1;
                } else {
                    circleIv.setImageResource(R.drawable.recycle);
                    mp.setLooping(true);
                    Toast.makeText(MainActivity.this,"单曲循环",Toast.LENGTH_SHORT).show();
                    flag = 0;

                }
            }
        });

        mseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mp.start();
                    mseekBar.setMax(mp.getDuration());
                    mseekBar.setProgress(mp.getCurrentPosition());
                    mp.seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseIv.setImageResource(R.drawable.start);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pauseIv.setImageResource(R.drawable.pause);
            }
        });
        mp = MediaPlayer.create(this,R.raw.bios);
        mseekBar.setMax(mp.getDuration());
        //mseekBar.setBackgroundColor(R.color.colorgray);
        //mseekBar.setOutlineAmbientShadowColor(R.color.colorgray);
        //mseekBar.setOutlineSpotShadowColor(R.color.colorgray);
        pauseIv.setImageResource(R.drawable.start);
        //circleIv.setImageResource(R.drawable.circles);
        //音乐总时长
        totalTime.setText(formatTime(mp.getDuration()));
        currentTime.setText(formatTime(mp.getCurrentPosition()));
    }

    @SuppressLint("WrongConstant")
    private void shape() {
        //最外部的半透明边线
        OvalShape ovalShape0 = new OvalShape();
        ShapeDrawable drawable0 = new ShapeDrawable(ovalShape0);
        drawable0.getPaint().setColor(0x10000000);
        drawable0.getPaint().setStyle(Paint.Style.FILL);
        drawable0.getPaint().setAntiAlias(true);

        //黑色唱片边框
        RoundedBitmapDrawable drawable1 = RoundedBitmapDrawableFactory.create(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.disc));
        drawable1.setCircular(true);
        drawable1.setAntiAlias(true);

        //内层黑色边线
        OvalShape ovalShape2 = new OvalShape();
        ShapeDrawable drawable2 = new ShapeDrawable(ovalShape2);
        drawable2.getPaint().setColor(Color.BLACK);
        drawable2.getPaint().setStyle(Paint.Style.FILL);
        drawable2.getPaint().setAntiAlias(true);

        //最里面的图像
        RoundedBitmapDrawable drawable3 = RoundedBitmapDrawableFactory.create(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.ddd));
        drawable3.setCircular(true);
        drawable3.setAntiAlias(true);

        Drawable[] layers = new Drawable[4];
        layers[0] = drawable0;
        layers[1] = drawable1;
        layers[2] = drawable2;
        layers[3] = drawable3;

        LayerDrawable layerDrawable = new LayerDrawable(layers);

        int width = 10;
        //针对每一个图层进行填充，使得各个圆环之间相互有间隔，否则就重合成一个了。
        //layerDrawable.setLayerInset(0, width, width, width, width);
        layerDrawable.setLayerInset(1, width , width, width, width );
        layerDrawable.setLayerInset(2, width * 11, width * 11, width * 11, width * 11);
        layerDrawable.setLayerInset(3, width * 12, width * 12, width * 12, width * 12);

        final View discView = findViewById(R.id.listen_changpian_img);
        discView.setBackgroundDrawable(layerDrawable);

        ImageView needleImage= (ImageView) findViewById(R.id.listen_zhizhen_iv);

        discObjectAnimator = ObjectAnimator.ofFloat(discView, "rotation", 0, 360);
        discObjectAnimator.setDuration(20000);
        //使ObjectAnimator动画匀速平滑旋转
        discObjectAnimator.setInterpolator(new LinearInterpolator());
        //无限循环旋转
        discObjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        discObjectAnimator.setRepeatMode(ValueAnimator.INFINITE);

        neddleObjectAnimator = ObjectAnimator.ofFloat(needleImage, "rotation", 0, 25);
        needleImage.setPivotX(0);
        needleImage.setPivotY(0);
        neddleObjectAnimator.setDuration(800);
        neddleObjectAnimator.setInterpolator(new LinearInterpolator());
    }

    private void play(){
        mp.reset();

        //Music music = Common.musicList.get(position);//获取传来的值
        // Music music=Common.musicList.get(position);
        Bitmap discs = BitmapFactory.decodeResource(getResources(), R.raw.bios);

        try {
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        backIv.setOnClickListener(this);
        nextIv.setOnClickListener(this);
        pauseIv.setOnClickListener(this);


        new Thread(new SeekBarThread()).start();
        mseekBar.setMax(position);

        animator1 = ObjectAnimator.ofFloat(needle, "rotation", -60f, 0.0f);
        animator1.setDuration(900);
        animator1.setRepeatCount(0);//设置动画重复次数（-1代表一直转）
        animator1.start();

    }

    private void Link() {
            backIv = findViewById(R.id.listen_back_img);
            nextIv = findViewById(R.id.listen_next_img);
            discsmap = findViewById(R.id.listen_changpian_img);
            pauseIv = findViewById(R.id.listen_pause1_img);
            circleIv = findViewById(R.id.circle);
            currentTime = findViewById(R.id.listen_current_tv);
            totalTime = findViewById(R.id.listen_length_tv);
            mseekBar = findViewById(R.id.listen_jindutiao_sb);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.listen_pause1_img:
                if (mp.isPlaying()){
                    mp.pause();
                    discObjectAnimator.cancel();
                    neddleObjectAnimator.reverse();
                    pauseIv.setImageResource(R.drawable.start);
                }   else {
                    mp.start();
                    discObjectAnimator.start();
                    neddleObjectAnimator.start();
                    pauseIv.setImageResource(R.drawable.pause);
                }
                    default:
                break;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        mp.reset();
        mp.release();
        mp = null;
        isStop = true;
    }

    class SeekBarThread implements Runnable {
        @Override
        public void run() {
            while (mp != null && isStop == false) {
                // 将SeekBar位置设置到当前播放位置
                handler.sendEmptyMessage(mp.getCurrentPosition());
                try {
                    // 每100毫秒更新一次位置
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

