<script lang="ts">
	import type { TemperatureReading } from '$lib/api';

	export let readings: TemperatureReading[] = [];
	export let loading = false;

	function formatDate(isoString: string): string {
		const date = new Date(isoString);
		return date.toLocaleString();
	}

	function formatTemp(value: number): string {
		return `${value.toFixed(1)}°`;
	}
</script>

<div class="table-container">
	<h3>Recent Readings</h3>
	{#if loading}
		<div class="loading">Loading readings...</div>
	{:else if readings.length === 0}
		<div class="no-data">No readings available</div>
	{:else}
		<table>
			<thead>
				<tr>
					<th>Sensor</th>
					<th>Temperature</th>
					<th>Recorded At</th>
				</tr>
			</thead>
			<tbody>
				{#each readings as reading (reading.id)}
					<tr>
						<td>{reading.sensorId}</td>
						<td class="temp-cell">{formatTemp(reading.temperature)}</td>
						<td>{formatDate(reading.recordedAt)}</td>
					</tr>
				{/each}
			</tbody>
		</table>
	{/if}
</div>

<style>
	.table-container {
		background: white;
		border-radius: 8px;
		padding: 1.5rem;
		box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
	}

	h3 {
		margin: 0 0 1rem 0;
		color: #333;
	}

	table {
		width: 100%;
		border-collapse: collapse;
	}

	th,
	td {
		padding: 0.75rem;
		text-align: left;
		border-bottom: 1px solid #eee;
	}

	th {
		font-weight: 600;
		color: #666;
		font-size: 0.875rem;
		text-transform: uppercase;
	}

	.temp-cell {
		font-weight: 500;
		font-family: monospace;
		font-size: 1rem;
	}

	tbody tr:hover {
		background: #f8f9fa;
	}

	.loading,
	.no-data {
		text-align: center;
		color: #666;
		padding: 2rem;
	}
</style>
