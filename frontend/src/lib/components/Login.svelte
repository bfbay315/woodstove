<script lang="ts">
	import { onMount } from 'svelte';
	import { auth, initGoogleSignIn, renderGoogleButton } from '$lib/auth';

	const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID;

	let buttonContainer: HTMLDivElement;
	let error: string | null = null;

	function handleCredential(token: string) {
		try {
			const payload = JSON.parse(atob(token.split('.')[1]));
			auth.setUser(
				{
					email: payload.email,
					name: payload.name,
					picture: payload.picture
				},
				token
			);
		} catch (e) {
			error = 'Failed to process login. Please try again.';
		}
	}

	onMount(() => {
		if (!GOOGLE_CLIENT_ID) {
			error = 'Google Client ID not configured';
			return;
		}

		initGoogleSignIn(GOOGLE_CLIENT_ID, handleCredential);

		// Wait for script to load then render button
		const checkGoogle = setInterval(() => {
			if (window.google) {
				clearInterval(checkGoogle);
				renderGoogleButton(buttonContainer);
			}
		}, 100);

		return () => clearInterval(checkGoogle);
	});
</script>

<div class="login-container">
	<div class="login-card">
		<h1>Woodstove Monitor</h1>
		<p>Sign in to view temperature data</p>

		{#if error}
			<div class="error">{error}</div>
		{/if}

		<div class="button-container" bind:this={buttonContainer}></div>
	</div>
</div>

<style>
	.login-container {
		min-height: 100vh;
		display: flex;
		align-items: center;
		justify-content: center;
		background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
	}

	.login-card {
		background: white;
		padding: 3rem;
		border-radius: 12px;
		box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
		text-align: center;
		max-width: 400px;
		width: 90%;
	}

	h1 {
		margin: 0 0 0.5rem 0;
		color: #1a1a2e;
		font-size: 1.75rem;
	}

	p {
		margin: 0 0 2rem 0;
		color: #666;
	}

	.button-container {
		display: flex;
		justify-content: center;
	}

	.error {
		background: #ffebee;
		color: #c62828;
		padding: 0.75rem;
		border-radius: 4px;
		margin-bottom: 1rem;
		font-size: 0.875rem;
	}
</style>
