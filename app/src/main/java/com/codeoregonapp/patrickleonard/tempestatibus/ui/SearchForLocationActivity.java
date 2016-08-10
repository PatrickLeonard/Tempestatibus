package com.codeoregonapp.patrickleonard.tempestatibus.ui;


import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.TempestatibusApplicationSettings;
import com.codeoregonapp.patrickleonard.tempestatibus.adapters.LocationAdapter;
import com.codeoregonapp.patrickleonard.tempestatibus.adapters.UnfilteredArrayAdapter;
import com.codeoregonapp.patrickleonard.tempestatibus.database.CachedLocationDataSource;
import com.codeoregonapp.patrickleonard.tempestatibus.database.SavedLocationModel;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.addressFromNameUtils.SearchedAddressFetchConstants;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.addressFromNameUtils.SearchedAddressFetchIntentService;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.addressFromNameUtils.SearchedAddressResultReceiver;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchForLocationActivity extends AppCompatActivity {
    public static final String TAG = SearchForLocationActivity.class.getSimpleName();
    public static final String CURRENT_LOCATION_EXTRA = "CURRENT_LOCATION";
    public static final String CURRENT_STANDARD_ADDRESS_EXTRA = "CURRENT_STANDARD_ADDRESS";
    public static final String CURRENT_SHORTENED_ADDRESS_EXTRA = "CURRENT_SHORTENED_ADDRESS";
    private CachedLocationDataSource mCachedLocationDataSource;
    private ArrayList<Address> mAddresses;
    private ArrayList<String> mDisplayStrings;
    private ArrayList<SavedLocationModel> mSavedLocationModels;
    private UnfilteredArrayAdapter mUnfilteredArrayAdapter;
    private Location mCurrentLocation;
    private String mCurrentStandardAddress;
    private String mCurrentShortenedAddress;
    private String mUserSavedName;

    public CachedLocationDataSource getLocationDataSource() {
        return mCachedLocationDataSource;
    }

    public void setLocationDataSource(CachedLocationDataSource cachedLocationDataSource) {
        mCachedLocationDataSource = cachedLocationDataSource;
    }

    public ArrayList<String> getDisplayStrings() {
        return mDisplayStrings;
    }

    public void setDisplayStrings(ArrayList<String> displayStrings) {
        mDisplayStrings = displayStrings;
    }

    public ArrayList<SavedLocationModel> getSavedLocationModels() {
        return mSavedLocationModels;
    }

    public void setSavedLocationModels(ArrayList<SavedLocationModel> savedLocationModels) {
        mSavedLocationModels = savedLocationModels;
    }

    public String getUserSavedName() {
        return mUserSavedName;
    }

    public void setUserSavedName(String userSavedName) {
        mUserSavedName = userSavedName;
    }

    public UnfilteredArrayAdapter getUnfilteredArrayAdapter() {
        return mUnfilteredArrayAdapter;
    }

    public void setUnfilteredArrayAdapter(UnfilteredArrayAdapter unfilteredArrayAdapter) {
        mUnfilteredArrayAdapter = unfilteredArrayAdapter;
    }

    public AutoCompleteTextView getAutoCompleteTextView() {
        return mAutoCompleteTextView;
    }

    public ListView getListView() {
        return mListView;
    }

    public ArrayList<Address> getAddresses() {
        return mAddresses;
    }

    public void setAddresses(ArrayList<Address> addresses) {
        mAddresses = addresses;
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        mCurrentLocation = currentLocation;
    }

    public String getCurrentStandardAddress() {
        return mCurrentStandardAddress;
    }

    public void setCurrentStandardAddress(String currentStandardAddress) {
        mCurrentStandardAddress = currentStandardAddress;
    }

    public String getCurrentShortenedAddress() {
        return mCurrentShortenedAddress;
    }

    public void setCurrentShortenedAddress(String currentShortenedAddress) {
        mCurrentShortenedAddress = currentShortenedAddress;
    }
    
    @Bind(R.id.autoCompleteTextView)
    AutoCompleteTextView mAutoCompleteTextView;
    @Bind(android.R.id.list)
    ListView mListView;
    @Bind(R.id.poweredByForecast)
    TextView mPoweredByForecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsActivity.setActivityTheme(this);
        super.onCreate(savedInstanceState);
        //Set the theme, content view, and use ButterKnife to Bind the recycler view
        setContentView(R.layout.search_for_location_activity);
        ButterKnife.bind(this);
        setCurrentData();
        configureSavedLocationsListView();
        configureAutoCompleteTextView();
        mPoweredByForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create an intent to open the default browser and travel to http://forecast.io
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.forecast_io_url)));
                startActivity(browserIntent);
            }
        });

    }

    private void setCurrentData() {
        Intent intent = getIntent();
        setCurrentLocation((Location) intent.getParcelableExtra(SearchForLocationActivity.CURRENT_LOCATION_EXTRA));
        setCurrentStandardAddress(intent.getStringExtra(SearchForLocationActivity.CURRENT_STANDARD_ADDRESS_EXTRA));
        setCurrentShortenedAddress(intent.getStringExtra(SearchForLocationActivity.CURRENT_SHORTENED_ADDRESS_EXTRA));
    }

    private void configureSavedLocationsListView() {
        setLocationDataSource(new CachedLocationDataSource(this));
        setSavedLocationModels(getLocationDataSource().readLocationsWrapper());
        getListView().setAdapter(new LocationAdapter(this, getSavedLocationModels()));
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startSearchedLocationActivity(getSavedLocationModels().get(position));
            }
        });
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                createLocationActionChoiceDialog(position);
                return true;
            }
        });
    }

    private void createLocationActionChoiceDialog(int position) {
        final int mPosition = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchForLocationActivity.this);
        builder.setTitle("Delete or Rename")
                .setMessage("Would you like to delete or rename the saved location?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                createDeleteLocationDialog(mPosition);
                            }
                        });
                    }
                })
                .setNegativeButton("Rename", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        createRenameLocationDialog(mPosition);
                    }
                });
        builder.create().show();
    }

    private void createDeleteLocationDialog(int position) {
        final int mPosition = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchForLocationActivity.this);
        builder.setTitle("Delete Location")
                .setMessage("Are you sure you want to delete the location?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                deleteSavedLocation(mPosition);
                                Toast.makeText(SearchForLocationActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(SearchForLocationActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.create().show();
    }

    private void createRenameLocationDialog(int position) {
        final int mPosition = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this,TempestatibusApplicationSettings.getAlertDialogThemeId());
        //Set up the title
        builder.setTitle("Rename Location");
        //Set up the input EditText
        final EditText input = (EditText)getLayoutInflater().inflate(R.layout.save_location_dialog_edit_text,null);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SearchForLocationActivity.this.setUserSavedName(input.getText().toString());
                SearchForLocationActivity.this.renameLocation(mPosition);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(SearchForLocationActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).
                setTextColor(ContextCompat.getColor(SearchForLocationActivity.this,
                TempestatibusApplicationSettings.getTextColorId()));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).
                setTextColor(ContextCompat.getColor(SearchForLocationActivity.this,
                TempestatibusApplicationSettings.getTextColorId()));
    }

    private void renameLocation(int position) {
        int locationId = getSavedLocationModels().get(position).getId();
        getLocationDataSource().updateLocation(locationId,getUserSavedName());
        getSavedLocationModels().get(position).setName(getUserSavedName());
        LocationAdapter locationAdapter = (LocationAdapter)getListView().getAdapter();
        locationAdapter.notifyDataSetChanged();
    }

    private void deleteSavedLocation(int position) {
        int locationId = getSavedLocationModels().get(position).getId();
        getLocationDataSource().deleteLocation(locationId);
        getSavedLocationModels().remove(position);
        LocationAdapter locationAdapter = (LocationAdapter)getListView().getAdapter();
        locationAdapter.notifyDataSetChanged();
    }

    private void configureAutoCompleteTextView() {
        setDisplayStrings(new ArrayList<String>());
        getDisplayStrings().add(getCurrentStandardAddress());
        setUnfilteredArrayAdapter(new UnfilteredArrayAdapter
                (this, R.layout.powered_by_google_item, getDisplayStrings()));
        getAutoCompleteTextView().setThreshold(1);
        getAutoCompleteTextView().setTextColor(ContextCompat.getColor(this,TempestatibusApplicationSettings.getTextColorId()));
        getAutoCompleteTextView().setDropDownBackgroundResource(TempestatibusApplicationSettings.getBackgroundDrawableId());
        getAutoCompleteTextView().setAdapter(getUnfilteredArrayAdapter());
        getAutoCompleteTextView().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                startSearchedAddressFetchIntentService(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        getAutoCompleteTextView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startSearchedLocationActivity(createSavedLocationModelFromSearchedLocation(position,getDisplayStrings().get(position)));
            }
        });
    }

    private void startSearchedLocationActivity(SavedLocationModel savedLocationModel) {
        Intent intent = new Intent(SearchForLocationActivity.this,SearchedLocationActivity.class);
        intent.putExtra(SearchedLocationActivity.SEARCHED_STANDARD_ADDRESS_EXTRA, savedLocationModel.getStandardAddress());
        intent.putExtra(SearchedLocationActivity.SEARCHED_SHORTENED_ADDRESS_EXTRA, savedLocationModel.getShortenedAddress());
        intent.putExtra(SearchedLocationActivity.SEARCHED_LOCATION_EXTRA, savedLocationModel.getLocation());
        intent.putExtra(SearchedLocationActivity.CURRENT_STANDARD_ADDRESS_EXTRA, getCurrentStandardAddress());
        intent.putExtra(SearchedLocationActivity.CURRENT_SHORTENED_ADDRESS_EXTRA, getCurrentShortenedAddress());
        intent.putExtra(SearchedLocationActivity.CURRENT_LOCATION_EXTRA, getCurrentLocation());
        startActivity(intent);
        finish();
    }

    private void startSearchedAddressFetchIntentService(String inputText) {
        Intent intent = new Intent(this, SearchedAddressFetchIntentService.class);
        SearchedAddressResultReceiver searchedAddressResultReceiver = new SearchedAddressResultReceiver(new Handler(),this);
        intent.putExtra(SearchedAddressFetchConstants.ENTERED_TEXT_KEY,inputText);
        intent.putExtra((SearchedAddressFetchConstants.RECEIVER), searchedAddressResultReceiver);
        startService(intent);
    }

    public void clearDisplayStringsAndAdapter() {
        getDisplayStrings().clear();
        getUnfilteredArrayAdapter().clear();
    }

    public void addResultsToAdapter() {
        String displayLine;
        ArrayList<Address> addresses = getAddresses();
        if(addresses != null) {
            for (Address address : getAddresses()) {
                displayLine = "";
                for (int i = 0; i < address.getMaxAddressLineIndex(); ++i) {
                    displayLine += address.getAddressLine(i);
                    displayLine += " ";
                }
                getDisplayStrings().add(displayLine);
            }
        }
    }

    public void addCurrentAndNotify() {
        getDisplayStrings().add(getCurrentStandardAddress());
        getUnfilteredArrayAdapter().addAll(getDisplayStrings());
        getUnfilteredArrayAdapter().notifyDataSetChanged();
    }


    private SavedLocationModel createSavedLocationModelFromSearchedLocation(int itemPosition,String displayString) {
        Location location = new Location(LocationManager.PASSIVE_PROVIDER);
        if(displayString.equals(getCurrentStandardAddress())) {
            location.setLatitude(getCurrentLocation().getLatitude());
            location.setLongitude(getCurrentLocation().getLongitude());
        }
        else {
            location.setLatitude(getAddresses().get(itemPosition).getLatitude());
            location.setLongitude(getAddresses().get(itemPosition).getLongitude());
        }
        return new SavedLocationModel(location,-2,null,displayString,displayString);
    }

    private String createStandardAddressString(Address address) {
        String standardAddress = "";
        for (int i=0;i<=address.getMaxAddressLineIndex();++i) {
            standardAddress += address.getAddressLine(i);
            standardAddress += " ";
        }
        return standardAddress;
    }

    private String createShortenedAddressString(Address address) {
        String shortenedAddress = "";
        for (int j=1;j<=address.getMaxAddressLineIndex();++j) {
            shortenedAddress += address.getAddressLine(j);
            shortenedAddress += " ";
        }
        return shortenedAddress;
    }
    //Keep up with theme changes on resume
    @Override
    protected void onResume() {
        SettingsActivity.setActivityTheme(this);
        super.onResume();
        LocationAdapter locationAdapter = (LocationAdapter)getListView().getAdapter();
        locationAdapter.notifyDataSetChanged();
    }
}