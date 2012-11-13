package com.test;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.Vector;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.BufferType;

public class WikipediaGameActivity extends Activity {
    /** Called when the activity is first created. */
	private TextView page;
	private Button submit; 
	private String html = null;
	Vector<String> sentences = new Vector<String>();
	String output_final = "";
	private String[] missingwords = new String[10];
	private String[] filledwords = new String[10];
	private int[] missingwords_sentence_index = new int[10];
	private String[] missingwords_ordered = new String[10];
	private int blank_clicked_index = 0;
	boolean[] flag;
	private String blank_clicked = "";
	private String blank_filled = "";
	
	private ListView mainListView ;  
	private ArrayAdapter<String> listAdapter ;
	ArrayList<String> wordList = new ArrayList<String>();
	
	DefaultHttpClient httpClient = new DefaultHttpClient();
    HttpGet httpGet = new HttpGet("http://en.wikipedia.org/wiki/Special:Random");
    ResponseHandler<String> resHandler = new BasicResponseHandler();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        page = (TextView) findViewById(R.id.content);
        page.setFocusable(false);
        submit = (Button) findViewById(R.id.submit);
        
        mainListView = (ListView) findViewById(R.id.wordlist);
        mainListView.setVisibility(View.GONE);
        mainListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                String selectedFromList =(String) (mainListView.getItemAtPosition(myItemInt));
                System.out.println(selectedFromList);
                blank_filled = selectedFromList;
                filledwords[blank_clicked_index-1] = blank_filled;
                System.out.println("blank "+blank_clicked_index+" is filled with "+blank_filled);
                //hide listview, replace [wordX] with the filledwords[blank_clicked_index]
                mainListView.setVisibility(View.GONE);
                
