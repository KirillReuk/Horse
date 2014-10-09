package com.example.horse;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity{

	private static final int maxCount = 1000;
	
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	private ListView leftDrawerList;
	private ListView rightDrawerList;
	private ListView phrasesDrawerList;
	private MediaPlayer mediaPlayer;
	private String[] rawList;
	private String[] phrasesAddressList;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        
        leftDrawerList = (ListView) findViewById(R.id.left_drawer);
        initListView(leftDrawerList, R.array.classes_array);
        leftDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        leftDrawerList.setItemChecked(0, true);
        
        rightDrawerList = (ListView) findViewById(R.id.right_drawer);
        initListView(rightDrawerList, R.array.commands_array);
        rightDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        rightDrawerList.setItemChecked(0, true);
        
        phrasesDrawerList = (ListView) findViewById(R.id.phrases_list_view);
        phrasesDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        drawerToggle = new ActionBarDrawerToggle(
        		this,
        		drawerLayout,
        		R.drawable.ic_launcher,
        		R.string.drawer_open,
        		R.string.drawer_close)
        {
	        public void onDrawerClosed(View view) {
	            super.onDrawerClosed(view);
	            getActionBar().setTitle(R.string.app_name);
	        }
	
	        public void onDrawerOpened(View drawerView) {
	            super.onDrawerOpened(drawerView);
	            getActionBar().setTitle(R.string.drawer_name);
	        }
        };
        
        drawerLayout.setDrawerListener(drawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        Resources res = getResources();
        TypedArray rawTypedArray = res.obtainTypedArray(R.array.phrases_array);
        rawList = new String[rawTypedArray.length()];
        for (int i=0;i<rawTypedArray.length();++i)
        {
        	rawList[i] = rawTypedArray.getString(i);
        }
        rawTypedArray.recycle();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	//ToastThat(getResources().getResourceEntryName(parent.getId()));
            selectItem(position, parent);
        }
    }
    
    private void selectItem(int position, View view) {
    	if (view == findViewById(R.id.phrases_list_view))
    	{
    		playPhrase(view, position);
    	}
    	else
    	{
    		ListView mDrawerList = (ListView) view;
	    	
	        mDrawerList.setItemChecked(position, true);
	        drawerLayout.closeDrawer(mDrawerList);
	        refreshPhrasesList();
    	}
    }
    
    private void refreshPhrasesList()
    {
    	Resources res = getResources();
    	int classId = leftDrawerList.getCheckedItemPosition();
    	int commandId = rightDrawerList.getCheckedItemPosition();
    	
    	TypedArray typedArrayClasses = res.obtainTypedArray(R.array.classes_array);
    	TypedArray typedArrayCommands = res.obtainTypedArray(R.array.commands_array);
    	String className = typedArrayClasses.getString(classId).toLowerCase(Locale.getDefault());
    	String commandName = typedArrayCommands.getString(commandId).toLowerCase(Locale.getDefault());
    	typedArrayClasses.recycle();
    	typedArrayCommands.recycle();
    	
    	ArrayList<String> phraseList = new ArrayList<String>();
    	String comparingPrefix = className;
    	
    	phrasesAddressList = new String[maxCount];
    	
    	if (commandId > 0)
    	{
    		comparingPrefix += "_" + commandName;
    	}    	
    	
        int index = 0;
        for (String str : rawList)
        {
        	if (str.startsWith(comparingPrefix))
        	{
        		String[] buffer = str.split("_");
        		phrasesAddressList[index] = buffer[0]+"_"+buffer[1]+buffer[3];
        		phraseList.add(buffer[2]);
        		++index;
        	}
        }
        initListView(phrasesDrawerList, phraseList.toArray(new String[phraseList.size()]));
    }
    
    private void initListView(ListView listView, int arrayid)
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,arrayid,android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
    }
    private void initListView(ListView listView, String[] array)
    {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,android.R.layout.simple_list_item_1,array);
        listView.setAdapter(adapter);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) 
        {
            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) 
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void playPhrase(View view, int position){
    	String s = phrasesAddressList[position];
    	playSound(view, getResources().getIdentifier(s, "raw", getPackageName()));    
    }

    private void playSound(View view, int srcid) {
    	mediaPlayer = MediaPlayer.create(this, srcid);
    	mediaPlayer.start();
    }
    /*
    private TypedArray getTypedFromTyped(TypedArray ta, long x)    {
    	Resources res = getResources();
        int huid = ta.getResourceId((int)x, 0);
        return res.obtainTypedArray(huid);
    }
    */
    private void ToastThat(CharSequence text){
    	Context context = getApplicationContext();
    	int duration = Toast.LENGTH_SHORT;

    	Toast toast = Toast.makeText(context, text, duration);
    	toast.show();
    }
    
}