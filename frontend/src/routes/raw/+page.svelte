<script lang="ts">
  import { onMount, onDestroy } from 'svelte';
  import maplibregl from 'maplibre-gl';
  import 'maplibre-gl/dist/maplibre-gl.css';

  const API = 'http://localhost:8080';
  let mapContainer;
  let map;
  let hoveredId = null;

  onMount(() => {
    map = new maplibregl.Map({
      container: mapContainer,
      style: {
        version: 8,
        sources: {
          osm: {
            type: 'raster',
            tiles: ['https://tile.openstreetmap.org/{z}/{x}/{y}.png'],
            tileSize: 256,
            attribution: '© OpenStreetMap contributors'
          }
        },
        layers: [{id: 'osm', type: 'raster', source: 'osm'}]
      },
      center: [8.00454, 44.95358], // <-- your Piedmont test area (lon, lat) 44,95358°  8,00454°
      zoom: 16
    });

    map.on('load', () => {
      // Start empty; we fill it on every move.
      map.addSource('parcels', {
        type: 'geojson',
        data: {type: 'FeatureCollection', features: []},
        promoteId: 'id'          // use each Feature's `id` as the feature-state key
      });

      map.addLayer({
        id: 'parcels-fill',
        type: 'fill',
        source: 'parcels',
        paint: {
          'fill-color': [
            'case',
            ['boolean', ['feature-state', 'hover'], false],
            '#4caf50',            // hovered
            '#88c9a1'             // default
          ],
          'fill-opacity': 0.45
        }
      });

      map.addLayer({
        id: 'parcels-outline',
        type: 'line',
        source: 'parcels',
        paint: {'line-color': '#2e7d32', 'line-width': 1}
      });

      loadParcels();
      map.on('moveend', loadParcels);
      wireInteraction();
    });
  });

  async function loadParcels() {
    if (map.getZoom() < 14) return;         // don't fetch when zoomed far out
    const b = map.getBounds();
    const qs = new URLSearchParams({
      minLon: b.getWest(), minLat: b.getSouth(),
      maxLon: b.getEast(), maxLat: b.getNorth()
    });
    const res = await fetch(`${API}/admin/parcels?${qs}`);
    map.getSource('parcels').setData(await res.json());
  }

  function wireInteraction() {
    map.on('mousemove', 'parcels-fill', (e) => {
      if (!e.features.length) return;
      if (hoveredId !== null)
        map.setFeatureState({source: 'parcels', id: hoveredId}, {hover: false});
      hoveredId = e.features[0].id;
      map.setFeatureState({source: 'parcels', id: hoveredId}, {hover: true});
      map.getCanvas().style.cursor = 'pointer';
    });

    map.on('mouseleave', 'parcels-fill', () => {
      if (hoveredId !== null)
        map.setFeatureState({source: 'parcels', id: hoveredId}, {hover: false});
      hoveredId = null;
      map.getCanvas().style.cursor = '';
    });

    map.on('click', 'parcels-fill', (e) => {
      console.log('clicked parcel', e.features[0].properties);
    });
  }

  onDestroy(() => map?.remove());
</script>

<div bind:this={mapContainer} class="map"></div>

<style>
  .map {
    width: 100%;
    height: 100vh;
  }
</style>