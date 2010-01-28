package net.jimblackler.readcalendar;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        String s = Example.readCalendar(this);
        TextView tv = (TextView)findViewById(R.id.foo);
        tv.setText(s);
    }
}