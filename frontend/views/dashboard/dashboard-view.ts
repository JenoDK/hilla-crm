import { html } from 'lit';
import { customElement } from 'lit/decorators.js';
import { View } from 'Frontend/views/view';

@customElement('dashboard-view')
export class DashboardView extends View {
	connectedCallback() {
		super.connectedCallback();
		this.classList.add('flex', 'flex-col', 'items-center', 'pt-xl');
	}

	render() {
		return html` <h1>Dashboard view</h1> `;
	}
}