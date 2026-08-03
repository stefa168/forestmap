<!-- section.svelte -->
<script lang="ts">
  import {cn} from "$lib/utils";
  import type {ClassValue, HTMLAttributes} from "svelte/elements";

  let {
    containerClass,
    class: className,
    variant = "default",
    children,
    ...restProps
  }: HTMLAttributes<HTMLElement> & {
    containerClass?: ClassValue | undefined | null;
    variant?: "default" | "panel" | "tight" | "dark";
  } = $props();
</script>

<section
    data-variant={variant}
    class={cn(
    "py-20",
    "data-[variant=tight]:[--section-py:--spacing(12)]",
    "data-[variant=panel]:bg-card",
    variant === "dark" && "dark bg-background",
    containerClass
  )}
    {...restProps}
>
  <div class={cn("section-shell", className)}>
    {@render children?.()}
  </div>
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