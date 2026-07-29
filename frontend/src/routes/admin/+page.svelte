<script lang="ts">
  import {getAssociation, getAssociationsNames} from './actions.remote';

  import {Button, buttonVariants} from "$lib/components/ui/button";
  import * as Dialog from "$lib/components/ui/dialog";

  let associations = await getAssociationsNames(); // todo spostare in un await block

  interface Association {

  }

  let editDialogAssociationData: null | Promise<Association> = $state(null);
  let editDialogOpen = $state(false);
  const openEditDialog = async (assocId: number) => {
    editDialogOpen = true;
    await getAssociation(assocId);
    // editDialogAssociationData =
  }
</script>

<div class="w-full flex flex-row justify-between">
  <div>
    <h1 class="text-3xl">Associazioni anagrafate</h1>
    <h3 class="text-muted-foreground">Cliccare sul nome di un'associazione per visualizzare e modificare i dettagli</h3>
  </div>

  <Button class="hover:cursor-pointer">
    Aggiungi associazione
  </Button>
</div>

<ul class="mt-2 list-inside list-disc">
  {#each associations as assoc}
    <li class="hover:cursor-pointer"
        onclick={() => openEditDialog(assoc.id)}
    >
      {assoc.name}
    </li>
  {/each}
</ul>

<Dialog.Root bind:open={editDialogOpen}>
  <Dialog.Content>
    <Dialog.Header>
      <Dialog.Title>Modifica dell'anagrafica associazione</Dialog.Title>
      <Dialog.Description>
        Da questo dialog è possibile modificare l'anagrafica di un'associazione.
      </Dialog.Description>
    </Dialog.Header>



    <Dialog.Footer>
      <Dialog.Close
          type="button"
          class={buttonVariants({ variant: "outline" })}
      >
        Annulla
      </Dialog.Close>
      <Button type="submit">Salva Modifiche</Button>
    </Dialog.Footer>
  </Dialog.Content>
</Dialog.Root>