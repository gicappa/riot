if (!window.riot) var riot = {}; // riot namespace

riot.chooser = (function() {

	var Chooser = Class.create({
		initialize: function(id, url) {
			this.id = id;
			this.url = url;
			this.element = $(id);
			this.element.down('.choose').observe('click', this.choose.bindAsEventListener(this));
			var unset = this.element.down('.unset');
			if (unset) {
				unset.observe('click', this.unset.bindAsEventListener(this));
			}
		},
		
		choose: function(ev) {
			ev.stop();
			this.dialog = new riot.window.Dialog({url: this.url, modal: true, closeButton: true, minWidth: 2000, minHeight: 2000, chooser: this});
		},
		
		unset: function(ev) {
			ev.stop();
			this.chosen(null);
		},
		
		chosen: function(objectId) {
			this.value = objectId || '';
			submitEvent(new ChangeEvent(this), this.onUpdate.bind(this));
		},
		
		onUpdate: function() {
			if (this.dialog) this.dialog.close();			
		}
	});

	return {
		register: function(id, url) {
			new Chooser(id, url);
		}
	}
	
})();

