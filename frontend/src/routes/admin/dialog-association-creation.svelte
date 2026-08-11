<script lang="ts">
  import * as Dialog from "$lib/components/ui/dialog";
  import {Button, buttonVariants} from "$lib/components/ui/button";


</script>

<Dialog.Root>
  <Dialog.Content>
    <Dialog.Header>
      <Dialog.Title>Aggiunta di un'Associazione</Dialog.Title>
      <Dialog.Description>
        Da questo dialog è possibile creare una nuova Associazione.
      </Dialog.Description>
    </Dialog.Header>



    <Dialog.Footer>
      <Dialog.Close
        type="button"
        class={buttonVariants({ variant: "outline" })}
      >
        Annulla
      </Dialog.Close>
      <Button type="submit">Crea</Button>
    </Dialog.Footer>
  </Dialog.Content>
</Dialog.Root>