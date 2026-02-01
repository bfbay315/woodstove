<script lang="ts">
	import { onMount, onDestroy } from 'svelte';
	import { getTemperatures, getStats, getSensors } from '$lib/api';
	import type { TemperatureReading, TemperatureStats, Sensor } from '$lib/api';
	import StatsPanel from '$lib/components/StatsPanel.svelte';
	import TemperatureChart from '$lib/components/TemperatureChart.svelte';
	import TemperatureTable from '$lib/components/TemperatureTable.svelte';

	let readings: TemperatureReading[] = [];
	let stats: TemperatureStats | null = null;
	let sensors: Sensor[] = [];
	let loading = true;
	let error: string | null = null;

	let selectedSensor: string | null = null;
	let selectedPeriod: 'hour' | 'day' | 'week' = 'day';

	let refreshInterval: ReturnType<typeof setInterval> | null = null;
	const REFRESH_INTERVAL = 30000; // 30 seconds

	async function loadData() {
		try {
			error = null;
			const [readingsData, statsData, sensorsData] = await Promise.all([
				getTemperatures({
					sensorId: selectedSensor ?? undefined,
					limit: 100
				}),
				getStats({
					sensorId: selectedSensor ?? undefined,
					period: selectedPeriod
				}),
				getSensors()
			]);

			readings = readingsData;
			stats = statsData;
			sensors = sensorsData;
			loading = false;
		} catch (e) {
			error = e instanceof Error ? e.message : 'Failed to load data';
			loading = false;
		}
	}

	function handleSensorChange(event: Event) {
		const target = event.target as HTMLSelectElement;
		selectedSensor = target.value || null;
		loading = true;
		loadData();
	}

	function handlePeriodChange(event: Event) {
		const target = event.target as HTMLSelectElement;
		selectedPeriod = target.value as 'hour' | 'day' | 'week';
		loading = true;
		loadData();
	}

	function startAutoRefresh() {
		refreshInterval = setInterval(loadData, REFRESH_INTERVAL);
	}

	function stopAutoRefresh() {
		if (refreshInterval) {
			clearInterval(refreshInterval);
			refreshInterval = null;
		}
	}

	onMount(() => {
		loadData();
		startAutoRefresh();
	});

	onDestroy(() => {
		stopAutoRefresh();
	});
</script>

<svelte:head>
	<title>Woodstove Temperature Monitor</title>
</svelte:head>

<div class="dashboard">
	<div class="controls">
		<div class="control-group">
			<label for="sensor-select">Sensor:</label>
			<select id="sensor-select" on:change={handleSensorChange} value={selectedSensor ?? ''}>
				<option value="">All Sensors</option>
				{#each sensors as sensor (sensor.sensorId)}
					<option value={sensor.sensorId}>{sensor.sensorId}</option>
				{/each}
			</select>
		</div>

		<div class="control-group">
			<label for="period-select">Period:</label>
			<select id="period-select" on:change={handlePeriodChange} value={selectedPeriod}>
				<option value="hour">Last Hour</option>
				<option value="day">Last 24 Hours</option>
				<option value="week">Last Week</option>
			</select>
		</div>

		<button class="refresh-btn" on:click={loadData} disabled={loading}>
			{loading ? 'Refreshing...' : 'Refresh'}
		</button>
	</div>

	{#if error}
		<div class="error">
			<strong>Error:</strong>
			{error}
		</div>
	{/if}

	<StatsPanel {stats} {loading} />

	<TemperatureChart {readings} {loading} />

	<TemperatureTable {readings} {loading} />

	<div class="auto-refresh-note">Auto-refreshes every 30 seconds</div>
</div>

<style>
	.dashboard {
		max-width: 1200px;
		margin: 0 auto;
	}

	.controls {
		display: flex;
		gap: 1.5rem;
		align-items: center;
		margin-bottom: 1.5rem;
		flex-wrap: wrap;
	}

	.control-group {
		display: flex;
		align-items: center;
		gap: 0.5rem;
	}

	.control-group label {
		font-weight: 500;
		color: #555;
	}

	.control-group select {
		padding: 0.5rem 1rem;
		border: 1px solid #ddd;
		border-radius: 4px;
		background: white;
		min-width: 150px;
	}

	.refresh-btn {
		padding: 0.5rem 1rem;
		background: #2196f3;
		color: white;
		border: none;
		border-radius: 4px;
		font-weight: 500;
		transition: background 0.2s;
	}

	.refresh-btn:hover:not(:disabled) {
		background: #1976d2;
	}

	.refresh-btn:disabled {
		background: #90caf9;
		cursor: not-allowed;
	}

	.error {
		background: #ffebee;
		border: 1px solid #ef5350;
		color: #c62828;
		padding: 1rem;
		border-radius: 8px;
		margin-bottom: 1.5rem;
	}

	.auto-refresh-note {
		text-align: center;
		color: #888;
		font-size: 0.875rem;
		margin-top: 1rem;
	}
</style>
