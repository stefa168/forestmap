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
    variant?: "default" | "panel" | "tight";
  } = $props();
</script>

<section
  data-variant={variant}
  class={cn(
    "py-(--section-py) [--section-py:--spacing(20)]",
    "data-[variant=tight]:[--section-py:--spacing(12)]",
    "data-[variant=panel]:bg-card",
    className
  )}
  {...restProps}
>
  <div class="section-shell">
    {@render children?.()}
  </div>
</section>