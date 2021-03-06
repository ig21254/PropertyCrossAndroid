package com.lasalle.second.part.propertycross.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.lasalle.second.part.propertycross.PropertyCrossApplication;
import com.lasalle.second.part.propertycross.R;
import com.lasalle.second.part.propertycross.activities.PropertyDetailsActivity;
import com.lasalle.second.part.propertycross.model.Property;
import com.lasalle.second.part.propertycross.model.PropertyClusterItem;
import com.lasalle.second.part.propertycross.services.ApplicationServiceFactory;
import com.lasalle.second.part.propertycross.services.PropertyService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapViewAdapter extends DefaultClusterRenderer<PropertyClusterItem>
        implements GoogleMap.InfoWindowAdapter, ClusterManager.OnClusterItemInfoWindowClickListener<PropertyClusterItem> {

    private HashMap<String, Property> propertyMap;
    private LayoutInflater layoutInflater;
    private Activity ownerActivity;
    private GoogleMap map;
    private ClusterManager<PropertyClusterItem> clusterManager;

    public MapViewAdapter(LayoutInflater layoutInflater, Activity ownerActivity,
                          GoogleMap map, ClusterManager<PropertyClusterItem> clusterManager) {

        super(ownerActivity.getApplicationContext(), map, clusterManager);

        this.propertyMap = new HashMap<>();
        this.layoutInflater = layoutInflater;
        this.ownerActivity = ownerActivity;
    }

    @Override
    protected void onClusterItemRendered(PropertyClusterItem clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);

        propertyMap.put(marker.getId(), clusterItem.getProperty());
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = layoutInflater.inflate(R.layout.search_result_item, null);

        Property property = propertyMap.get(marker.getId());

        /*TextView title = (TextView) view.findViewById(R.id.search_result_title);
        title.setText(property.getName());

        TextView subtitle = (TextView) view.findViewById(R.id.search_result_subtitle);
        title.setText(property.getName());*/

        setViewImage(view);
        setViewTitle(view, property);
        setViewSubtitle(view, property);
        setViewPrice(view, property);

        return view;
    }

    protected void setViewImage(View rowView) {
        ImageView imageView = (ImageView) rowView.findViewById(R.id.search_result_image);
        switch ((int)(Math.random()*10) % 4) {
            case 0:
                imageView.setImageResource(R.drawable.flat_sample_image);
                break;
            case 1:
                imageView.setImageResource(R.drawable.flat_sample_image_2);
                break;
            case 2:
                imageView.setImageResource(R.drawable.flat_sample_image_3);
            case 3:
                imageView.setImageResource(R.drawable.flat_sample_image_4);
                break;
        }
    }

    protected void setViewTitle(View rowView, Property property) {
        TextView titleTextView = (TextView) rowView.findViewById(R.id.search_result_title);
        titleTextView.setText(property.getName());
    }

    protected void setViewSubtitle(View rowView, Property property) {
        TextView subtitleTextView = (TextView) rowView.findViewById(R.id.search_result_subtitle);

        List<Object> objectList = new ArrayList<>();
        objectList.add(property.getSquareFootage());
        String formattedString;
        if (property.isRent()) {
            formattedString = String.format(ownerActivity.getString(R.string.results_subtitle_text_rent),
                    objectList.toArray());
        } else {
            formattedString = String.format(ownerActivity.getString(R.string.results_subtitle_text_sale),
                    objectList.toArray());
        }

        subtitleTextView.setText(formattedString);
    }

    protected void setViewPrice(View rowView, Property property) {
        TextView subtitleTextView = (TextView) rowView.findViewById(R.id.search_result_price);

        List<Object> objectList = new ArrayList<>();
        objectList.add(property.getPrice());

        String formattedString = String.format(ownerActivity.getString(R.string.results_price),
                objectList.toArray());
        subtitleTextView.setText(formattedString);
    }


    @Override
    public void onClusterItemInfoWindowClick(PropertyClusterItem propertyClusterItem) {
        String idPropiedad = propertyClusterItem.getProperty().getId();
        String[] parser = idPropiedad.split("-");
        new AsyncDetails().execute(Integer.parseInt(parser[2]));
    }

    private class AsyncDetails extends AsyncTask<Integer, Void, Boolean> {

        private Context context;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.context = PropertyCrossApplication.getContext();
            View view = layoutInflater.inflate(R.layout.fragment_search_result_map, null);
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.map_progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            PropertyService propertyService = ApplicationServiceFactory.
                    getInstance(context).getPropertyService();

            return propertyService.searchPropertyDetailsCachingResult(integers[0]) != null;
        }

        @Override
        protected void onPostExecute(Boolean hasResults) {
            super.onPostExecute(hasResults);
            if(!isCancelled()) {
                if(!hasResults) {
                    Toast toast = Toast.makeText(
                            context,
                            context.getString(R.string.no_results_found),
                            Toast.LENGTH_LONG);
                    toast.show();
                }
                else {
                    Intent intent = new Intent(context, PropertyDetailsActivity.class);
                    ownerActivity.startActivity(intent);
                }
            }
        }
    }
}
