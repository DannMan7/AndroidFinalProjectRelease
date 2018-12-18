package com.example.medwa.androidfinalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

// My Cluster Manager Class
public class MyClusterManager extends DefaultClusterRenderer<StatusMarkers> {

    // Member Variables for My Cluster Manager
    private final IconGenerator iconGenerator;
    private ImageView imageView;
    private final int markerWidth;
    private final int markerHeight;

    // Constructor for My Cluster Manager
    public MyClusterManager(Context context, GoogleMap map, com.google.maps.android.clustering.ClusterManager<StatusMarkers> clusterManager) {
        super(context, map, clusterManager);
        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        markerWidth = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        markerHeight = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
        int padding = (int) context.getResources().getDimension(R.dimen.custom_marker_padding);
        imageView.setPadding(padding,padding,padding,padding);
        iconGenerator.setContentView(imageView);
    }
    // onBeforeClusterItemRendered Call
    @Override
    protected void onBeforeClusterItemRendered(StatusMarkers item, MarkerOptions markerOptions) {

        imageView.setImageResource(item.getIconImage());
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
    }
    // shouldRenderAsCluster Called
    @Override
    protected boolean shouldRenderAsCluster(Cluster<StatusMarkers> cluster) {
        return false;
    }
}
