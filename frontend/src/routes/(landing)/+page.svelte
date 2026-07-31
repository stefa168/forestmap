<script lang="ts">
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
	import Navbar from "./navbar.svelte";
	import Hero from './hero.svelte';
	import Section from './section.svelte';
	import StatsBand from './stats-band.svelte';

  const manifesto = [
    {
      title: "Un sistema vivente complesso",
      text: "Prima di essere risorsa economica, il bosco è cruciale per salute, equilibri e bellezza del territorio.",
    },
    {
      title: "Conservazione dal basso",
      text: "Tutela concreta attraverso lo strumento dell'acquisto diretto dei boschi di pregio.",
    },
    {
      title: "Ottica rigorosamente naturalistica",
      text: "Nessuno sfruttamento: protezione integrale degli ecosistemi forestali di qualità.",
    },
    {
      title: "Autonomia in sinergia",
      text: "Ogni realtà resta libera e indipendente, senza vincoli o interferenze esterne. Uniti, contiamo di più.",
    },
  ];

  const howItWorks: { title: string, description: string, icon: Component<SvelteHTMLElements['svg']> }[] = [
    {
      title: "Individua",
      description: "I nodi locali riconoscono i boschi di elevato valore ecologico a rischio sul territorio.",
      icon: LucideMapPin
    },
    {
      title: "Custodisce",
      description: "Con l'acquisto diretto, il bosco viene sottratto alla distruzione e protetto in modo integrale.",
      icon: LucideShieldCheck
    }, {
      title: "Mette in rete",
      description: "Le azioni si connettono in sinergia: conoscenza, strategia e tutela diventano patrimonio comune.",
      icon: LucideNetwork
    }
  ]
</script>

<Navbar/>

<div id="top"></div>

<Hero/>

<StatsBand/>

<section id="chi-siamo" class="max-w-280 my-0 mx-auto px-6 py-20 grid grid-cols-2 gap-16 items-center">
  <div>
    <p class="section-title text-chart-2">
      Chi siamo
    </p>
    <h2 class="section-subtitle text-card-foreground">
      Il bosco è un sistema vivente, non una merce.
    </h2>
    <p class="mt-8 text-[17px] text-[#45514a] leading-[1.7]">
      Il 28 marzo 2026, a Piovà Massaia, 62 tra associazioni, fondazioni, istituti di ricerca, comitati e cittadini
      hanno dato vita a una rete diffusa per la tutela concreta dei boschi delle aree collinari, montane e planiziali
      piemontesi.</p>
    <p class="mt-4 text-[17px] text-[#45514a] leading-[1.7]">
      Ogni nodo continua a operare in modo autonomo e indipendente. Insieme, però, facciamo massa critica: parliamo con
      una voce sola e più autorevole, e ci diamo obiettivi di conservazione più ambiziosi.
    </p>
  </div>
  <div class="border border-solid rounded-2xl overflow-hidden shadow-2xl">
    <img class="h-full w-full object-cover" src={chisiamo} alt="">
  </div>
</section>

