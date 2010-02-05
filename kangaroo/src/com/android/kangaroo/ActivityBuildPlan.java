package com.android.kangaroo;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityBuildPlan extends Activity
{
  	private boolean mIsBound;
    private ServiceRoute serviceRouteBound;
    private TextView myText; 
    
	  @Override
	  public void onCreate(Bundle savedInstanceState) 
	  {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activitybuildplan); 
	        
	        // Watch for button clicks.
	        Button button = (Button)findViewById(R.id.bind);
	        button.setOnClickListener(mBindListener);
	        button = (Button)findViewById(R.id.unbind);
	        button.setOnClickListener(mUnbindListener);
	        
	        myText = (TextView)findViewById(R.id.text);
	        
	        
	  }   
	  
	  private FactoryServiceBinding myServiceFactory = new FactoryServiceBinding();
	  
	    private ServiceConnection mConnection = new ServiceConnection() 
	    {
	        public void onServiceConnected(ComponentName className, IBinder service) 
	        {
	        	serviceRouteBound = ((ServiceRoute.LocalBinder)service).getService();
	            //Toast.makeText(ActivityBuildPlan.this, "BAM - service bound",Toast.LENGTH_SHORT).show();
	        }
	
	        public void onServiceDisconnected(ComponentName className) 
	        {
	        	serviceRouteBound = null;
	            //Toast.makeText(ActivityBuildPlan.this, "BAM - service un-bound", Toast.LENGTH_SHORT).show();
	        }
	    };
	
	    private OnClickListener mBindListener = new OnClickListener() {
	        public void onClick(View v) {
	            // Establish a connection with the service.  We use an explicit
	            // class name because we want a specific service implementation that
	            // we know will be running in our own process (and thus won't be
	            // supporting component replacement by other applications).
	            
	        	 Intent i = new Intent();
	     	    i.setClassName( "com.android.kangaroo", "com.android.kangaroo.ServiceRoute" );
	        	
	     	    //bindService(new Intent(ActivityBuildPlan.this, ServiceRoute.class), mConnection, Context.BIND_AUTO_CREATE);
	        	bindService(i, mConnection, Context.BIND_AUTO_CREATE);
	        	//serviceRouteBound = myServiceFactory.getServiceRoute(ActivityBuildPlan.this);
	        	
	        	while(serviceRouteBound == null);
	        	
	        	mIsBound = true;
	            if(serviceRouteBound != null)
	            {
	            	myText.setText(serviceRouteBound.routingTest("test"));
	            }
	            else
	            {
	            	myText.setText("did not work");
	            }
	        }
	    };
	
	    private OnClickListener mUnbindListener = new OnClickListener() {
	        public void onClick(View v) {
	            if (mIsBound) {
	                // Detach our existing connection.
	                unbindService(mConnection);
	                mIsBound = false;
		            mIsBound = true;
		            if(serviceRouteBound != null)
		            {
		            	myText.setText("not ok");
		            }
		            else
		            {
		            	myText.setText("ok");
		            }
	            }
	        }
	    };
	  
	  
	  
}
