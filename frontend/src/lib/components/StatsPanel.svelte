<script lang="ts">
	import type { TemperatureStats } from '$lib/api';

	export let stats: TemperatureStats | null = null;
	export let loading = false;

	function formatTemp(value: number | null): string {
		if (value === null) return '--';
		return `${value.toFixed(1)}°`;
	}
</script>

<div class="stats-panel">
	{#if loading}
		<div class="loading">Loading stats...</div>
	{:else if stats}
		<div class="stat-grid">
			<div class="stat-card current">
				<div class="stat-label">Current</div>
				<div class="stat-value">{formatTemp(stats.current)}</div>
			</div>
			<div class="stat-card">
				<div class="stat-label">Min</div>
				<div class="stat-value">{formatTemp(stats.min)}</div>
			</div>
			<div class="stat-card">
				<div class="stat-label">Max</div>
				<div class="stat-value">{formatTemp(stats.max)}</div>
			</div>
			<div class="stat-card">
				<div class="stat-label">Average</div>
				<div class="stat-value">{formatTemp(stats.avg)}</div>
			</div>
		</div>
		<div class="reading-count">
			{stats.readingCount} readings in period
		</div>
	{:else}
		<div class="no-data">No data available</div>
	{/if}
</div>

<style>
	.stats-panel {
		background: #f8f9fa;
		border-radius: 8px;
		padding: 1.5rem;
		margin-bottom: 1.5rem;
	}

	.stat-grid {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 1rem;
	}

	.stat-card {
		background: white;
		border-radius: 8px;
		padding: 1rem;
		text-align: center;
		box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
	}

	.stat-card.current {
		background: #e3f2fd;
		border: 2px solid #2196f3;
	}

	.stat-label {
		font-size: 0.875rem;
		color: #666;
		margin-bottom: 0.5rem;
	}

	.stat-value {
		font-size: 1.75rem;
		font-weight: 600;
		color: #333;
	}

	.reading-count {
		text-align: center;
		margin-top: 1rem;
		font-size: 0.875rem;
		color: #666;
	}

	.loading,
	.no-data {
		text-align: center;
		color: #666;
		padding: 2rem;
	}

	@media (max-width: 600px) {
		.stat-grid {
			grid-template-columns: repeat(2, 1fr);
		}
	}
</style>
