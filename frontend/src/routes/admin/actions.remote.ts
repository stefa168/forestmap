import { command, getRequestEvent, query } from '$app/server';
import { error } from '@sveltejs/kit';
import { z } from 'zod';
import { API_BASE } from '$app/env/private';

export interface AssociationName {
	id: number;
	name: string;
}

export const getAssociationsNames = query(async () => {
	const { fetch } = getRequestEvent();
	const res = await fetch(`${API_BASE}/associations/names`);

	if (!res.ok) {
		error(500);
	}

	return (await res.json()) as AssociationName[];
});

export const getAssociation = query(z.number(), async (id) => {
	const { fetch } = getRequestEvent();
	const res = await fetch(`${API_BASE}/api/associations/${id}`);

	if (!res.ok) {
		error(500, await res.text());
	}

	console.log(await res.json());
});
