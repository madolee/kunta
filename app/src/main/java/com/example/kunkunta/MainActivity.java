package com.example.kunkunta;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Button send_button;
    EditText send_editText;
    TextView send_textView;
    TextView read_textView;
    private DataInputStream dataInput;
    private static final String SERVER_IP = "18.144.100.147";
    private static final String CONNECT_MSG = "connect";
    private static final String STOP_MSG = "stop";

    private static final int BUF_SIZE = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send_button = findViewById(R.id.send_button);
        send_editText = findViewById(R.id.send_editText);
        send_textView = findViewById(R.id.send_textView);
        read_textView = findViewById(R.id.read_textView);


        send_button.setOnClickListener(view -> {
            Connect connect = new Connect();
            connect.execute(CONNECT_MSG);
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class Connect extends AsyncTask< String , String,Void > {
        private String output_message;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                DataOutputStream dataOutput;
                try (Socket client = new Socket(SERVER_IP, 7000)) {
                    dataOutput = new DataOutputStream(client.getOutputStream());
                    dataInput = new DataInputStream(client.getInputStream());
                }
                output_message = strings[0];
                dataOutput.writeUTF(output_message);

            } catch (UnknownHostException e) {
                String str = e.getMessage();
                Log.w("discount", str + " 1");
            } catch (IOException e) {
                String str = Objects.requireNonNull(e.getMessage());
                Log.w("discount", str + " 2");
            }

            while (true){
                try {
                    byte[] buf = new byte[BUF_SIZE];
                    int read_Byte  = dataInput.read(buf);
                    String input_message = new String(buf, 0, read_Byte);
                    if (!input_message.equals(STOP_MSG)){
                        publishProgress(input_message);
                    }
                    else{
                        break;
                    }
                    Thread.sleep(10);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... params){
            send_textView.setText(""); // Clear the chat box
            send_textView.append("sen_msg: " + output_message );
            read_textView.setText(""); // Clear the chat box
            read_textView.append("reuse_msg: " + params[0]);
        }
    }
}

#tttt