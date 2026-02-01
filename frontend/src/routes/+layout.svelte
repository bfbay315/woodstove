<script lang="ts">
	import '../app.css';
</script>

<div class="app">
	<header>
		<h1>Woodstove Temperature Monitor</h1>
	</header>
	<main>
		<slot />
	</main>
</div>

<style>
	.app {
		min-height: 100vh;
		display: flex;
		flex-direction: column;
	}

	header {
		background: #1a1a2e;
		color: white;
		padding: 1rem 2rem;
		box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
	}

	h1 {
		margin: 0;
		font-size: 1.5rem;
		font-weight: 600;
	}

	main {
		flex: 1;
		padding: 2rem;
		background: #f0f2f5;
	}
</style>
