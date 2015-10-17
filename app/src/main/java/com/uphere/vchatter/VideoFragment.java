package com.uphere.vchatter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;
import android.widget.VideoView;
//
import com.uphere.vchatter.Chatter;


public class VideoFragment extends Fragment
{
     
    Chatter parent;
    public TextView  tv;
    public ScrollView sv;
    
    Button button;

    Toolbar toolbar;
    private EditText msginput;
    public String nickname;

    VideoView vv;

     
    public VideoFragment( Chatter p ) {
	parent = p;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
			       Bundle savedInstanceState) {
	View rootView = inflater.inflate(R.layout.video, container, false); 

	toolbar = (Toolbar)rootView.findViewById(R.id.toolbar);
	toolbar.setTitle("Chat");
	toolbar.setSubtitle("This uses chatter haskell program.");

	tv = (TextView) rootView.findViewById(R.id.textview);
	tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv.setTextColor(Color.WHITE);

	onCreateHS( R.id.textview, tv );
	
	sv = (ScrollView) rootView.findViewById(R.id.scrollview);
	
	msginput = (EditText) rootView.findViewById(R.id.edit_msg);
        msginput.setTextColor(Color.WHITE);
	msginput.setHintTextColor(Color.GRAY);
	msginput.setImeOptions(EditorInfo.IME_ACTION_DONE);
	msginput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		    if (actionId == EditorInfo.IME_ACTION_DONE) {
			String msg = msginput.getText().toString();
			if(parent.nickname != null && msg != null) {
			    onClickHS(parent.nickname,msg);
			    msginput.setText("");
			    return true;
			}
		    }
		    return false;
		}
	    });
	/*
	button = (Button) rootView.findViewById(R.id.button);
        button.setOnClickListener( new View.OnClickListener() {
		@Override
		public void onClick(View view) {
		    String msg = msginput.getText().toString();
		    if(parent.nickname != null && msg != null)
		    {
        		onClickHS(tv,parent.nickname,msg);
			msginput.setText("");
		    }
		}
		
	    }); 
	*/
	
	vv = (VideoView) rootView.findViewById(R.id.myvideo);
	String vaddr = "http://ianwookim.org/video/test.mp4";
	Uri vuri = Uri.parse(vaddr);
	vv.setVideoURI(vuri);
	vv.start(); 

	
	return rootView;
    }

    public native void onCreateHS( int id, TextView tv );
    
    public native void onClickHS( String nick, String msg);
    
}