import {Route} from '@vaadin/router';
import './views/dashboard/dashboard-view';
import './main-layout.ts';

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
		path: '',
		component: 'main-layout',
		children: views,
	},
];
