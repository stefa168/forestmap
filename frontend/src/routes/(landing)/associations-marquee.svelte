<script lang="ts">
  import * as Card from "$lib/components/ui/card/index.js";
  import * as Carousel from "$lib/components/ui/carousel/index.js";
  import type {CarouselOptions, CarouselPlugins} from "$lib/components/ui/carousel/context";
  import AutoScroll from "embla-carousel-auto-scroll";

  const images = import.meta.glob('$lib/assets/logos/associations/*.{jpg,png,webp,avif}', {
    eager: true,
    query: '?url',
    import: 'default'
  });
  const urls = Object.values(images); // string[] of resolved URLs
  const a = urls.map((url, i) => ({id: String(i), nome: '...', imageUrl: url}))

  const options: CarouselOptions = $state({
    align: "center",
    loop: true,

  });

  const plugins: CarouselPlugins = $state([
    AutoScroll({
      speed: 1.3
    })
  ]);

</script>

<div class="flex flex-col items-center">
  <Carousel.Root
    opts={options}
    {plugins}
    class="w-full fade-edges"
  >
    <Carousel.Content>
      {#each a as i}
        <Carousel.Item class="basis-auto mx-4 fill-white">
          <img class="max-h-32 rounded-lg" alt="" src={i.imageUrl}>
        </Carousel.Item>
      {/each}
    </Carousel.Content>
    <Carousel.Previous/>
    <Carousel.Next/>
  </Carousel.Root>
</div>