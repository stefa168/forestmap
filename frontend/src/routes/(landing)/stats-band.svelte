<script lang="ts">
  const stats: { value: string, label: string; }[] = [
    {value: "62", label: "realtà aderenti"},
    {value: "500+", label: "ettari già protetti"},
    {value: "6", label: "nodi fondatori"},
    {value: "2026", label: "anno di fondazione"},
  ];
</script>

<div class="section-shell">
  <dl class="grid grid-cols-2 sm:grid-cols-4 sm:grid-flow-col">
    {#each stats as { value, label }}
      <div class="flex-1 flex-col px-6 py-5 text-center">
        <dd class="text-5xl tracking-tight font-bold text-primary leading-none">{value}</dd>
        <dt class="text-sm text-muted-foreground">{label}</dt>
      </div>
    {/each}
  </dl>
</div>