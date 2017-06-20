package net.kotlinandroid.customprogress;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.kotlinandroid.customlibrary.view.ArcProgress;
import net.kotlinandroid.customlibrary.view.LineProgressBar;
import net.kotlinandroid.customlibrary.view.OnTextCenter;
import net.kotlinandroid.customlibrary.view.SectorProgress;
import net.kotlinandroid.customlibrary.view.onImageCenter;

public class MainActivity extends AppCompatActivity {

    private LineProgressBar mProgressBar ;
    private LineProgressBar mProgressBar2 ;

    ArcProgress mProgress,mProgress1,mProgress02,mProgress03;

    private int count;
    private SectorProgress mView1;
    private SectorProgress mView2;

    private LinearLayout ll_line,ll_arc,ll_sector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ll_line= (LinearLayout) findViewById(R.id.ll_line);
        ll_arc= (LinearLayout) findViewById(R.id.ll_arc);
        ll_sector= (LinearLayout) findViewById(R.id.ll_sector);

        mProgressBar = (LineProgressBar) findViewById(R.id.line_progresbar);
        mProgressBar2 = (LineProgressBar) findViewById(R.id.line_progresbar2);

        mProgress = (ArcProgress) findViewById(R.id.myProgress);
        mProgress1 = (ArcProgress) findViewById(R.id.myProgress01);
        mProgress02 = (ArcProgress) findViewById(R.id.myProgress02);
        mProgress03 = (ArcProgress) findViewById(R.id.myProgress03);


        mView1 = (SectorProgress) findViewById(R.id.sectorProgress);
        mView2 = (SectorProgress) findViewById(R.id.sectorProgress1);

        showLine();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_line) {
            Toast.makeText(MainActivity.this,"直线型",Toast.LENGTH_SHORT).show();

            showLine();

            return true;
        }else if(id == R.id.action_arc) {
            Toast.makeText(MainActivity.this,"圆形",Toast.LENGTH_SHORT).show();

            showArc();
            return true;
        }else if(id == R.id.action_sector) {
            Toast.makeText(MainActivity.this,"扇形",Toast.LENGTH_SHORT).show();

            showSector();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //扇形
    private void showSector() {
        ll_arc.setVisibility(View.GONE);
        ll_line.setVisibility(View.GONE);
        ll_sector.setVisibility(View.VISIBLE);
        mView1.setMinMaxValue(20, 200);
        int[] color = new int[3];
        color[0] = Color.GREEN;
        color[1] = Color.YELLOW;
        color[2] = Color.RED;
        mView1.setShaderColor(color);
        mView1.setProgress(100);

        mView2.setIsDrawRestart(true);
        mView2.setAnimateDuration(2000);
        mView2.setPercent(0.6f);

        int num = (int) (20 + Math.random() * 180);
        mView1.setProgress(num);
        mView2.setPercent(num / 200f);

    }

    //圆弧形状
    private void showArc() {
        ll_arc.setVisibility(View.VISIBLE);
        ll_line.setVisibility(View.GONE);
        ll_sector.setVisibility(View.GONE);
        mProgress.setOnCenterDraw(new OnTextCenter());
        mProgress1.setOnCenterDraw(new OnTextCenter());
        mProgress02.setOnCenterDraw(new ArcProgress.OnCenterDraw() {
            @Override
            public void draw(Canvas canvas, RectF rectF, float x, float y, float storkeWidth, int progress) {
                Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                textPaint.setStrokeWidth(35);
                textPaint.setColor(getResources().getColor(R.color.textColor));
                String progressStr = String.valueOf(progress+"%");
                float textX = x-(textPaint.measureText(progressStr)/2);
                float textY = y-((textPaint.descent()+textPaint.ascent())/2);
                canvas.drawText(progressStr,textX,textY,textPaint);
            }
        });
        mProgress03.setOnCenterDraw(new onImageCenter(this,R.mipmap.git));
        addProrgress(mProgress);
        addProrgress(mProgress1);
        addProrgress(mProgress02);
        addProrgress(mProgress03);
    }

    //直线形状
    private void showLine() {
        mProgressBar.setCurProgress(0);
        ll_line.setVisibility(View.VISIBLE);
        ll_arc.setVisibility(View.GONE);
        ll_sector.setVisibility(View.GONE);

        mProgressBar.setOnFinishedListener(new LineProgressBar.OnFinishedListener() {
            @Override
            public void onFinish() {
                if (count == 0) {
                    Toast.makeText(MainActivity.this, "下载完成!", Toast.LENGTH_SHORT).show();
                }
                count++;
            }

        });
        mProgressBar.setProgressDesc("剩余");
        mProgressBar.setMaxProgress(50);
        mProgressBar.setProgressColor(Color.parseColor("#785447"));
        mProgressBar.setCurProgress(50);



        mProgressBar2.setOnAnimationEndListener(new LineProgressBar.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                Toast.makeText(MainActivity.this,"animation end!",Toast.LENGTH_SHORT).show();
            }
        });
        mProgressBar2.setProgressDesc("剩余");
        mProgressBar2.setMaxProgress(100);
        mProgressBar2.setProgressColor(Color.parseColor("#e2c4b9"));
        mProgressBar2.setCurProgress(80,4000);

    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArcProgress progressBar = (ArcProgress) msg.obj;
            progressBar.setProgress(msg.what);
        }
    };

    public void addProrgress(ArcProgress progressBar) {
        Thread thread = new Thread(new ProgressThread(progressBar));
        thread.start();
    }

    class ProgressThread implements Runnable{
        int i= 0;
        private ArcProgress progressBar;
        public ProgressThread(ArcProgress progressBar) {
            this.progressBar = progressBar;
        }
        @Override
        public void run() {
            for(;i<=100;i++){
                if(isFinishing()){
                    break;
                }
                Message msg = new Message();
                msg.what = i;
                Log.e("DEMO","i == "+i);
                msg.obj = progressBar;
                SystemClock.sleep(100);
                handler.sendMessage(msg);
            }
        }
    }
}
