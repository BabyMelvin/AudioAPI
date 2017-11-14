package com.study.audioapi.process;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.study.audioapi.R;

import ca.uol.aig.fftpack.RealDoubleFFT;

/**
 * 音频处理
 * 通过AudioRecord对象对音频进行其他处理。
 *      声音是经过某中物质振动，振动可以被麦克风所捕获。麦克风将通过空气传播的振动转换成一个不断变化的电流。
 * 当计算机使用麦克风来捕获声音时，该声音将会被数字化。
 *      具体，特定大小的振幅样本会被每秒钟采集多次（采样率）。这个数据流称为PCM(脉冲编码调制)流，器形成了数字音频的基础。
 * PCM流形式的样本形成了捕获音频波形。
 *
 * 可视化频率
 *      通常分析音频的方法是可视化其中存在的频率。通常这些类型的可视化采用了均衡器，均衡器允许调整各种频率范围的级别。
 *
 * 将音频信号转换成分量频率（component frequency）的技术采用了一个数学变化，称为离散傅里叶变换(Discrete Fourier Transform,DFT)
 * FFTPACK库的Java端口，提供支持。
 * */

public class ProcessingAudio extends AppCompatActivity implements View.OnClickListener {

    private Button mStartButton;
    private ImageView mImageView;
    private final  int FREQUENCY=8000;
    private final int CHANNEL_CONFIGTUATION= AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private final int AUDIO_ENCODING=AudioFormat.ENCODING_PCM_16BIT;
    private int mBlockSize=256;
    private RealDoubleFFT mTransformer;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private boolean started=false;
    private double mToTransform[]=new double[mBlockSize];
    private RecordAudioTask mRecordAudioTask;
    private double[] mDoubles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing_audio);
        initView();
        initData();
    }

    private void initData() {
        mTransformer= new RealDoubleFFT(mBlockSize);
        mRecordAudioTask = new RecordAudioTask();
        mBitmap = Bitmap.createBitmap(256, 100, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mImageView.setImageBitmap(mBitmap);
    }

    private void initView() {
        mStartButton = (Button) findViewById(R.id.process_button_start);
        mImageView = (ImageView) findViewById(R.id.process_image_view);
        mStartButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.process_button_start:
                if(started){
                    started=false;
                    mStartButton.setText("Start");
                    if(mRecordAudioTask!=null)
                         mRecordAudioTask.cancel(true);
                }else {
                    started=true;
                    mStartButton.setText("Stop");
                    mRecordAudioTask=new RecordAudioTask();
                    mRecordAudioTask.execute();
                }
                break;
            default:
                break;
        }
    }
    private class RecordAudioTask extends AsyncTask<Void,double[],Void>{

        @Override
        protected Void doInBackground(Void... params) {
            int minBufferSize = AudioRecord.getMinBufferSize(FREQUENCY, CHANNEL_CONFIGTUATION, AUDIO_ENCODING);
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, FREQUENCY, CHANNEL_CONFIGTUATION, AUDIO_ENCODING,minBufferSize);
            short[] buffer=new short[mBlockSize];
            mDoubles = new double[mBlockSize];
            audioRecord.startRecording();
            while(started){
                int bufferReadResult=audioRecord.read(buffer,0,mBlockSize);
                for(int i=0;i<mBlockSize&&i<bufferReadResult;i++){
                    //AudioRecord对象中读取数据之后进行遍历，将short值转成double值。不能直接强制转化
                    //因为期望值在-1.0~1.0之间，而不是整个之的范围/32768.0short最大值，实现了这个目的。
                    mToTransform[i]=buffer[i]/32768.0;
                }
                /**
                 * 使用256个值企鹅采样率是8000，确定数组中每个元素将近似覆盖15.625Hz。
                 * 通过这个采样率除以2，然后除以256，就可以得到这个数字。因此，数组中第一个元素表示的数据将代表0~15.625Hz之间音频级别。
                 * */
                mTransformer.ft(mToTransform);
                publishProgress(mToTransform);
            }
            audioRecord.stop();
            return null;
        }

        @Override
        protected void onProgressUpdate(double[]... values) {
            super.onProgressUpdate(values);
            mCanvas.drawColor(Color.BLACK);
            for(int i=0;i<values[0].length;i++){
                int x=i;
                int downY= (int) (100-values[0][i]*10);
                int upY=100;
                mCanvas.drawLine(x,downY,x,upY,mPaint);
            }
            mImageView.invalidate();
        }
    }
}
