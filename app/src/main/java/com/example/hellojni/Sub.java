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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;
import android.widget.VideoView;

import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import java.io.IOException;
import java.util.List;



    
public class Sub extends Activity
{
    private class NicknameDialogFragment extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setMessage("Set your nickname")
		.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			    Log.d("HELLOJNI", "button clicked"); 
			}
		    });
	    return builder.create();
	}
	
    }

    private class IdleHandler implements MessageQueue.IdleHandler {
	private Looper _looper;
	private int count;
	protected IdleHandler(Looper looper) {
	    _looper = looper;
	    count = 0;
	}
	public boolean queueIdle() {
	    onIdleHS(tv);
	    return(true);
	}
       
    }

    
    public void ProcessEvents()
    {
	Log.d("HELLOJNI", "ProcessEvents");
        Looper looper = Looper.myLooper();
        looper.myQueue().addIdleHandler(new IdleHandler(looper));	
    }
    
    Button button;
    TextView  tv;
    ListView lv;
    //FloatingActionButton fab;
    VideoView vv; 

    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
	setContentView(R.layout.sub);


	toolbar = (Toolbar)findViewById(R.id.toolbar2);
	toolbar.setTitle("Chat");
	toolbar.setSubtitle("This uses chatter haskell program.");
 	tv = (TextView) findViewById(R.id.textview2);
	tv.setMovementMethod(ScrollingMovementMethod.getInstance());
	////setActionBar(toolbar);
	//toolbar.inflateMenu(R.menu.toolbar2); 

	//fab = (FloatingActionButton)findViewById(R.id.favorite2);
	button = (Button)findViewById(R.id.button1);
        button.setOnClickListener( new View.OnClickListener() {
		@Override
		public void onClick(View view) {
		    onClickHS(tv);
		}

	    });
	
	FragmentManager fm = getFragmentManager();
	NicknameDialogFragment n = new NicknameDialogFragment();
	n.show(fm,"fragment_nickname");
	
	ProcessEvents();
	    
    }

    public native void onCreateHS(TextView tv);

    public native void onClickHS(TextView tv);

    public native void onIdleHS(TextView tv);
    
}
