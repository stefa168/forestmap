import {defineEnvVars} from '@sveltejs/kit/env';
import { building } from '$app/env';
import {z} from 'zod';

// https://svelte.dev/docs/kit/environment-variables#Explicit-environment-variables
export const variables = defineEnvVars({
	API_BASE: {
		description: 'URL di base per le API della backend',
		schema: building ? z.optional(z.string()) : z.string() // todo forse sarebbe più bello un URL
	}
});