                output_final = output_final.replace("[word"+blank_clicked_index+"]", filledwords[blank_clicked_index-1]);
                page.setMovementMethod(LinkMovementMethod.getInstance());
                page.setText(addClickablePart(output_final), BufferType.SPANNABLE);
              }                 
        });
        
        ScorePanel scorepanel=new ScorePanel();
        
        generatePage();
        
    }
    private void generatePage() {
    	sentences.clear();
    	DefaultHttpClient httpClient = new DefaultHttpClient();
        httpGet = new HttpGet("http://en.wikipedia.org/wiki/Special:Random");
        resHandler = new BasicResponseHandler();
        try {
			html = httpClient.execute(httpGet, resHandler);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        Document doc = Jsoup.parse(html);
        String text = doc.body().text();
        //System.out.println(text);
        Element content = doc.getElementById("content");
        //get heading
        String headingText = "";
        Elements heading = content.getElementsByTag("h1");
        headingText += heading.text();
        //get paragraphs
        Elements paragraphs = content.getElementsByTag("p");
        String paragraphText = "";
        int NoOfParagraphs = 0;
        for (Element paragraph : paragraphs) {
        	paragraphText += paragraph.text() + "\n";
        	NoOfParagraphs++;
        }
        System.out.println(NoOfParagraphs);
        //compute number of sentences
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        String source = paragraphText;
        //source = source.replaceAll("\\[\\d+\\] ", "");
        source = source.replaceAll("\\[.*?\\]", "").replaceAll(" +", " ");
        source.replace("[", "").replace("]","");
        int sentence_index = 0;
        iterator.setText(source);
        int start = iterator.first();
        for (int end = iterator.next();
            end != BreakIterator.DONE;
            start = end, end = iterator.next()) {
        	sentences.add(source.substring(start,end));
        	sentence_index++;
        	
        }
        int number_sentences = sentences.size();
        System.out.println(number_sentences);
        if (number_sentences>40 || number_sentences<15) {
        	sentences.clear();
        	generatePage();
        }
        else {
        	//randomly select sentences, randomly select words in selected sentences
        	generateWords();
        	//test click blank
            //paragraphText += "behind is test blank: ";
            //String sentence = "this is [part 1] and [here another] and [another one]";
        	//String whole = Arrays.toString(AllwordsInOnesentence)
        	
        	String [] s = sentences.toArray(new String[sentences.size()]);
        	
        	for(int i=0;i<s.length;i++)
        		output_final += s[i];
        	
        	System.out.println(output_final);
        	//System.out.println(output[0]);
            page.setMovementMethod(LinkMovementMethod.getInstance());
            page.setText(addClickablePart(output_final), BufferType.SPANNABLE);
        	//page.setText(headingText + "\n" + paragraphText);
        }
        	
    }
    private void generateWords() {
    	System.out.println("in generateWords()");
    	System.out.println(sentences);
    	
    	int remaining = 10;
    	flag = new boolean[sentences.size()];
    	for(int i=0;i<sentences.size();i++)
    		flag[i] = false;
    	int min = 0; 
		int max = sentences.size();
    	while(remaining>0 && max>0) {
    		System.out.println("inside!");
    		Random r = new Random();
    		int i1 = r.nextInt(max)+min;
    		//if this index already been marked, generate another one
    		while(flag[i1] == true)
    			i1 = r.nextInt(max-min)+min;
    		flag[i1] = true;
    		System.out.println(i1 + sentences.get(i1));
    		remaining--;
    		//select word for selected sentence
    		String AllwordsInOnesentence[] = sentences.get(i1).split(" ");
    		Random r1 = new Random();
    		int i11 = r1.nextInt(AllwordsInOnesentence.length);
    		missingwords[9-remaining] = AllwordsInOnesentence[i11];
    		missingwords_sentence_index[9-remaining] = i1;		//record down respected sentence index
    		System.out.println(missingwords[9-remaining] + "with sentence index " + missingwords_sentence_index[9-remaining]);
    		//replace word with [ ]
    		AllwordsInOnesentence[i11] = "[   ]";
    		String temp = "";
    		for(int m=0;m<AllwordsInOnesentence.length;m++) {
    			temp += AllwordsInOnesentence[m];
    			temp += " ";
    		}
    			
    		//sentences.set(i1, Arrays.toString(AllwordsInOnesentence));
    		sentences.set(i1, temp);	
    	}
    	
    	wordList.addAll( Arrays.asList(missingwords) );
        listAdapter = new ArrayAdapter<String>(this, R.layout.row, wordList);
        mainListView.setAdapter( listAdapter );
        
    	assignNumber();
    	System.out.println(sentences);
    	generate_missingwords_ordered();
    }
    private void generate_missingwords_ordered() {
    	
    	for(int i=0;i<10;i++) {
    		System.out.println("sentence "+missingwords_sentence_index[i]+" missingword "+missingwords[i]);
    	}
    	missingwords_ordered = missingwords;
    	
    	for(int i=0;i<missingwords_sentence_index.length-1;i++) {
    		int index = i;
    		int min=missingwords_sentence_index[i];		//min is the minimum sentence index
    		for(int j=i+1;j<missingwords_sentence_index.length;j++) {
    			if(missingwords_sentence_index[j]<min) {
    				min = missingwords_sentence_index[j];
    				index = j;    				
    			}
    		}
    		
    		String temp_word = missingwords_ordered[index];
    		missingwords_ordered[index] = missingwords_ordered[i];
    		missingwords_ordered[i] = temp_word;
    		System.out.println(index + " " + missingwords_sentence_index[index]);
    		int temp_int = missingwords_sentence_index[index];
    		missingwords_sentence_index[index] = missingwords_sentence_index[i];
    		missingwords_sentence_index[i] = temp_int;
    	}	
    	System.out.println("");   	
    	for(int i=0;i<missingwords_ordered.length;i++)
    		System.out.println(missingwords_ordered[i]);
    }
    private void assignNumber() {
    	System.out.println("starts here");
    	int index=1;
    	for(int i=0;i<sentences.size();i++) {
    		if(flag[i] == true) {
    			String temp = sentences.get(i);
    			temp = temp.replace("[   ]", ("[word"+index+"]"));
    			System.out.println(temp);
    			index++;
    			sentences.set(i, temp);
    		}
    			
    	}
    }

    private SpannableStringBuilder addClickablePart(String str) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);

        int idx1 = str.indexOf("[");
        int idx2 = 0;
        while (idx1 != -1) {
            idx2 = str.indexOf("]", idx1) + 1;

            final String clickString = str.substring(idx1, idx2);
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                	TextView tv = (TextView) widget;
                	blank_clicked = tv
                			.getText()
                			.subSequence(tv.getSelectionStart(),
                					tv.getSelectionEnd()).toString();
                	if(blank_clicked.charAt(5) == '1' && blank_clicked.charAt(6) == '0')
                		blank_clicked_index = 10;
                	else
                		blank_clicked_index = blank_clicked.charAt(5) - '0';
                    System.out.println(blank_clicked_index+ " is clicked!");
                    
                    mainListView.setVisibility(View.VISIBLE);
                }
            }, idx1, idx2, 0);
            idx1 = str.indexOf("[", idx2);
        }

        return ssb;
    }
    public void submitClicked(View v) {
    	System.out.println("submit clicked!");
    	String score = "";
    	int Score = 0;
    	for(int i=0;i<10;i++) {
    		if(missingwords_ordered[i] == filledwords[i])
    			Score++;
    	}
    	System.out.println(Score);
    	Intent i = new Intent(WikipediaGameActivity.this, ScorePanel.class);
    	i.putExtra("new_value", Score+score);
    	finish();
    	startActivity(i);
    }
}