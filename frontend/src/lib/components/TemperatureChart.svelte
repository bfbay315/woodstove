<script lang="ts">
	import { onMount, onDestroy } from 'svelte';
	import type { TemperatureReading } from '$lib/api';
	import {
		Chart,
		LineController,
		LineElement,
		PointElement,
		LinearScale,
		TimeScale,
		Title,
		Tooltip,
		Legend,
		Filler
	} from 'chart.js';
	import 'chart.js/auto';

	export let readings: TemperatureReading[] = [];
	export let loading = false;

	let canvas: HTMLCanvasElement;
	let chart: Chart | null = null;

	Chart.register(
		LineController,
		LineElement,
		PointElement,
		LinearScale,
		TimeScale,
		Title,
		Tooltip,
		Legend,
		Filler
	);

	function createChart() {
		if (!canvas) return;

		const sortedReadings = [...readings].sort(
			(a, b) => new Date(a.recordedAt).getTime() - new Date(b.recordedAt).getTime()
		);

		const labels = sortedReadings.map((r) => new Date(r.recordedAt));
		const data = sortedReadings.map((r) => r.temperature);

		if (chart) {
			chart.destroy();
		}

		chart = new Chart(canvas, {
			type: 'line',
			data: {
				labels,
				datasets: [
					{
						label: 'Temperature',
						data,
						borderColor: '#2196f3',
						backgroundColor: 'rgba(33, 150, 243, 0.1)',
						borderWidth: 2,
						fill: true,
						tension: 0.3,
						pointRadius: 3,
						pointHoverRadius: 5
					}
				]
			},
			options: {
				responsive: true,
				maintainAspectRatio: false,
				plugins: {
					legend: {
						display: false
					},
					tooltip: {
						callbacks: {
							label: (context) => {
								const value = context.parsed.y;
								return value !== null ? `${value.toFixed(1)}°` : '';
							}
						}
					}
				},
				scales: {
					x: {
						type: 'time',
						time: {
							displayFormats: {
								hour: 'HH:mm',
								day: 'MMM d'
							}
						},
						title: {
							display: true,
							text: 'Time'
						}
					},
					y: {
						title: {
							display: true,
							text: 'Temperature'
						},
						ticks: {
							callback: (value) => `${value}°`
						}
					}
				},
				interaction: {
					intersect: false,
					mode: 'index'
				}
			}
		});
	}

	$: if (readings && canvas) {
		createChart();
	}

	onMount(() => {
		if (readings.length > 0) {
			createChart();
		}
	});

	onDestroy(() => {
		if (chart) {
			chart.destroy();
		}
	});
</script>

<div class="chart-container">
	<h3>Temperature Over Time</h3>
	{#if loading}
		<div class="loading">Loading chart...</div>
	{:else if readings.length === 0}
		<div class="no-data">No data available for chart</div>
	{:else}
		<div class="chart-wrapper">
			<canvas bind:this={canvas}></canvas>
		</div>
	{/if}
</div>

<style>
	.chart-container {
		background: white;
		border-radius: 8px;
		padding: 1.5rem;
		margin-bottom: 1.5rem;
		box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
	}

	h3 {
		margin: 0 0 1rem 0;
		color: #333;
	}

	.chart-wrapper {
		height: 300px;
		position: relative;
	}

	.loading,
	.no-data {
		text-align: center;
		color: #666;
		padding: 2rem;
		height: 300px;
		display: flex;
		align-items: center;
		justify-content: center;
	}
</style>
