<!-- section.svelte -->
<script lang="ts">
  import {cn} from "$lib/utils";
  import type {HTMLAttributes} from "svelte/elements";

  let {
    class: className,
    variant = "default",
    children,
    ...restProps
  }: HTMLAttributes<HTMLElement> & {
    variant?: "default" | "panel" | "tight" | "dark";
  } = $props();
</script>

<section
    data-variant={variant}
    class={cn(
    "section-shell py-20",
    "data-[variant=tight]:[--section-py:--spacing(12)]",
    "data-[variant=panel]:bg-card",
    variant === "dark" && "dark bg-background",
    className
  )}
    {...restProps}
>
  {@render children?.()}
</section>

<style>
  @layer components {
    section :global(p) {
      /*text-lg*/
      font-size: var(--text-lg);
      /*leading-7*/
      line-height: calc(var(--spacing) * 7);
      color: var(--color-body);
    }
  }
</style>