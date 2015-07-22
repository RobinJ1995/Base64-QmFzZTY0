package be.robinj.base64;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();

        if (id == R.id.action_about)
        {
			Intent intent = new Intent (this, About.class);
	        this.startActivity (intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

	public static String encode (String str)
	{
		return Base64.encodeToString(str.trim().getBytes(), Base64.DEFAULT);
	}

	public static String decode (String str)
	{
		return new String (Base64.decode (str.trim ().getBytes (), Base64.DEFAULT));
	}

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {
	    public PlaceholderFragment ()
        {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

	        final TextView txtPlain = (TextView) rootView.findViewById (R.id.txtPlain);
	        final TextView txtBase64 = (TextView) rootView.findViewById (R.id.txtBase64);

	        txtPlain.addTextChangedListener
	        (
		        new TextWatcher ()
		        {
			        @Override
			        public void beforeTextChanged (CharSequence charSequence, int i, int i2, int i3)
			        {
				        txtPlain.setError (null);
				        txtBase64.setError (null);
			        }

			        @Override
			        public void onTextChanged (CharSequence charSequence, int i, int i2, int i3)
			        {
				        if (txtPlain.hasFocus ())
				        {
					        try
					        {
						        String encoded = MainActivity.encode(charSequence.toString());
				                txtBase64.setText(encoded);
					        }
					        catch (IllegalArgumentException ex)
					        {
								txtBase64.setError (getString (R.string.invalidInput));
					        }
				        }
			        }

			        @Override
			        public void afterTextChanged (Editable editable)
			        {
			        }
		        }
	        );

	        txtBase64.addTextChangedListener
            (
		        new TextWatcher ()
		        {
			        @Override
			        public void beforeTextChanged (CharSequence charSequence, int i, int i2, int i3)
			        {
				        txtPlain.setError (null);
				        txtBase64.setError (null);
			        }

			        @Override
			        public void onTextChanged (CharSequence charSequence, int i, int i2, int i3)
			        {
				        if (txtBase64.hasFocus ())
				        {
					        try
					        {
						        String decoded = MainActivity.decode(charSequence.toString());
				                txtPlain.setText(decoded);
					        }
					        catch (IllegalArgumentException ex)
					        {
						        txtBase64.setError (getString (R.string.invalidBase64));
					        }
				        }
			        }

			        @Override
			        public void afterTextChanged (Editable editable)
			        {
			        }
		        }
	        );

            return rootView;
        }
    }

}
