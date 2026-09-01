<script lang="ts">
  import {Button, buttonVariants} from "$lib/components/ui/button";
  import {Label} from "$lib/components/ui/label";
  import {Textarea} from "$lib/components/ui/textarea";
  import * as InputGroup from "$lib/components/ui/input-group";
  import * as Dialog from "$lib/components/ui/dialog";
  import {Progress} from "$lib/components/ui/progress";

  import LucideImageUp from '~icons/lucide/image-up';
  import LucideUpload from '~icons/lucide/upload';

  import {finalizeUpload, presignUpload} from "./upload.remote";
  import {ACCEPTED_TYPES, FileMetaSchema} from "./types.ts";

  let {onUploaded}: { onUploaded?: (id: string) => void } = $props();

  let dialogOpen = $state(false);
  let file = $state<File | null>(null);
  let description = $state('');
  let busy = $state(false);
  let progress = $state(0);
  let error = $state<string | null>(null);

  const parsed = $derived(
    file === null
      ? null
      : FileMetaSchema.safeParse({
        name: file.name,
        size: file.size,
        type: file.type,
        // description: description.trim() || null
      })
  );

  $inspect(parsed)

  const errors = $derived.by(() => {
    if (!parsed || parsed.success) return {} as Record<string, string>;
    return Object.fromEntries(
      parsed.error.issues.map((i) => [i.path.join('.'), i.message])
    );
  });

  const canSubmit = $derived(parsed?.success === true && !busy);

  function reset() {
    file = null;
    description = '';
    busy = false;
    progress = 0;
    error = null;
  }

  /** fetch() has no upload progress events; XHR does. */
  function putObject(url: string, contentType: string, body: File): Promise<void> {
    return new Promise((resolve, reject) => {
      const xhr = new XMLHttpRequest();
      xhr.open('PUT', url);
      // Must match exactly what the server signed, or SigV4 rejects with 403.
      xhr.setRequestHeader('Content-Type', contentType);

      xhr.upload.addEventListener('progress', (e) => {
        if (e.lengthComputable) progress = Math.round((e.loaded / e.total) * 100);
      });
      xhr.addEventListener('load', () => {
        if (xhr.status >= 200 && xhr.status < 300) resolve();
        else reject(new Error(`Upload fallito (HTTP ${xhr.status})`));
      });
      xhr.addEventListener('error', () => reject(new Error('Errore di rete durante il caricamento.')));
      xhr.addEventListener('abort', () => reject(new Error('Caricamento annullato.')));

      xhr.send(body);
    });
  }

  async function submit(event: SubmitEvent) {
    event.preventDefault();
    if (!parsed?.success) return;

    busy = true;
    error = null;
    progress = 0;

    try {
      // 1. Server inserts a PENDING_UPLOAD row, commits, then signs.
      const {id, url, contentType} = await presignUpload(parsed.data);

      // 2. Bytes go straight to the quarantine bucket, never through the app.
      await putObject(url, contentType, file);

      // 3. Server moves the row to PENDING_VALIDATION.
      //    If we never get here, the sweeper reclaims the orphan.
      await finalizeUpload(id);

      onUploaded?.(id);
      dialogOpen = false;
      reset();
    } catch (e) {
      error = e instanceof Error ? e.message : 'Errore imprevisto.';
      busy = false;
    }
  }
</script>

<Dialog.Root
    bind:open={dialogOpen}
    onOpenChange={(open) => { if (!open) reset(); }}
>
  <Dialog.Trigger>
    <Button>
      <LucideImageUp/>
      Carica un'immagine
    </Button>
  </Dialog.Trigger>

  <Dialog.Content>
    <Dialog.Header>
      <Dialog.Title>Caricamento immagini</Dialog.Title>
      <Dialog.Description>
        Da questo dialog è possibile caricare una nuova immagine.
      </Dialog.Description>
    </Dialog.Header>

    <form onsubmit={submit} class="space-y-4">
      <div class="space-y-2">
        <Label for="asset-file">File da caricare</Label>
        <InputGroup.Root>
          <InputGroup.Input
              id="asset-file"
              type="file"
              accept={ACCEPTED_TYPES.join(', ')}
              disabled={busy}
              onchange={(e) => { file = e.currentTarget.files?.[0] ?? null; error = null; }}
          />
          <InputGroup.Addon align="inline-end">
            <InputGroup.Button type="submit" disabled={!canSubmit}>
              <LucideUpload/>
              Carica
            </InputGroup.Button>
          </InputGroup.Addon>
        </InputGroup.Root>

        {#if errors.type}<p class="text-destructive text-sm">{errors.type}</p>{/if}
      </div>

      <div class="space-y-2">
        <Label for="asset-description">Descrizione</Label>
        <Textarea
            id="asset-description"
            bind:value={description}
            disabled={busy}
            rows={2}
            placeholder="Breve descrizione del contenuto dell'immagine"
        />
        <p class="text-muted-foreground text-sm">
          Usata come testo alternativo per chi naviga con screen reader.
        </p>
      </div>

      {#if busy}
        <div class="space-y-1">
          <Progress value={progress} max={100}/>
          <p class="text-muted-foreground text-sm" aria-live="polite">
            Caricamento in corso… {progress}%
          </p>
        </div>
      {/if}

      {#if error}
        <p class="text-destructive text-sm" role="alert">{error}</p>
      {/if}
    </form>

    <Dialog.Footer>
      <Dialog.Close
          type="button"
          disabled={busy}
          class={buttonVariants({ variant: "outline" })}
      >
        Chiudi
      </Dialog.Close>
    </Dialog.Footer>
  </Dialog.Content>
</Dialog.Root>
