package com.example.mcberliner.socketdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class MainActivity extends Activity {

    private Handler handler = new Handler();
    public ListView msgList;
    public ArrayAdapter<String> adapter;

    private static final String HOST_ADDR = "10.0.2.2";
    private static final int PORT_NUMBER = 8013;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        msgList = (ListView)findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        msgList.setAdapter(adapter);

        Button btnSend = (Button) findViewById(R.id.btnSend);

        receiveMsg();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText etMessage = (EditText)findViewById(R.id.etInput);
                sendMessageToServer(etMessage.getText().toString());
                msgList.smoothScrollToPosition(adapter.getCount() - 1);
            }
        });
    }

    public void sendMessageToServer(String str) {
        final String msg = str;
        new Thread(new Runnable() {
            @Override
            public void run() {
                PrintWriter out;
                try{
                    Socket socket = new Socket(HOST_ADDR, PORT_NUMBER);
                    out = new PrintWriter(socket.getOutputStream());
                    out.println(msg);
                    out.flush();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }) .start();
    }

    public void receiveMsg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                BufferedReader in = null;
                try {
                    socket = new Socket(HOST_ADDR, PORT_NUMBER);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                while (true) {
                    String msg = null;
                    try {
                        msg = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (msg == null) {
                        break;
                    } else {
                        displayMsg(msg);
                    }
                }
            }
        }).start();
    }

    public void displayMsg(final String msg) {
        final String msgText = msg;
        handler.post(new Runnable() {
            @Override
            public void run() {
                adapter.add(msgText);
                msgList.smoothScrollToPosition(adapter.getCount() - 1);
            }
        });
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
