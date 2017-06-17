
package com.example.alien.sampleinventory;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Min Hye on 5/23/2017.
 */

public class BackgroundTask extends AsyncTask<String, Void, String> {

    Context ctx;

    BackgroundTask(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String insert_url = "http://192.168.1.102/info.php";
        String epc = params[0];
        String user = params[1];
        String tid = params[2];
        String trans_p = params[3];
        String rssi = params[4];
        String count = params[5];

        Log.d("count", ": " + count);

        try {
            URL url = new URL(insert_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            if(epc == null) epc = "NULL";
            if(user == null) user = "NULL";
            if(tid == null) tid= "NULL";
          //  if(acc_pass == null) acc_pass = "NULL";
          //  if(kil_pass == null) kil_pass = "NULL";
            if(rssi == "0") rssi = "NULL";

            String tagInfo = URLEncoder.encode("epc", "UTF-8") + "=" + URLEncoder.encode(epc, "UTF-8") + "&" +
                            URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8") + "&" +
                            URLEncoder.encode("tid", "UTF-8") + "=" + URLEncoder.encode(tid, "UTF-8") + "&" +
                            URLEncoder.encode("trans_p", "UTF-8") + "=" + URLEncoder.encode(trans_p, "UTF-8") + "&" +
                            URLEncoder.encode("rssi", "UTF-8") + "=" + URLEncoder.encode(rssi, "UTF-8") + "&" +
                            URLEncoder.encode("count", "UTF-8") + "=" + URLEncoder.encode(count, "UTF-8");
            writer.write(tagInfo);
            writer.flush();
            writer.close();
            os.close();
            InputStream is = conn.getInputStream();
            is.close();
            return "Insertion Success!";

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
        }

@Override
protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        }

@Override
protected void onPostExecute(String result) {
       // Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
        }
}
