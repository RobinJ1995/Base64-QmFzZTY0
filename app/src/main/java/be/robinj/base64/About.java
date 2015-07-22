package be.robinj.base64;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class About extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

	public void btnDev_clicked (View view)
	{
		Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse ("http://www.robinj.be/"));
		this.startActivity (intent);
	}

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment()
        {
        }

        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View rootView = inflater.inflate (R.layout.fragment_about, container, false);

	        TextView version = (TextView) rootView.findViewById (R.id.version);
	        String v = "";

	        try
	        {
		        PackageInfo pkgInfo = this.getActivity ().getPackageManager ().getPackageInfo (this.getActivity ().getPackageName (), 0);
		        v = "v" + pkgInfo.versionName;
	        }
	        catch (PackageManager.NameNotFoundException e)
	        {
		        v = "{" + e.toString () + "}";
	        }

	        version.setText (v);

            return rootView;
        }
    }

}
