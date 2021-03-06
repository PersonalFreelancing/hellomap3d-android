package com.nutiteq.advancedmap3;

import android.os.Bundle;

import com.nutiteq.core.MapPos;
import com.nutiteq.core.ScreenPos;
import com.nutiteq.datasources.BitmapOverlayRasterTileDataSource;
import com.nutiteq.graphics.Bitmap;
import com.nutiteq.layers.RasterTileLayer;
import com.nutiteq.layers.TileSubstitutionPolicy;
import com.nutiteq.projections.Projection;
import com.nutiteq.utils.BitmapUtils;
import com.nutiteq.wrappedcommons.MapPosVector;
import com.nutiteq.wrappedcommons.ScreenPosVector;

/*
 * A sample demonstrating how to add ground level raster overlay. This samples
 * adds additional raster layer on top of base layer, using NTBitmapOverlayRasterTileDataSource.
 * Note: for really big overlays (containing 10000 pixels or more), Nutiteq SDK provides
 * GDAL-based raster tile data source. This data source is not part of the standard SDK, it
 * is an extra feature provided using GIS-extensions package.
 */
public class GroundOverlayActivity extends VectorMapSampleBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // MapSampleBaseActivity creates and configures mapView  
        super.onCreate(savedInstanceState);

        Projection proj = mapView.getOptions().getBaseProjection();
        
        // Load ground overlay bitmap
        //Bitmap androidMarkerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.jefferson);
        //com.nutiteq.graphics.Bitmap overlayBitmap = BitmapUtils.createBitmapFromAndroidBitmap(androidMarkerBitmap);
        Bitmap overlayBitmap = BitmapUtils.loadBitmapFromAssets("jefferson-building-ground-floor.jpg", false);
        
        // Create two vectors containing geographical positions and corresponding raster image pixel coordinates.
        // 2, 3 or 4 points may be specified. Usually 2 points are enough (for conformal mapping).
        MapPos pos = proj.fromWgs84(new MapPos(-77.004590, 38.888702));
        double sizeNS = 110, sizeWE = 100;

        MapPosVector mapPoses = new MapPosVector();
        mapPoses.add(new MapPos(pos.getX()-sizeWE, pos.getY()+sizeNS));
        mapPoses.add(new MapPos(pos.getX()+sizeWE, pos.getY()+sizeNS));
        mapPoses.add(new MapPos(pos.getX()+sizeWE, pos.getY()-sizeNS));
        mapPoses.add(new MapPos(pos.getX()-sizeWE, pos.getY()-sizeNS));

        ScreenPosVector bitmapPoses = new ScreenPosVector();
        bitmapPoses.add(new ScreenPos(0, 0));
        bitmapPoses.add(new ScreenPos(0, overlayBitmap.getOrigHeight()));
        bitmapPoses.add(new ScreenPos(overlayBitmap.getOrigWidth(), overlayBitmap.getOrigHeight()));
        bitmapPoses.add(new ScreenPos(overlayBitmap.getOrigWidth(), 0));
        
        // Create bitmap overlay raster tile data source
        BitmapOverlayRasterTileDataSource rasterDataSource = new BitmapOverlayRasterTileDataSource(0, 20, overlayBitmap, proj, mapPoses, bitmapPoses);
        RasterTileLayer rasterLayer = new RasterTileLayer(rasterDataSource);
        mapView.getLayers().add(rasterLayer);
        
        // Apply zoom level bias to the raster layer.
        // By default, bitmaps are upsampled on high-DPI screens.
        // We will correct this by applying appropriate bias
        float zoomLevelBias = (float) (Math.log(mapView.getOptions().getDPI() / 160.0f) / Math.log(2));
        rasterLayer.setZoomLevelBias(zoomLevelBias * 0.75f);
        rasterLayer.setTileSubstitutionPolicy(TileSubstitutionPolicy.TILE_SUBSTITUTION_POLICY_VISIBLE);
        
        mapView.setFocusPos(pos, 0);
        mapView.setZoom(15.5f, 0);
    }
}
