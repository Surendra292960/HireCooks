package com.test.sample.hirecooks.Adapter.ManageAddress;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.test.sample.hirecooks.Activity.ManageAddress.SearchAddress;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.SharedPrefManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PlacesAutoCompleteAdapter extends RecyclerView.Adapter<PlacesAutoCompleteAdapter.PredictionHolder> implements Filterable {
    private static final String TAG = "PlacesAutoAdapter";
    private ArrayList<PlaceAutocomplete> mResultList = new ArrayList<>();
    private Context mContext;
    private CharacterStyle STYLE_BOLD;
    private CharacterStyle STYLE_NORMAL;
    private PlacesClient placesClient;
    private ClickListener clickListener;
    private String pinCode;

    public PlacesAutoCompleteAdapter(Context context) {
        mContext = context;
        STYLE_BOLD = new StyleSpan(Typeface.BOLD);
        STYLE_NORMAL = new StyleSpan(Typeface.NORMAL);
        placesClient = com.google.android.libraries.places.api.Places.createClient(context);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void click(Place place);
    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList = getPredictions(constraint);
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    //notifyDataSetInvalidated();
                }
            }
        };
    }


    private ArrayList<PlaceAutocomplete> getPredictions(CharSequence constraint) {

        final ArrayList<PlaceAutocomplete> resultList = new ArrayList<>();

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        //https://gist.github.com/graydon/11198540
        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                //.setLocationBias(bounds)
                //.setCountry("BD")
                //.setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(constraint.toString())
                .build();

        Task<FindAutocompletePredictionsResponse> autocompletePredictions = placesClient.findAutocompletePredictions(request);

        // This method should have been called off the main UI thread. Block and wait for at most
        // 60s for a result from the API.
        try {
            Tasks.await(autocompletePredictions, 60, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }

        if (autocompletePredictions.isSuccessful()) {
            FindAutocompletePredictionsResponse findAutocompletePredictionsResponse = autocompletePredictions.getResult();
            if (findAutocompletePredictionsResponse != null)
                for (AutocompletePrediction prediction : findAutocompletePredictionsResponse.getAutocompletePredictions()) {
                    Log.i(TAG, prediction.getPlaceId());
                    resultList.add(new PlaceAutocomplete(prediction.getPlaceId(), prediction.getPrimaryText(STYLE_NORMAL).toString(), prediction.getFullText(STYLE_BOLD).toString()));
                }

            return resultList;
        } else {
            return resultList;
        }

    }

    @NonNull
    @Override
    public PredictionHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = layoutInflater.inflate(R.layout.recyclerview_search_address, viewGroup, false);
        return new PredictionHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionHolder mPredictionHolder, final int i) {
        mPredictionHolder.mAddress.setText(mResultList.get(i).address);
        mPredictionHolder.mArea.setText(mResultList.get(i).area);
    }

    @Override
    public int getItemCount() {
        return mResultList==null?0:mResultList.size();
    }

    public PlaceAutocomplete getItem(int position) {
        return mResultList.get(position);
    }

    public class PredictionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mAddress, mArea;
        private LinearLayout mRow;

        PredictionHolder(View itemView) {
            super(itemView);
            mArea = itemView.findViewById(R.id.area);
            mAddress = itemView.findViewById(R.id.address);
            mRow = itemView.findViewById(R.id.place_item_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            PlaceAutocomplete item = mResultList.get(getAdapterPosition());
            if (v.getId() == R.id.place_item_view) {

                String placeId = String.valueOf(item.placeId);

                List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.PLUS_CODE);
                FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
                placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse response) {
                        Place place = response.getPlace();
                        clickListener.click(place);

                        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                        try {
                            List<android.location.Address> addressList = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                            if (addressList != null && addressList.size() > 0) {
                                android.location.Address address = addressList.get(0);
                                pinCode = address.getPostalCode();
                            }
                        } catch (IOException e) {
                           e.printStackTrace();
                        }

                     try{
                         Map address = new Map();
                         address.setAddress(String.valueOf(mResultList.get(getAdapterPosition()).address));
                         address.setPincode(Integer.parseInt(pinCode));
                         address.setSubAddress(String.valueOf(mResultList.get(getAdapterPosition()).area));
                         if(!SharedPrefManager.getInstance(mContext).getUser().getFirmId().equalsIgnoreCase("Not_Available")){
                             address.setFirm_id(SharedPrefManager.getInstance(mContext).getUser().getFirmId());
                         }else{
                             address.setFirm_id(SharedPrefManager.getInstance(mContext).getUser().getFirmId());
                         }
                         address.setLatitude(String.valueOf(place.getLatLng().latitude));
                         address.setLongitude(String.valueOf(place.getLatLng().longitude));
                         address.setPlaceId(placeId);
                         address.setUserId(SharedPrefManager.getInstance(mContext).getUser().getId());
                         ///////////////////////////////
                         Bundle bundle = new Bundle();
                         Intent intent = new Intent(mContext, SearchAddress.class);
                         bundle.putSerializable("address_book", address);
                         intent.putExtras(bundle);
                         mContext.startActivity(intent .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                         ((Activity)mContext).finish();
                     }catch (Exception e){
                         e.printStackTrace();
                     }
                        //////////////////////////////
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        if (exception instanceof ApiException) {
                            Toast.makeText(mContext, exception.getMessage() + "", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    /**
     * Holder for Places Geo Data Autocomplete API results.
     */
    public class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence address, area;

        PlaceAutocomplete(CharSequence placeId, CharSequence area, CharSequence address) {
            this.placeId = placeId;
            this.area = area;
            this.address = address;
        }

        @Override
        public String toString() {
            return area.toString();
        }
    }
}