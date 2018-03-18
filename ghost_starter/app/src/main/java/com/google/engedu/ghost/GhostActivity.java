/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    FastDictionary fastDictionary;
    TextView ghostText;
    TextView label;
    Button challenge;
    Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();

        try {
            fastDictionary=new FastDictionary(assetManager.open("words.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
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

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        challenge=(Button)findViewById(R.id.challenge);
        challenge.setEnabled(true);
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {
        ghostText=(TextView)findViewById(R.id.ghostText);
        label = (TextView) findViewById(R.id.gameStatus);
        String ghost=ghostText.getText().toString();
        if(ghost.length()>=4 && fastDictionary.isWord(ghost)) {
            label.setText("Computer Wins!\nWord exists!");
            challenge.setEnabled(false);
        }
        else {
            String getAny=fastDictionary.getAnyWordStartingWith(ghost);
            if(getAny==null) {
                label.setText("Computer Wins!\nNo word can be made from this!");
                challenge.setEnabled(false);
            }
            else if(getAny.length()==ghost.length()) {
                label.setText("Computer Wins!\nWord exists!");
                challenge.setEnabled(false);
            }
            else {
                ghostText.append(""+getAny.charAt(ghost.length()));
                userTurn = true;
                label.setText(USER_TURN);
            }
        }
    }

    public void challenge(View view) {
        String text=ghostText.getText().toString();
        if(text.length()>=4 && fastDictionary.isWord(text) || fastDictionary.getAnyWordStartingWith(text)==null) {
            label.setText("You Win!");
            challenge.setEnabled(false);
        }
        else {
            String possibleWord=fastDictionary.getAnyWordStartingWith(text);
            label.setText("Computer Wins!\nPossible Word: "+possibleWord);
            challenge.setEnabled(false);
        }
    }
    public void reset(View view) {
        onStart(null);
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        ghostText=(TextView)findViewById(R.id.ghostText);
        label = (TextView) findViewById(R.id.gameStatus);
        challenge=(Button)findViewById(R.id.challenge);
        char c=(char)event.getUnicodeChar();
        if(c>='a' && c<='z' || c>='A' && c<='Z') {
            ghostText.append(""+c);
            label.setText("Computer's turn");
            userTurn=false;
            computerTurn();
        }
        else {
            Toast.makeText(this, "Enter a valid keyword!", Toast.LENGTH_SHORT).show();
        }
        return super.onKeyUp(keyCode, event);
    }
}
