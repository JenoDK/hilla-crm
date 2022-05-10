import {Commands, Context, Route, Router} from '@vaadin/router';
import './views/dashboard/dashboard-view';
import './main-layout.ts';
import {uiStore} from './stores/app-store';
import './views/login/login-view';
import {autorun} from "mobx";

export type ViewRoute = Route & {
	title?: string;
	icon?: string;
	children?: ViewRoute[];
};

export const views: ViewRoute[] = [
	// place routes below (more info https://hilla.dev/docs/routing)
	{
		path: '',
		component: 'dashboard-view',
		icon: 'la la-file',
		title: 'Dashboard',
	},
	{
		path: 'contacts',
		component: 'list-view',
		icon: 'la la-file',
		title: 'Contacts',
		action: async () => {
			await import('./views/list/list-view');
		},
	},
];

export const routes: ViewRoute[] = [
	{
		path: 'login',
		component: 'login-view',
	},
	{
		path: 'logout',
		action: (_: Context, commands: Commands) => {
			uiStore.logout();
			return commands.redirect('/login');
		},
	},
	{
		path: 'oauth2/authorization/google',
		action: (_: Context, commands: Commands) => {
			window.location.pathname = _.pathname;
		},
	},
	{
		path: '',
		component: 'main-layout',
		children: views,
	},
];

autorun(() => {
	if (uiStore.loggedIn) {
		Router.go('/');
	} else {
		if (location.pathname !== '/login') {
			sessionStorage.setItem('login-redirect-path', location.pathname);
			Router.go('/login');
		}
	}
});
