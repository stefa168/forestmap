<script lang="ts">
  import {MapLibre, NavigationControl, ScaleControl, GeoJSONSource, FillLayer, LineLayer} from 'svelte-maplibre-gl';
  import maplibregl, {type MapGeoJSONFeature} from "maplibre-gl";
  import {onMount} from "svelte";

  const style = "https://tiles.openfreemap.org/styles/liberty";

  let parcelsData = $state({type: "FeatureCollection", features: []});
  let map: maplibregl.Map | undefined = $state();
  let hoveredId: string | number | null = $state(null);

  const loadParcels = async () => {
    if (!map || map.getZoom() < 14) return;

    const b = map.getBounds();
    const qs = new URLSearchParams({
      minLon: b.getWest(), minLat: b.getSouth(),
      maxLon: b.getEast(), maxLat: b.getNorth()
    });
    // todo remote function
    const res = await fetch(`http://localhost:8080/admin/parcels?${qs}`);
    parcelsData = await res.json();   // reassignment -> source updates itself
  }

  const setHoveredParcel = (id: string | number | null) => {
    if (!map) return

    // clear the old one
    if (hoveredId !== null) {
      map.setFeatureState({source: 'parcels', id: hoveredId}, {hover: false});
    }

    hoveredId = id;
    if (id !== null) {
      map.setFeatureState({source: 'parcels', id}, {hover: true});
    }
  }

  // todo load function
  onMount(() => loadParcels())
</script>

<MapLibre
    bind:map
    class="h-200"
    {style}
    zoom={14}
    center={{ lng: 8.00454, lat: 44.95358 }}
    onmoveend={loadParcels}
>
  <NavigationControl/>
  <ScaleControl/>
  <!--  <GlobeControl/>-->
  <GeoJSONSource id="parcels" data={parcelsData} promoteId="id">
    <FillLayer
        paint={{
        'fill-color': [
            'case',
            ['boolean', ['feature-state', 'hover'], false],
            '#4caf50',   // hovered
            '#88c9a1'    // default
          ],
          'fill-opacity': 0.45
        }}
        onmousemove={(e) => {
          const f = e.features?.[0] as MapGeoJSONFeature | undefined;
          if(f){
            setHoveredParcel(f.id??null);
            if(map) map.getCanvas().style.cursor = 'pointer'
          }
        }}
        onmouseleave={() => {
          setHoveredParcel(null);
          if(map) map.getCanvas().style.cursor = '';
        }}
        onclick={(e) => {
          console.log("Clicked on feature", e.features?.[0].properties)
        }}
    />
    <LineLayer paint={{ 'line-color': '#2e7d32', 'line-width': 1 }}/>
  </GeoJSONSource>
</MapLibre>