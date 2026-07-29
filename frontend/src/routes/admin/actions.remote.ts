import { getRequestEvent, query } from '$app/server';
import { error } from '@sveltejs/kit';
import { z } from 'zod';

export interface AssociationName {
	id: number;
	name: string;
}

export const getAssociationsNames = query(async () => {
	const { fetch } = getRequestEvent();
	const res = await fetch('http://localhost:8080/api/associations/names');

	if (!res.ok) {
		error(500);
	}

	return (await res.json()) as AssociationName[];
});

export const getAssociation = query(
	z.number(),
	async (id) => {
		const { fetch } = getRequestEvent();
		const res = await fetch(`http://localhost:8080/api/associations/${id}`);

		if (!res.ok) {
			error(500);
		}

		console.log(await res.json());
	}
)