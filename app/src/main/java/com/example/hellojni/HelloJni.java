
package com.example.hellojni;

import android.app.Activity;
import android.os.Bundle;

import android.widget.TextView;
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

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d("HELLO", "HelloJni Activity onCreate");

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

        addListenerOnButton();
        
        //stringFromJNI();
        //setContentView(tv);
        
        Log.d("HELLO", "HelloJni Activity created");
    }

    public void addListenerOnButton() 
    {
        button = (Button) findViewById(R.id.button1);
        button.setOnClickListener
            ( 
              new OnClickListener() 
                {
                    @Override
                    public void onClick(View arg0) 
                    {
                        onClickHS(tv);
                        Log.d("HELLO", "button clicked");
                    }
                }
            );
    }

    public native void onCreateHS(TextView tv); // (Bundle savedInstanceState);
    public native void onClickHS(TextView tv); // (Bundle savedInstanceState);

    public native String  stringFromJNI(); // (Bundle savedInstanceState);
    public native String  unimplementedStringFromJNI();
    
    static {
        Log.d("HELLO", "in class STATIC haskell library LOADING");
        System.loadLibrary("haskell");
        Log.d("HELLO", "in class STATIC haskell library loaded");
    }

}
