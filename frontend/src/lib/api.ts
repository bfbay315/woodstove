import { auth } from './auth';

const API_BASE = import.meta.env.VITE_API_URL || '/api';

export interface TemperatureReading {
	id: number;
	sensorId: string;
	temperature: number;
	recordedAt: string;
	createdAt: string;
}

export interface TemperatureStats {
	current: number | null;
	min: number | null;
	max: number | null;
	avg: number | null;
	readingCount: number;
	periodStart: string;
	periodEnd: string;
	sensorId: string | null;
}

export interface Sensor {
	sensorId: string;
}

export interface TemperatureRequest {
	sensorId: string;
	temperature: number;
	recordedAt?: string;
}

function getAuthHeaders(): HeadersInit {
	const token = auth.getToken();
	if (token) {
		return {
			Authorization: `Bearer ${token}`
		};
	}
	return {};
}

export async function postTemperature(data: TemperatureRequest): Promise<TemperatureReading> {
	const response = await fetch(`${API_BASE}/temperatures`, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
			...getAuthHeaders()
		},
		body: JSON.stringify(data)
	});
	if (!response.ok) {
		throw new Error(`Failed to post temperature: ${response.statusText}`);
	}
	return response.json();
}

export async function getTemperatures(params?: {
	sensorId?: string;
	limit?: number;
	from?: string;
	to?: string;
}): Promise<TemperatureReading[]> {
	const searchParams = new URLSearchParams();
	if (params?.sensorId) searchParams.set('sensorId', params.sensorId);
	if (params?.limit) searchParams.set('limit', params.limit.toString());
	if (params?.from) searchParams.set('from', params.from);
	if (params?.to) searchParams.set('to', params.to);

	const query = searchParams.toString();
	const url = `${API_BASE}/temperatures${query ? `?${query}` : ''}`;

	const response = await fetch(url, {
		headers: getAuthHeaders()
	});
	if (!response.ok) {
		if (response.status === 401) {
			auth.logout();
			throw new Error('Session expired. Please sign in again.');
		}
		throw new Error(`Failed to get temperatures: ${response.statusText}`);
	}
	return response.json();
}

export async function getStats(params?: {
	sensorId?: string;
	period?: 'hour' | 'day' | 'week';
}): Promise<TemperatureStats> {
	const searchParams = new URLSearchParams();
	if (params?.sensorId) searchParams.set('sensorId', params.sensorId);
	if (params?.period) searchParams.set('period', params.period);

	const query = searchParams.toString();
	const url = `${API_BASE}/temperatures/stats${query ? `?${query}` : ''}`;

	const response = await fetch(url, {
		headers: getAuthHeaders()
	});
	if (!response.ok) {
		if (response.status === 401) {
			auth.logout();
			throw new Error('Session expired. Please sign in again.');
		}
		throw new Error(`Failed to get stats: ${response.statusText}`);
	}
	return response.json();
}

export async function getSensors(): Promise<Sensor[]> {
	const response = await fetch(`${API_BASE}/sensors`, {
		headers: getAuthHeaders()
	});
	if (!response.ok) {
		if (response.status === 401) {
			auth.logout();
			throw new Error('Session expired. Please sign in again.');
		}
		throw new Error(`Failed to get sensors: ${response.statusText}`);
	}
	return response.json();
}
