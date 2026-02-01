<script lang="ts">
	import '../app.css';
	import { onMount } from 'svelte';
	import { auth } from '$lib/auth';
	import Login from '$lib/components/Login.svelte';

	let isLoading = true;
	let isAuthenticated = false;
	let user: { name: string; picture: string; email: string } | null = null;

	onMount(() => {
		auth.initialize();
	});

	auth.subscribe((state) => {
		isLoading = state.isLoading;
		isAuthenticated = state.isAuthenticated;
		user = state.user;
	});

	function handleLogout() {
		auth.logout();
	}
</script>

{#if isLoading}
	<div class="loading-screen">
		<div class="spinner"></div>
	</div>
{:else if !isAuthenticated}
	<Login />
{:else}
	<div class="app">
		<header>
			<h1>Woodstove Temperature Monitor</h1>
			<div class="user-menu">
				{#if user}
					<img src={user.picture} alt={user.name} class="avatar" />
					<span class="user-name">{user.name}</span>
				{/if}
				<button class="logout-btn" on:click={handleLogout}>Sign Out</button>
			</div>
		</header>
		<main>
			<slot />
		</main>
	</div>
{/if}

<style>
	.loading-screen {
		min-height: 100vh;
		display: flex;
		align-items: center;
		justify-content: center;
		background: #1a1a2e;
	}

	.spinner {
		width: 40px;
		height: 40px;
		border: 3px solid rgba(255, 255, 255, 0.3);
		border-top-color: white;
		border-radius: 50%;
		animation: spin 1s linear infinite;
	}

	@keyframes spin {
		to {
			transform: rotate(360deg);
		}
	}

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
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	h1 {
		margin: 0;
		font-size: 1.5rem;
		font-weight: 600;
	}

	.user-menu {
		display: flex;
		align-items: center;
		gap: 0.75rem;
	}

	.avatar {
		width: 32px;
		height: 32px;
		border-radius: 50%;
	}

	.user-name {
		font-size: 0.875rem;
	}

	.logout-btn {
		background: rgba(255, 255, 255, 0.1);
		border: 1px solid rgba(255, 255, 255, 0.2);
		color: white;
		padding: 0.5rem 1rem;
		border-radius: 4px;
		font-size: 0.875rem;
		cursor: pointer;
		transition: background 0.2s;
	}

	.logout-btn:hover {
		background: rgba(255, 255, 255, 0.2);
	}

	main {
		flex: 1;
		padding: 2rem;
		background: #f0f2f5;
	}

	@media (max-width: 600px) {
		.user-name {
			display: none;
		}
	}
</style>
