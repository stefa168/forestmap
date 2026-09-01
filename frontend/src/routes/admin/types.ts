import { z } from 'zod';

const MAX_UPLOAD_BYTES = 10 * 1024 * 1024;
export const ACCEPTED_TYPES = ['image/jpeg', 'image/png', 'application/pdf'] as const;

export type FileMetadata = Pick<File, 'name' | 'size' | 'type'>;
export const FileMetaSchema = z.object({
	name: z.string().min(1).max(255),
	size: z.int().min(1).max(MAX_UPLOAD_BYTES),
	type: z.enum(ACCEPTED_TYPES)
}) satisfies z.ZodType<FileMetadata>;

export interface PresignedUrl {
	id: string;
	url: string;
	contentType: string;
}
