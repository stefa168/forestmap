import { command, getRequestEvent, query } from '$app/server';
import { error } from '@sveltejs/kit';
import { API_BASE } from '$app/env/private';
import { FileMetaSchema, type PresignedUrl } from './types.ts';
import { z } from 'zod';

export const presignUpload = command(FileMetaSchema, async (meta) => {
	const { fetch } = getRequestEvent();

	const res = await fetch(`${API_BASE}/assets/presign`, {
		method: 'POST',
		headers: {"Content-Type": "application/json"},
		body: JSON.stringify(meta)
	});

	if (!res.ok) {
		error(500);
	}

	return (await res.json()) as PresignedUrl;
});

export const finalizeUpload = command(z.string(), async (id) => {
	const { fetch } = getRequestEvent();

	const res = await fetch(`${API_BASE}/assets/finalize`, {
		method: 'POST',
		body: id
	});

	if (!res.ok) {
		error(500);
	}
})