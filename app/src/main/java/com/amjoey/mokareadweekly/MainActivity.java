package com.amjoey.mokareadweekly;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import Moka7.*;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {
                // we add 100 new entries
                for (int i = 0; i < 100; i++) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            new PlcReader().execute("");
                        }
                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
    }
    S7Client client = new S7Client();

    public void readdb_val(View v){
        new PlcReader().execute("");
    }


    private class PlcReader extends AsyncTask<String, Void, String> {

        String ret = "";

        @Override
        protected String doInBackground(String... params){

            try{
                client.SetConnectionType(S7.S7_BASIC);
                int res=client.ConnectTo("192.168.1.12",0,0);

                if(res==0){//connection OK

                    byte[] data = new byte[12];
                    res = client.ReadArea(S7.S7AreaDB,1,1,12,data);
                    //  ret = "value of DB1.DBD25: "+ S7.GetFloatAt(data,0);
                    //  ret = "value of DB1.DBD10: "+ S7.GetWordAt(data,0);
                    ret = "Value of DB1.DBD1: "+ S7.GetWordAt(data,0)+"/"+ S7.GetWordAt(data,2)+"/"+ S7.GetWordAt(data,4)+"/"+ S7.GetWordAt(data,6)+"/"+ S7.GetWordAt(data,8)+"/"+ S7.GetWordAt(data,10);
 /*
                    byte[] dataWrite = new byte[2];
                   // S7.SetBitAt(dataWrite, 0, 1, true);
                   // S7.SetDIntAt(dataWrite,0,5);
                    S7.SetWordAt(dataWrite,0,700);

                    client.WriteArea(S7.S7AreaDB, 1, 12, 2, dataWrite);
                    ret = "WriteArea of DB1.DBD12: OK ";
                    */


                }else{
                    ret = "ERR: "+ S7Client.ErrorText(res);
                }
                client.Disconnect();
            }catch (Exception e) {
                ret = "EXC: "+e.toString();
                Thread.interrupted();
            }
            return "executed";
        }

        @Override
        protected void onPostExecute(String result){
            TextView txout = (TextView) findViewById(R.id.textView);
            txout.setText(ret);
        }
    }
}
