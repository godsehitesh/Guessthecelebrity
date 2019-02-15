package com.example.hitesh.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    Map<String, String> map;
    ImageView imageView;
    ArrayList<String> answers =new ArrayList<String>();
    ArrayList<String> askedCelebrities = new ArrayList<String>();
    Integer locationOfCorrectAnswer;
    EditText editText, timerText, gameOverText, scoreText, welcomeText;
    TextView scoreView;
    Integer score, questionsAsked;
    CountDownTimer timer;
    LinearLayout answersLayout;
    Button playAgainButton, playButton;
    Toast toast;
    String CorrectCelebrityName;

    public void displayQuestion(View view){

        List<String> keysAsArray = new ArrayList<String>(map.keySet());
        Random r = new Random();

        Integer RandomNumber = r.nextInt(keysAsArray.size());
        String RandomCelebrityName = keysAsArray.get(RandomNumber);

        while(askedCelebrities.contains(RandomCelebrityName))
        {
            RandomNumber = r.nextInt(keysAsArray.size());
            RandomCelebrityName = keysAsArray.get(RandomNumber);
        }

        askedCelebrities.add(RandomCelebrityName);
        String RandomCelebrityURL = map.get(RandomCelebrityName);

//        Log.i("Celebrity:", RandomCelebrityName +" "+ RandomCelebrityURL);
        locationOfCorrectAnswer = r.nextInt(4);

        CorrectCelebrityName = RandomCelebrityName;

        scoreView.setText(Integer.toString(score) + "/" + Integer.toString(questionsAsked));

        answers.clear();

        for(int i = 0; i<4;i++)
        {
            if(i==locationOfCorrectAnswer)
            {
                answers.add(CorrectCelebrityName);
            }
            else
            {
                RandomNumber = r.nextInt(keysAsArray.size());
                String IncorrectCelebrityName = keysAsArray.get(RandomNumber);

                while(answers.contains(IncorrectCelebrityName)||CorrectCelebrityName.equals(IncorrectCelebrityName))
                {
                    RandomNumber = r.nextInt(keysAsArray.size());
                    IncorrectCelebrityName = keysAsArray.get(RandomNumber);
                }

                answers.add(IncorrectCelebrityName);

            }
        }

        Button button0 = (Button)findViewById(R.id.answer1);
        Button button1 = (Button)findViewById(R.id.answer2);
        Button button2 = (Button)findViewById(R.id.answer3);
        Button button3 = (Button)findViewById(R.id.answer4);

        button0.setText(answers.get(0));
        button1.setText(answers.get(1));
        button2.setText(answers.get(2));
        button3.setText(answers.get(3));

        downloadAndDisplayImage(RandomCelebrityURL);
    }

    public void clickedAnswer(View view)
    {

        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer)))
        {
            toast = Toast.makeText(getApplicationContext(),
                    "Correct!",
                    Toast.LENGTH_SHORT);

            toast.show();
            score++;

        }
        else
        {
            toast = Toast.makeText(getApplicationContext(),
                    "Incorrect! It was "+CorrectCelebrityName,
                    Toast.LENGTH_SHORT);

            toast.show();}

        questionsAsked++;
        displayQuestion(view);
    }

    public void downloadAndDisplayImage(String imgUrl) {

        // https://upload.wikimedia.org/wikipedia/en/a/aa/Bart_Simpson_200px.png

        ImageDownloader task = new ImageDownloader();
        Bitmap myImage;

        try {
            myImage = task.execute(imgUrl).get();

            imageView.setImageBitmap(myImage);

        } catch (Exception e) {

            e.printStackTrace();

        }

        Log.i("Interaction", "Button Tapped");

    }


    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection)url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }

                return result;

            }
            catch(Exception e) {

                e.printStackTrace();

                return "Failed";

            }


        }

    }
    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;


            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }

            return null;

        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadTask task = new DownloadTask();
        String htmlText = null;

        map = new HashMap<String,String>();

        welcomeText = (EditText)findViewById(R.id.welcomeText);
        playButton = (Button)findViewById(R.id.playButton);
        imageView = (ImageView) findViewById(R.id.imageView);
        editText = (EditText)findViewById(R.id.timerText);
        scoreView = (TextView)findViewById(R.id.scoreView);
        answersLayout = (LinearLayout)findViewById(R.id.answersLayout);
        gameOverText = (EditText)findViewById(R.id.gameOverText);
        scoreText = (EditText)findViewById(R.id.scoreText);
        playAgainButton = (Button)findViewById(R.id.playAgainButton);
        timerText = (EditText)findViewById(R.id.timerText);


        try {

            htmlText = task.execute("http://www.posh24.se/kandisar").get();

        } catch (InterruptedException e) {

            e.printStackTrace();

        } catch (ExecutionException e) {

            e.printStackTrace();

        }

        String[] splitHTMLText = htmlText.split("<div class=\"sidebarContainer\">");

        Pattern p = Pattern.compile("<img src=\"(.*?)\" alt=\"(.*?)\"/>");
        Matcher m = p.matcher(splitHTMLText[0]);

        int i=1;

        while(m.find()) {

        //    Log.i("Extracted text:", m.group(1)+" "+m.group(2));
            map.put(m.group(2), m.group(1));

        }
//Log.i("HashMap:", String.valueOf(map));

      //  Log.i("Contents Of URL", htmlText);

        Log.i("Size of Map:", Integer.toString(map.size()));

        timerText.setVisibility(View.INVISIBLE);
        scoreView.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        answersLayout.setVisibility(View.INVISIBLE);

        gameOverText.setVisibility(View.INVISIBLE);
        scoreText.setVisibility(View.INVISIBLE);
        playAgainButton.setVisibility(View.INVISIBLE);


    }

    public void playClicked(View view){

        welcomeText.setVisibility(View.INVISIBLE);
        playButton.setVisibility(View.INVISIBLE);

        gameOverText.setVisibility(View.INVISIBLE);
        scoreText.setVisibility(View.INVISIBLE);
        playAgainButton.setVisibility(View.INVISIBLE);

        timerText.setVisibility(View.VISIBLE);
        scoreView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        answersLayout.setVisibility(View.VISIBLE);

        score = 0;
        questionsAsked = 0;

        askedCelebrities.clear();

        timer = new CountDownTimer(60 * 1000, 1000) {

            public void onTick(long millisecondsUntilDone) {
                timerText.setText(Long.toString(millisecondsUntilDone/1000) + "s");
            }

            public void onFinish() {

                timerText.setVisibility(View.INVISIBLE);
                scoreView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                answersLayout.setVisibility(View.INVISIBLE);

                gameOverText.setVisibility(View.VISIBLE);
                scoreText.setVisibility(View.VISIBLE);
                playAgainButton.setVisibility(View.VISIBLE);

                timerText.setText("0s");
                scoreText.setText("Your score: "+ Integer.toString(score) + "/" + Integer.toString(questionsAsked));

            }
        }.start();


        displayQuestion(imageView);

    }
}
