<script lang="ts">
  import {
    MapLibre,
    NavigationControl,
    ScaleControl,
    GeoJSONSource,
    FillLayer,
    LineLayer,
    CustomControl
  } from 'svelte-maplibre-gl';
  import maplibregl, {type MapGeoJSONFeature} from "maplibre-gl";
  import {onMount} from "svelte";

  const style = "https://tiles.openfreemap.org/styles/liberty";

  let parcelsData = $state({type: "FeatureCollection", features: []});
  let map: maplibregl.Map | undefined = $state();
  let hoveredId: string | number | null = $state(null);
  import LucideCloudDownload from '~icons/lucide/cloud-download'

  const loadParcels = async () => {
    if (!map || map.getZoom() < 14) return;

    const b = map.getBounds();
    const qs = new URLSearchParams({
      minLon: b.getWest().toString(10),
      minLat: b.getSouth().toString(10),
      maxLon: b.getEast().toString(10),
      maxLat: b.getNorth().toString(10)
    });
    // todo remote function
    const res = await fetch(`http://localhost:8080/admin/parcels?${qs}`);
    parcelsData = await res.json();   // reassignment -> source updates itself
  }

  const ingestZone = async () => {
    if (!map || map.getZoom() < 14) return;

    const b = map.getBounds();
    const bbox = {
      minLon: b.getWest(), minLat: b.getSouth(),
      maxLon: b.getEast(), maxLat: b.getNorth()
    }

    const res = await fetch(`http://localhost:8080/admin/ingest`, {
      method: 'POST',
      body: JSON.stringify(bbox),
      headers: {"Content-Type": "application/json"}
    });

    console.log(`Ingested ${await res.text()} parcels`);
    await loadParcels()
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
  <CustomControl>
    <button
      onclick={ingestZone}
      class="flex! items-center justify-center text-gray-900"
    >
      <LucideCloudDownload class="w-5"/>
    </button>
  </CustomControl>

  <NavigationControl/>
  <ScaleControl/>
  <!--  <GlobeControl/>-->
  <GeoJSONSource id="parcels" data={parcelsData} promoteId="id" attribution="Agenzia delle Entrate">
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