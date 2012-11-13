package com.test;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Wordlist extends Activity {
	private String[] words;
	private ListView mainListView ;  
	private ArrayAdapter<String> listAdapter ;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wordlist);
        mainListView = (ListView) findViewById(R.id.wordlist);
        Bundle b=this.getIntent().getExtras();
        String key = "";
        words=b.getStringArray(key);
        ArrayList<String> wordList = new ArrayList<String>();
        wordList.addAll( Arrays.asList(words) );
        listAdapter = new ArrayAdapter<String>(this, R.layout.row, wordList);
        mainListView.setAdapter( listAdapter );
        
        mainListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                String selectedFromList =(String) (mainListView.getItemAtPosition(myItemInt));
                System.out.println(selectedFromList);
              }                 
        });
    }
	
	public void replayClicked(View v) {
    	Intent i = new Intent(Wordlist.this, WikipediaGameActivity.class);
    	finish();
    	startActivity(i);
    }
}
