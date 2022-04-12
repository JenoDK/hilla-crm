import {makeAutoObservable} from 'mobx';
import {CrmStore} from "Frontend/stores/crm-store";
import {UiStore} from "Frontend/stores/ui-store";

export class AppStore {
	crmStore = new CrmStore();
	uiStore = new UiStore();

	constructor() {
		makeAutoObservable(this);
	}
}

export const appStore = new AppStore();
export const crmStore = appStore.crmStore;
export const uiStore = appStore.uiStore;
