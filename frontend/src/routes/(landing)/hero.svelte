<script>
import hero_img from "$lib/assets/hero_img.jpg";
</script>

<header class="relative flex min-h-dvh items-end overflow-hidden">
  <enhanced:img
    src={hero_img}
    alt="Foto di un bosco custodito"
    class="absolute inset-0 h-full w-full object-cover"
  />
  <!-- Overlay immagine con gradiente -->
  <div
    class="pointer-events-none absolute inset-0"
    style="background:linear-gradient(to top,rgba(11,26,15,0.90) 0%,rgba(11,26,15,0.55) 40%,rgba(11,26,15,0.15) 75%,rgba(11,26,15,0.35) 100%);"
  ></div>

  <div class="relative mx-auto w-full max-w-280 px-6 pb-22 text-[#F3F7F0]">
    <h1 class="m-0 max-w-[16ch] text-[clamp(2.375rem,6vw,4.25rem)] font-bold leading-[1.02]
               tracking-[-0.03em] text-balance
               animate-in fade-in slide-in-from-bottom-[14px] fill-mode-both duration-600 delay-50">
      Mettiamo al sicuro i boschi più preziosi del Piemonte.
    </h1>
    <p class="mt-6 max-w-[56ch] text-[clamp(1.0625rem,2vw,1.3125rem)] leading-[1.55] text-[#F3F7F0]/88
              animate-in fade-in slide-in-from-bottom-[14px] fill-mode-both duration-600 delay-[120ms]"
    >
      62 realtà unite che, dal basso e con l'acquisto diretto, sottraggono alla distruzione le foreste di elevata
      naturalità — e le custodiscono per sempre.
    </p>

    <div class="mt-8 flex flex-wrap gap-3
                animate-in fade-in slide-in-from-bottom-[14px] fill-mode-both duration-600 delay-[180ms]">
      <a href="#manifesto"
         class="rounded-md bg-[#F3F7F0] px-6 py-3.5 text-[15px] font-semibold text-[#16281B]
                transition-colors hover:bg-white">
        Scopri il Manifesto
      </a>
      <a href="#come-funziona"
         class="rounded-md border border-[#F3F7F0]/35 bg-[#F3F7F0]/8 px-6 py-3.5 text-[15px]
                font-semibold text-[#F3F7F0] transition-colors hover:bg-[#F3F7F0]/16">
        Come funziona la rete
      </a>
    </div>
  </div>
</header>