{#snippet manifestoCard(elem: (typeof manifesto)[number], idx: number)}
  {@const index = idx < 10 ? '0' + idx : idx}
  <div class="rounded-xl border border-[#F3F7F0]/14 bg-[#F3F7F0]/3 p-7">
    <div class="mb-3 font-mono text-[13px] text-[#8FD694]">{index}</div>
    <h3 class="text-xl font-semibold tracking-[-0.02em] text-[#F3F7F0]">{elem.title}</h3>
    <p class="mt-3 text-sm leading-relaxed text-[#F3F7F0]/72">{elem.text}</p>
  </div>
{/snippet}

<section id="manifesto" class="bg-foreground">
  <div class="max-w-280 mx-auto px-6 py-20">
    <div class="max-w-2xl">
      <div class="section-title text-chart-1">
        Il Manifesto
      </div>
      <h2 class="text-[clamp(30px,3.6vw,42px)] leading-[1.1] font-bold tracking-tight text-balance text-secondary">
        I principi che ci tengono insieme.
      </h2>
      <p class="mt-5 text-[17px] text-[rgba(243,247,240,0.78)] leading-[1.7]">
        La carta fondativa della Rete: un decalogo di valori e indicazioni operative. Ne trovi qui i cardini.
      </p>
    </div>
    <div class="mt-12 grid grid-cols-2 gap-5">
      {#each manifesto as m, idx}
        {@render manifestoCard(m, idx + 1)}
      {/each}
    </div>
  </div>
</section>

{#snippet howItWorksCard(elem: (typeof howItWorks)[number], idx: number)}
  {@const Icon = elem.icon}
  <div class="border border-[#DEE6D8] rounded-[16px] p-8 bg-white">
    <div class="size-11 rounded-xl bg-secondary text-primary flex items-center justify-center">
      <Icon/>
    </div>
    <h3 class="mt-5 text-xl font-semibold text-shadow-sidebar-accent-foreground tracking-tight">
      {idx} · {elem.title}
    </h3>
    <p class="mt-3 text-sm leading-[1.65] text-[#45514A]">{elem.description}</p>
  </div>
{/snippet}

<section id="come-funziona" class="max-w-280 my-0 mx-auto px-6 py-20">
  <div class="max-w-2xl">
    <div class="section-title text-chart-2">
      Come funziona la rete
    </div>
    <h2 class="text-[clamp(30px,3.6vw,42px)] leading-[1.1] font-bold tracking-tight text-balance text-card-foreground">
      Nodi autonomi, un'unica strategia condivisa.
    </h2>
  </div>
  <div class="mt-12 grid grid-cols-3 gap-6">
    {#each howItWorks as e, idx}
      {@render howItWorksCard(e, idx + 1)}
    {/each}
  </div>
</section>

<section id="galleria" class="border-[#DEE6D8] border-y bg-white">
  <div class="max-w-280 my-0 mx-auto px-6 py-20">
    <div class="flex justify-between items-end flex-wrap gap-6">
      <div class="max-w-2xl">
        <div class="section-title text-chart-2">
          I boschi custoditi
        </div>
        <h2 class="section-subtitle text-card-foreground">
          Ciò che proteggiamo, in immagini.
        </h2>
      </div>
      <p class="m-0 max-w-[34ch] text-sm leading-[1.65] text-[#6B766C]">
        In futuro le foto verranno raccolte a rotazione dalle diverse realtà della Rete.
      </p>
    </div>
    <div style="margin-top:44px;display:grid;grid-template-columns:repeat(4,1fr);grid-auto-rows:200px;gap:16px;">
      <div style="grid-column:span 2;grid-row:span 2;position:relative;border-radius:14px;overflow:hidden;border:1px solid #DEE6D8;"></div>
      <div style="position:relative;border-radius:14px;overflow:hidden;border:1px solid #DEE6D8;"></div>
      <div style="position:relative;border-radius:14px;overflow:hidden;border:1px solid #DEE6D8;"></div>
      <div style="grid-column:span 2;position:relative;border-radius:14px;overflow:hidden;border:1px solid #DEE6D8;"></div>
      <div style="position:relative;border-radius:14px;overflow:hidden;border:1px solid #DEE6D8;"></div>
      <div style="position:relative;border-radius:14px;overflow:hidden;border:1px solid #DEE6D8;"></div>
    </div>
  </div>
</section>

<!--
<section class="max-w-280 my-0 mx-auto px-6">
  <div
      style="position:relative;border-radius:20px;overflow:hidden;border:1px solid #DEE6D8;min-height:340px;display:flex;align-items:center;background:#EDF2E8;">

      <enhanced:img
          src={mappa}
          alt="Placeholder mappa"
          class="absolute inset-0 h-full w-full object-cover opacity-35"
      />
    <div style="position:relative;padding:48px;max-width:520px;">
      <div
          style="display:inline-flex;align-items:center;gap:8px;padding:6px 14px;border-radius:9999px;background:#1F5130;color:#F3F7F0;font-size:12px;font-weight:600;letter-spacing:0.04em;text-transform:uppercase;margin-bottom:18px;">
        In arrivo
      </div>
      <h2 style="margin:0;font-size:clamp(26px,3vw,36px);line-height:1.12;font-weight:700;letter-spacing:-0.025em;color:#14261A;">
        La mappa interattiva dei boschi della Rete.</h2>
      <p style="margin:16px 0 0;font-size:16px;line-height:1.6;color:#3B4A3F;">Presto potrai esplorare le zone protette
        nodo per nodo, in tutto il Piemonte di collina, pianura e montagna.</p>
    </div>
  </div>
</section>
-->

<!-- ===== ASSOCIAZIONI / MARQUEE ===== -->
<section id="associazioni" style="padding:104px 0;overflow:hidden;">
  <div style="max-width:1120px;margin:0 auto;padding:0 24px;text-align:center;">
    <div class="section-title text-chart-2">
      Le realtà della Rete
    </div>
    <h2 style="margin:0 auto;max-width:22ch;font-size:clamp(28px,3.4vw,40px);line-height:1.12;font-weight:700;letter-spacing:-0.025em;color:#16281B;text-wrap:balance;">Associazioni, fondazioni e comitati, uniti dallo stesso orizzonte.</h2>
    <p style="margin:16px auto 0;max-width:46ch;font-size:15px;color:#6B766C;">Passa il mouse su un logo per fermare lo scorrimento e scoprire chi è.</p>
  </div>

  <div style="margin-top:48px;position:relative;">
    <div style="position:absolute;left:0;top:0;bottom:0;width:120px;z-index:2;pointer-events:none;background:linear-gradient(to right,#FBFBF8,rgba(251,251,248,0));"></div>
    <div style="position:absolute;right:0;top:0;bottom:0;width:120px;z-index:2;pointer-events:none;background:linear-gradient(to left,#FBFBF8,rgba(251,251,248,0));"></div>
    <div style="display:flex;width:max-content;gap:16px;padding:0 8px;animation:rbc-marquee 34s linear infinite;"> <!-- style-hover="animation-play-state:paused;" -->
<!--      <sc-for list="{{ marquee }}" as="assoc" hint-placeholder-count="8">
        <div style="position:relative;flex:none;width:280px;display:flex;align-items:center;gap:16px;padding:22px 24px;border:1px solid #DEE6D8;border-radius:16px;background:#ffffff;cursor:default;" style-hover="border-color:#1F5130;box-shadow:0 10px 15px -3px rgba(22,40,27,0.1);" onMouseEnter="{{ assoc.onEnter }}" onMouseLeave="{{ assoc.onLeave }}">
          <div style="flex:none;width:54px;height:54px;border-radius:12px;background:#EDF2E8;display:flex;align-items:center;justify-content:center;font-weight:700;font-size:18px;color:#1F5130;letter-spacing:-0.02em;">{{ assoc.initials }}</div>
          <div style="min-width:0;">
            <div style="font-weight:600;font-size:15px;line-height:1.2;color:#16281B;">{{ assoc.name }}</div>
            <div style="margin-top:3px;font-size:12px;color:#8A948B;">Nodo della Rete</div>
          </div>
          <sc-if value="{{ assoc.active }}" hint-placeholder-val="{{ false }}">
            <div style="position:absolute;bottom:calc(100% + 12px);left:50%;transform:translateX(-50%);width:250px;z-index:5;pointer-events:none;background:#16281B;color:#F3F7F0;padding:14px 16px;border-radius:12px;box-shadow:0 20px 25px -5px rgba(0,0,0,0.3);animation:rbc-rise .16s ease both;">
              <div style="font-weight:600;font-size:14px;line-height:1.25;">{{ assoc.name }}</div>
              <div style="margin-top:6px;font-size:13px;line-height:1.45;color:rgba(243,247,240,0.82);">{{ assoc.desc }}</div>
              <div style="position:absolute;top:100%;left:50%;transform:translateX(-50%);border:7px solid transparent;border-top-color:#16281B;"></div>
            </div>
          </sc-if>
        </div>
      </sc-for>-->
    </div>
  </div>
  <div style="text-align:center;margin-top:36px;">
    <span style="display:inline-block;padding:8px 16px;border-radius:9999px;background:#EDF2E8;color:#1F5130;font-size:14px;font-weight:600;">+ altre 56 realtà aderenti</span>
  </div>
</section>

<!-- ===== NOVITA ===== -->
<!--
<section id="novita" style="background:#ffffff;border-top:1px solid #DEE6D8;">
  <div style="max-width:1120px;margin:0 auto;padding:104px 24px;">
    <div style="font-size:13px;font-weight:600;letter-spacing:0.08em;text-transform:uppercase;color:#4E8A57;margin-bottom:16px;">Novità</div>
    <h2 style="margin:0;font-size:clamp(30px,3.6vw,42px);line-height:1.1;font-weight:700;letter-spacing:-0.025em;color:#16281B;">Cosa succede nella Rete.</h2>
    <div style="margin-top:44px;display:grid;grid-template-columns:repeat(3,1fr);gap:24px;">
      <article style="border:1px solid #DEE6D8;border-radius:16px;overflow:hidden;background:#FBFBF8;">
        <div style="height:180px;position:relative;border-bottom:1px solid #DEE6D8;"><image-slot id="rbc-n1" shape="rect" fit="cover" placeholder="Convegno di Piovà Massaia"></image-slot></div>
        <div style="padding:24px;">
          <div style="font-family:'Geist Mono',monospace;font-size:12px;color:#4E8A57;">28 marzo 2026 · Fondazione</div>
          <h3 style="margin:12px 0 0;font-size:18px;font-weight:600;letter-spacing:-0.02em;color:#16281B;line-height:1.3;">Nasce la Rete dei Boschi Custoditi</h3>
          <p style="margin:10px 0 0;font-size:14px;line-height:1.6;color:#45514A;">Al convegno di Piovà Massaia, davanti a centinaia di persone, prende forma la rete diffusa.</p>
        </div>
      </article>
      <article style="border:1px solid #DEE6D8;border-radius:16px;overflow:hidden;background:#FBFBF8;">
        <div style="height:180px;position:relative;border-bottom:1px solid #DEE6D8;"><image-slot id="rbc-n2" shape="rect" fit="cover" placeholder="Prossimi incontri"></image-slot></div>
        <div style="padding:24px;">
          <div style="font-family:'Geist Mono',monospace;font-size:12px;color:#4E8A57;">Prossimamente · Eventi</div>
          <h3 style="margin:12px 0 0;font-size:18px;font-weight:600;letter-spacing:-0.02em;color:#16281B;line-height:1.3;">Un calendario condiviso</h3>
          <p style="margin:10px 0 0;font-size:14px;line-height:1.6;color:#45514A;">Incontri e appuntamenti dedicati alla conoscenza e salvaguardia del patrimonio boschivo.</p>
        </div>
      </article>
      <article style="border:1px solid #DEE6D8;border-radius:16px;overflow:hidden;background:#FBFBF8;">
        <div style="height:180px;position:relative;border-bottom:1px solid #DEE6D8;"><image-slot id="rbc-n3" shape="rect" fit="cover" placeholder="Archivio del convegno"></image-slot></div>
        <div style="padding:24px;">
          <div style="font-family:'Geist Mono',monospace;font-size:12px;color:#4E8A57;">Su richiesta · Archivio</div>
          <h3 style="margin:12px 0 0;font-size:18px;font-weight:600;letter-spacing:-0.02em;color:#16281B;line-height:1.3;">Le relazioni scientifiche</h3>
          <p style="margin:10px 0 0;font-size:14px;line-height:1.6;color:#45514A;">Tutti i contenuti del convegno sono stati videoregistrati e sono a disposizione di chi ne fa richiesta.</p>
        </div>
      </article>
    </div>
  </div>
</section>
-->

<!-- ===== CTA ADERISCI ===== -->
<section id="aderisci" style="max-width:1120px;margin:0 auto;padding:104px 24px;">
  <div style="position:relative;overflow:hidden;border-radius:24px;background:#16281B;padding:72px 48px;text-align:center;">
    <svg viewBox="0 0 120 90" preserveAspectRatio="xMidYMax meet" style="position:absolute;left:50%;bottom:-20%;transform:translateX(-50%);width:720px;opacity:0.16;" fill="none">
      <path d="M-6 92 A72 72 0 0 1 126 92" stroke="#8FD694" stroke-width="6"/>
      <path d="M10 92 A56 56 0 0 1 110 92" stroke="#8FD694" stroke-width="6"/>
      <path d="M26 92 A40 40 0 0 1 94 92" stroke="#8FD694" stroke-width="6"/>
    </svg>
    <div style="position:relative;">
      <h2 style="margin:0 auto;max-width:20ch;font-size:clamp(30px,4vw,46px);line-height:1.08;font-weight:700;letter-spacing:-0.03em;color:#F3F7F0;text-wrap:balance;">Vuoi custodire un bosco con noi?</h2>
      <p style="margin:20px auto 0;max-width:52ch;font-size:18px;line-height:1.6;color:rgba(243,247,240,0.8);">La Rete è aperta a nuove realtà associative e private che condividono valori, principi e orizzonti comuni.</p>
      <div style="display:flex;flex-wrap:wrap;gap:12px;margin-top:34px;justify-content:center;">
        <a href="#contatti" style="padding:14px 28px;border-radius:8px;font-size:15px;font-weight:600;background:#F3F7F0;color:#16281B;" >Aderisci alla Rete</a> <!-- style-hover="background:#ffffff;" -->
        <a href="#contatti" style="padding:14px 28px;border-radius:8px;font-size:15px;font-weight:600;background:rgba(243,247,240,0.08);color:#F3F7F0;border:1px solid rgba(243,247,240,0.35);" >Contattaci</a> <!-- style-hover="background:rgba(243,247,240,0.16);" -->
      </div>
    </div>
  </div>
</section>

<!-- ===== FOOTER ===== -->
<footer id="contatti" style="border-top:1px solid #DEE6D8;background:#ffffff;">
  <div style="max-width:1120px;margin:0 auto;padding:72px 24px 40px;display:grid;grid-template-columns:1.4fr 1fr 1.3fr;gap:48px;">
    <div>
      <a href="#top" style="display:flex;align-items:center;gap:10px;color:#16281B;">
        <svg viewBox="0 0 120 90" width="34" height="26" fill="none" style="flex:none;">
          <path d="M14 82 A46 46 0 0 1 106 82" stroke="#3d7350" stroke-width="7" stroke-linecap="round"/>
          <path d="M28 82 A32 32 0 0 1 92 82" stroke="#2f5a3b" stroke-width="7" stroke-linecap="round"/>
          <path d="M42 82 A18 18 0 0 1 78 82" stroke="#1F5130" stroke-width="7" stroke-linecap="round"/>
        </svg>
        <span style="font-weight:700;font-size:15px;letter-spacing:-0.02em;line-height:1.05;">Rete dei Boschi<br><span style="color:#1F5130;">Custoditi</span></span>
      </a>
      <p style="margin:20px 0 0;max-width:34ch;font-size:14px;line-height:1.65;color:#6B766C;">Una rete diffusa di realtà associative e private per la conservazione concreta dei boschi di pregio del Piemonte.</p>
    </div>
    <div>
      <div style="font-size:13px;font-weight:600;letter-spacing:0.06em;text-transform:uppercase;color:#8A948B;margin-bottom:16px;">Sezioni</div>
      <div style="display:flex;flex-direction:column;gap:10px;font-size:14px;">
        <a href="#chi-siamo" style="color:#45514A;" class="text-[#1F5130]">Chi siamo</a>
        <a href="#manifesto" style="color:#45514A;" class="text-[#1F5130]">Il Manifesto</a>
        <a href="#come-funziona" style="color:#45514A;" class="text-[#1F5130]">Come funziona</a>
        <a href="#galleria" style="color:#45514A;" class="text-[#1F5130]">I boschi</a>
        <a href="#novita" style="color:#45514A;" class="text-[#1F5130]">Novità</a>
      </div>
    </div>
    <div>
      <div style="font-size:13px;font-weight:600;letter-spacing:0.06em;text-transform:uppercase;color:#8A948B;margin-bottom:16px;">Contatti</div>
      <div style="display:flex;flex-direction:column;gap:14px;font-size:14px;color:#45514A;line-height:1.5;">
        <div><strong style="color:#16281B;font-weight:600;">TBGM</strong><br>339 4676949 · <a href="/cdn-cgi/l/email-protection#8fe9fdeee1ece0fdfda1ece0ebeaf7cfe8e2eee6e3a1ece0e2"><span class="__cf_email__" data-cfemail="81e7f3e0efe2eef3f3afe2eee5e4f9c1e6ece0e8edafe2eeec">[email&#160;protected]</span></a></div>
        <div><strong style="color:#16281B;font-weight:600;">Pro Natura</strong><br>348 4528931 · <a href="/cdn-cgi/l/email-protection#90e0f9f5e2ffbef2f5fcfcf5e4e4f9a5a5d0f7fdf1f9fcbef3fffd"><span class="__cf_email__" data-cfemail="63130a06110c4d01060f0f0617170a565623040e020a0f4d000c0e">[email&#160;protected]</span></a></div>
        <div><strong style="color:#16281B;font-weight:600;">Legambiente Valtriversa</strong><br>335 7506324 · <a href="/cdn-cgi/l/email-protection#4a3a25383e2b152b0a2623282f382564233e"><span class="__cf_email__" data-cfemail="c5b5aab7b1a49aa485a9aca7a0b7aaebacb1">[email&#160;protected]</span></a></div>
      </div>
    </div>
  </div>
  <div style="border-top:1px solid #DEE6D8;">
    <div style="max-width:1120px;margin:0 auto;padding:24px;display:flex;justify-content:space-between;flex-wrap:wrap;gap:12px;font-size:13px;color:#8A948B;">
      <span>© 2026 Rete dei Boschi Custoditi</span>
      <span>Piemonte · collina, pianura e montagna</span>
    </div>
  </div>
</footer>