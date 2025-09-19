	package ui;
	
	import javafx.geometry.Insets;
	import javafx.scene.control.*;
	import javafx.scene.layout.GridPane;
	import javafx.scene.layout.VBox;
	import javafx.scene.layout.HBox;   // or VBox / StackPane
	import javafx.scene.canvas.Canvas;
	import logic.mapping.MapTileManager;
	
	/**
	 * 🧭 CacheManagerPanel - Manages map tile caching for offline operation.
	 * Allows operator to preload regions and clear cached map data.
	 */
	public class CacheManagerPanel extends VBox {
	
	    private final TextField latMinField = new TextField();
	    private final TextField latMaxField = new TextField();
	    private final TextField lonMinField = new TextField();
	    private final TextField lonMaxField = new TextField();
	    private final Button cacheButton = new Button("📦 Cache Selected Region");
	    private final Button clearButton = new Button("🗑 Clear All Cached Data");
	    private final Label statusLabel = new Label("🧭 Ready to cache.");
	
	    public CacheManagerPanel() {
	        setSpacing(10);
	        setPadding(new Insets(10));
	
	        getChildren().addAll(
	            new Label("🧭 Cache Manager - Offline Map Storage"),
	            buildCoordGrid(),
	            new VBox(5, cacheButton, clearButton),
	            statusLabel
	        );
	
	        cacheButton.setOnAction(e -> cacheRegion());
	        clearButton.setOnAction(e -> {
	            MapTileManager.clearCache();
	            statusLabel.setText("🗑 All cached data cleared.");
	        });
	    }
	
	    private GridPane buildCoordGrid() {
	        GridPane grid = new GridPane();
	        grid.setVgap(5);
	        grid.setHgap(10);
	
	        grid.add(new Label("Lat Min:"), 0, 0);
	        grid.add(latMinField, 1, 0);
	        grid.add(new Label("Lat Max:"), 0, 1);
	        grid.add(latMaxField, 1, 1);
	        grid.add(new Label("Lon Min:"), 0, 2);
	        grid.add(lonMinField, 1, 2);
	        grid.add(new Label("Lon Max:"), 0, 3);
	        grid.add(lonMaxField, 1, 3);
	
	        return grid;
	    }
	
	    private void cacheRegion() {
	        try {
	            double latMin = Double.parseDouble(latMinField.getText().trim());
	            double latMax = Double.parseDouble(latMaxField.getText().trim());
	            double lonMin = Double.parseDouble(lonMinField.getText().trim());
	            double lonMax = Double.parseDouble(lonMaxField.getText().trim());
	
	            boolean success = MapTileManager.cacheRegion(latMin, latMax, lonMin, lonMax);
	            statusLabel.setText(success ? "✅ Region cached." : "❌ Caching failed.");
	
	        } catch (Exception e) {
	            statusLabel.setText("❗ Invalid coordinates.");
	        }
	    }
	}
