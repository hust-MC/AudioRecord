package com.example.audiorecord;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.view.View.OnClickListener;
import android.util.Log;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.AudioRecord;

public class MainActivity extends Activity
{
	EditText inputIP;
	Button confirm, cancel, start, stop;

	boolean isRecording = false;
	String IP;

	public void widgetInit()
	{
		inputIP = (EditText) findViewById(R.id.inputIP);
		confirm = (Button) findViewById(R.id.confirm);
		cancel = (Button) findViewById(R.id.cancel);
		start = (Button) findViewById(R.id.start_bt);
		stop = (Button) findViewById(R.id.end_bt);
	}

	public void buttonEvent()
	{
		// ����ȷ���������¼�
		confirm.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				IP = inputIP.getText().toString();
			}
		});

		// �����˳��������¼�
		cancel.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				finish();
			}
		});

		// ���ÿ�ʼ�������¼�
		start.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				Thread thread = new Thread(new Runnable()
				{
					public void run()
					{
						record();
					}
				});
				thread.start();
				start.setEnabled(false);
				stop.setEnabled(true);
			}

		});

		// ����ֹͣ�������¼�
		stop.setEnabled(false);
		stop.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				isRecording = false;
				start.setEnabled(true);
				stop.setEnabled(false);
			}

		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		widgetInit();
		buttonEvent();
	}

	public void record()
	{
		int frequency = 11025;
		int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
		int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

		try
		{
			int bufferSize = AudioRecord.getMinBufferSize(frequency,
					channelConfiguration, audioEncoding);
			AudioRecord audioRecord = new AudioRecord(
					MediaRecorder.AudioSource.MIC, frequency,
					channelConfiguration, audioEncoding, bufferSize);

			isRecording = true;

			int trackSize = AudioTrack.getMinBufferSize(frequency,
					channelConfiguration, AudioFormat.ENCODING_PCM_16BIT);
			AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
					11025, AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT, trackSize,
					AudioTrack.MODE_STREAM);

			short[] buffer = new short[bufferSize];

			audioRecord.startRecording(); // ��ʼ¼��
			audioTrack.play(); // ��ʼ����
			while (isRecording)
			{
				int bufferReadResult = audioRecord.read(buffer, 0, bufferSize); // ����˷��ȡ��Ƶ
				short[] tmpBuf = new short[bufferReadResult]; // ���뻺��
				System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult); // �����ļ�
				audioTrack.write(buffer, 0, bufferReadResult);
			}
			audioTrack.stop();
			audioRecord.stop();

		} catch (Throwable t)
		{
			Log.e("AudioRecord", "Recording Failed");
		}
	}
}