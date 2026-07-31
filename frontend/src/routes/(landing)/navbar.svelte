<script lang="ts">
  import * as NavigationMenu from "$lib/components/ui/navigation-menu";
  import {navigationMenuTriggerStyle} from "$lib/components/ui/navigation-menu/navigation-menu-trigger.svelte";
  import {Button} from "$lib/components/ui/button";
  import {cn} from "$lib/utils.js";

  // Icone
  import LucideMapPin from '~icons/lucide/map-pin'
  import LucideShieldCheck from '~icons/lucide/shield-check'
  import LucideNetwork from '~icons/lucide/network'
  import type {Component} from "svelte";
  import type {SvelteHTMLElements} from "svelte/elements";

  import Logo from "$lib/assets/logo.svelte";
  import hero_img from '$lib/assets/hero_img.jpg'
  import chisiamo from '$lib/assets/IMG_20210503_120138.jpg'
  import mappa from '$lib/assets/piemonte_mappa_placeholder.jpg'

  const links = [
    {label: "Chi siamo", href: "#chi-siamo"},
    {label: "La rete", href: "#come-funziona"},
    {label: "Boschi", href: "#galleria"},
    {label: "Novità", href: "#novita"},
  ];
</script>

<nav class="fixed inset-x-0 top-4 z-50 flex justify-center px-4">
  <div class="flex w-full max-w-280 items-center justify-between gap-6
              rounded-full border bg-background/80 py-2 pr-2 pl-5 shadow-lg backdrop-blur-md">
    <!-- logoId: plain anchor, deliberately outside NavigationMenu -->
    <a href="#top" class="flex items-center gap-2.5 text-foreground">
      <Logo/>
      <span class="text-[15px] font-bold leading-[1.05] tracking-[-0.02em]">
        Rete dei Boschi<br/><span class="text-primary">Custoditi</span>
      </span>
    </a>

    <div class="flex items-center gap-1">
      <NavigationMenu.Root viewport={false}>
        <NavigationMenu.List class="gap-1">
          {#each links as link}
            <NavigationMenu.Item>
              <NavigationMenu.Link>
                {#snippet child()}
                  <a href={link.href}
                     class={cn(navigationMenuTriggerStyle(),
                       "rounded-full bg-transparent text-muted-foreground hover:text-foreground")}>
                    {link.label}
                  </a>
                {/snippet}
              </NavigationMenu.Link>
            </NavigationMenu.Item>
          {/each}
        </NavigationMenu.List>
      </NavigationMenu.Root>

      <Button href="#manifesto" class="ml-2 rounded-full font-semibold">Scopri il Manifesto</Button>
    </div>
  </div>
</nav>