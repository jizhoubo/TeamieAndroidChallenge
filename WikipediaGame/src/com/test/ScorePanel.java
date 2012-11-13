package com.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScorePanel extends Activity {
	private TextView scoretitle;
	private TextView score;
	private Button replay;
	private Button exit;
	
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scorepanel);
        scoretitle = (TextView) findViewById(R.id.scoretitle);
        score = (TextView) findViewById(R.id.score);

        replay = (Button) findViewById(R.id.replay);
        exit = (Button) findViewById(R.id.exit);
        
        scoretitle.setText("Score");
        
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
        	String value = extras.getString("new_value");
        	System.out.println(value);
        	score.setText(value + " out of 10!");
        }
        
    }
	
	public void replayClicked(View v) {
    	Intent i = new Intent(ScorePanel.this, WikipediaGameActivity.class);
    	finish();
    	startActivity(i);
    }
	public void exitClicked(View v) {
		finish();
    }
}
