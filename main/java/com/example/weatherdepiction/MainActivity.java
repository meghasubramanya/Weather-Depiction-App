package com.example.weatherdepiction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    TextView tvResult;
    EditText etName;
    Button btnWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult=findViewById(R.id.tvResult);
        etName=findViewById(R.id.etName);
        btnWeather=findViewById(R.id.btnWeather);

        btnWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    DownloadTask task = new DownloadTask();

                    //to convert spaces into 20%.
                    String encodedString = URLEncoder.encode(etName.getText().toString(), "UTF-8");

                    task.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedString+ "&appid=439d4b804bc8187953eb36d2a8c26a02");

                    //to exit keyboard after the button is pressed.
                    InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(etName.getWindowToken(), 0);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Could not depict weather :(", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });


    }

    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {

            String result = "";
            URL url;
            HttpURLConnection httpURLConnection = null;

            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = in.read();
                while (data != -1) {
                    char current = (char) data;
                    result = result + current;
                    data = reader.read();          //reads the next data.
                }
                return result;
            } catch (Exception e) {

                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try
            {
                JSONObject jsonObject=new JSONObject(s);
                String weatherInfo=jsonObject.getString("weather");
                JSONArray array=new JSONArray(weatherInfo);

                String message="";

                for(int i=0;i<array.length();i++)
                {
                    JSONObject jsonPart=array.getJSONObject(i);
                    String main=jsonPart.getString("main");
                    String description=jsonPart.getString("description");

                    if(!main.equals("")&& !description.equals(""))
                    {
                        message+=main + " : " + description + "\r\n";
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Could not depict weather :(", Toast.LENGTH_SHORT).show();
                    }
                }

                if(!message.equals(""))
                {
                    tvResult.setText(message);
                }
            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(), "Could not depict weather :(", Toast.LENGTH_SHORT).show();
               e.printStackTrace();
            }
        }
    }
}