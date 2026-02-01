import { writable } from 'svelte/store';
import { browser } from '$app/environment';

interface User {
	email: string;
	name: string;
	picture: string;
}

interface AuthState {
	user: User | null;
	token: string | null;
	isAuthenticated: boolean;
	isLoading: boolean;
}

const initialState: AuthState = {
	user: null,
	token: null,
	isAuthenticated: false,
	isLoading: true
};

function createAuthStore() {
	const { subscribe, set, update } = writable<AuthState>(initialState);

	return {
		subscribe,
		setUser: (user: User, token: string) => {
			if (browser) {
				localStorage.setItem('auth_token', token);
			}
			update((state) => ({
				...state,
				user,
				token,
				isAuthenticated: true,
				isLoading: false
			}));
		},
		logout: () => {
			if (browser) {
				localStorage.removeItem('auth_token');
				// Revoke Google token
				google?.accounts.id.disableAutoSelect();
			}
			set({ ...initialState, isLoading: false });
		},
		initialize: () => {
			if (browser) {
				const token = localStorage.getItem('auth_token');
				if (token) {
					// Decode JWT to get user info (without verification - server will verify)
					try {
						const payload = JSON.parse(atob(token.split('.')[1]));
						const user: User = {
							email: payload.email,
							name: payload.name,
							picture: payload.picture
						};
						// Check if token is expired
						if (payload.exp * 1000 > Date.now()) {
							update((state) => ({
								...state,
								user,
								token,
								isAuthenticated: true,
								isLoading: false
							}));
							return;
						}
					} catch {
						localStorage.removeItem('auth_token');
					}
				}
			}
			update((state) => ({ ...state, isLoading: false }));
		},
		getToken: (): string | null => {
			if (browser) {
				return localStorage.getItem('auth_token');
			}
			return null;
		}
	};
}

export const auth = createAuthStore();

// Google Sign-In types
declare global {
	interface Window {
		google?: typeof google;
	}
	const google: {
		accounts: {
			id: {
				initialize: (config: {
					client_id: string;
					callback: (response: { credential: string }) => void;
					auto_select?: boolean;
				}) => void;
				renderButton: (
					element: HTMLElement,
					config: { theme?: string; size?: string; width?: number }
				) => void;
				prompt: () => void;
				disableAutoSelect: () => void;
			};
		};
	};
}

export function initGoogleSignIn(clientId: string, callback: (token: string) => void) {
	if (!browser) return;

	const script = document.createElement('script');
	script.src = 'https://accounts.google.com/gsi/client';
	script.async = true;
	script.defer = true;
	script.onload = () => {
		google.accounts.id.initialize({
			client_id: clientId,
			callback: (response) => {
				callback(response.credential);
			},
			auto_select: true
		});
		google.accounts.id.prompt();
	};
	document.head.appendChild(script);
}

export function renderGoogleButton(element: HTMLElement) {
	if (!browser || !window.google) return;

	google.accounts.id.renderButton(element, {
		theme: 'outline',
		size: 'large',
		width: 250
	});
}
