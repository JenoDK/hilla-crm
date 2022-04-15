import {crmStore} from 'Frontend/stores/app-store';
import {makeAutoObservable, observable} from 'mobx';
import Contact from "Frontend/generated/com/jeno/application/data/entity/Contact";
import ContactModel from "Frontend/generated/com/jeno/application/data/entity/ContactModel";

class ListViewStore {
	filterText = '';
	selectedContact: Contact | null = null;

	constructor() {
		makeAutoObservable(
			this,
			{ selectedContact: observable.ref },
			{ autoBind: true }
		);
	}

	updateFilter(filterText: string) {
		this.filterText = filterText;
	}

	setSelectedContact(contact: Contact) {
		this.selectedContact = contact;
	}

	editNew() {
		this.selectedContact = ContactModel.createEmptyValue();
	}

	cancelEdit() {
		this.selectedContact = null;
	}

	get filteredContacts() {
		const contacts = crmStore.contacts;
		if (this.filterText === '') {
			return contacts;
		}
		const filter = new RegExp(this.filterText, 'i');
		return contacts.filter((contact) =>
			filter.test(`${contact.firstName} ${contact.lastName}`)
		);
	}

	async save(contact: Contact) {
		await crmStore.saveContact(contact);
		this.cancelEdit();
	}

	async delete() {
		if (this.selectedContact) {
			await crmStore.deleteContact(this.selectedContact);
			this.cancelEdit();
		}
	}
}

export const listViewStore = new ListViewStore();