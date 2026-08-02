<script lang="ts">
  import {cn} from "$lib/utils";
  import type {HTMLAttributes} from "svelte/elements";
  import type {Snippet} from "svelte";

  let {
    class: className,
    eyebrow,
    heading,
    lead,
    align = "left",
    // level = 2,
    children,
    ...restProps
  }: HTMLAttributes<HTMLDivElement> & {
    eyebrow?: string;
    heading: string;
    lead?: string;
    align?: "left" | "center";
    // level?: 2 | 3;
    children?: Snippet;
  } = $props();
</script>

<div
    data-slot="section-header"
    data-align={align}
    class={cn(
    "flex max-w-2xl flex-col gap-4 mb-8",
    "data-[align=center]:mx-auto data-[align=center]:items-center data-[align=center]:text-center",
    className
  )}
    {...restProps}
>
  {#if eyebrow}
    <span class="section-title">
      {eyebrow}
    </span>
  {/if}

  <!--<svelte:element
      this={`h${level}`}
      class="section-subtitle text-card-foreground"
  >
    {heading}
  </svelte:element>-->
  <h2 class="section-subtitle">
    {heading}
  </h2>

  {#if lead}
    <p class="section-lead">
      {lead}
    </p>
  {/if}

  {@render children?.()}
</div>