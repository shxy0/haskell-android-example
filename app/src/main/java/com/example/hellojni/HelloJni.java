/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.hellojni;

import android.app.Activity;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.util.Log;

public class HelloJni extends Activity
{
    
    Button button;
    TextView  tv; 

    Toolbar toolbar;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d("HELLO", "haskell library LOADING");
        System.loadLibrary("haskell");
        Log.d("HELLO", "haskell library loaded");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // onCreateHS(savedInstanceState);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("My toolbar");
        toolbar.setSubtitle("Subtitle");
        //setActionBar(toolbar);
        toolbar.inflateMenu(R.menu.toolbar);

        //tv.setText( stringFromJNI() );
        tv = (TextView) findViewById(R.id.textview1);
        onCreateHS(tv);

        // addListenerOnButton();
        
        //stringFromJNI();
        //setContentView(tv);
        
        Log.d("HELLO", "HelloJni Activity created");
    }
/*
    public void addListenerOnButton() 
    {
        button = (Button) findViewById(R.id.button1);
        button.setOnClickListener
          ( new OnClickListener() 
            {
                @Override
                public void onClick(View arg0) 
                {
                    // Log.d("HELLOJNI", "button clicked");
                    onClickHS(tv);
                }
            }
          );
    }
*/
    public native void onCreateHS(TextView tv); // (Bundle savedInstanceState);

    // public native void onClickHS(TextView tv); // (Bundle savedInstanceState);

    // public native String  stringFromJNI(); // (Bundle savedInstanceState);
    // public native String  unimplementedStringFromJNI();
    
/*
    static {
        Log.d("HELLO", "haskell library LOADING");
        System.loadLibrary("haskell");
        Log.d("HELLO", "haskell library loaded");
    }
*/
}
