/// <summary>Kendo UI Radio Button Group Widget.</summary>
/// <description>Kendo UI  widget that displays a radio button group.</description>
/// <version>1.0</version>
/// <author>John DeVight</author>
/// <license>
/// Licensed under the MIT License (MIT)
/// You may obtain a copy of the License at
/// http://opensource.org/licenses/mit-license.html
/// </license>
(function ($, kendo) {
    var NS = ".kendoExtRadioButtonGroup";

    var ExtRadioButtonGroup = kendo.ui.Widget.extend({
            /// <summary>Displays radio buttons from a kendo.data.DataSource.</summary>

        options: {
            // <summary>The data source of the widget which is used to display a list of radio buttons.</summary>
            dataSource: null,

            /// <summary>The field of the data item that provides the text content of the radio buttons.</summary>
            dataTextField: "",

            /// <summary>The field of the data item that provides the value of the widget.</summary>
            dataValueField: "",

            /// <summary>The name given to the radio button group.</summary>
            groupName: "",

            /// <summary>Name of the widget.</summary>
            name: "ExtRadioButtonGroup",

            /// <summary>Specifies the orientation of the widget. Supported values are "horizontal" and "vertical".</summary>
            orientation: "vertical"
        },

        events: [
            /// <summary>Fired when the user selects a radio button.</summary>
            "change",

            /// <summary>Fired when the widget is bound to data from its data source.</summary>
            "dataBound"
        ],

        /// <summary>Data source for the widget.</summary>
        dataSource: null,

        init: function (element, options) {
            /// <summary>Initialize the widget.</summary>

            kendo.ui.Widget.fn.init.call(this, element, options);

            this._dataSource();

            // Read the data from the data source.
            this.dataSource.fetch();

            // Attach an event handler to the selection of a radio button.
            this.element.on("click" + NS, ".radio-info-label", { sender: this }, this._onRadioButtonSelected);

            this.element.css({ "display": "inline-block" });
        },

        destroy: function () {
            /// <summary>Destroy the widget.</summary>

            $(this.element).off(NS);

            kendo.ui.Widget.fn.destroy.call(this);
        },

        _dataSource: function () {
            /// <summary>Initialize the data source.</summary>

            var dataSource = this.options.dataSource;

            // If the data source is an array, then define an object and set the array to the data attribute.
            dataSource = $.isArray(dataSource) ? { data: dataSource } : dataSource;

            // If there is a data source defined already.
            if (this.dataSource && this._refreshHandler) {
                // Unbind from the change event.
                this.dataSource.unbind("change", this._refreshHandler);
            } else {
                // Create the refresh event handler for the data source change event.
                this._refreshHandler = $.proxy(this.refresh, this);
            }

            // Initialize the data source.
            this.dataSource = kendo.data.DataSource.create(dataSource).bind("change", this._refreshHandler);
        },

        _template: function () {
            /// <summary>Get the template for a radio button.</summary>

            var html = kendo.format("<div class='radio-info' data-uid='#: uid #' data-value='#: {1} #' data-text='#: {2} #' style='margin: 0px 35px 5px 0px;display:{3};'><input type='radio' name='{0}' value='#: {1} #' class='radio-k' /><label class='radio-info-label'>#: {2} #</label></div>",
                this.options.groupName.length === 0 ? kendo.guid() : this.options.groupName,
                this.options.dataValueField, this.options.dataTextField,
                this.options.orientation === "vertical" ? "block" : "inline-block");

            return kendo.template(html);
        },

        _onRadioButtonSelected: function (e) {
            /// <summary>Handle the selection of a radio button.</summary>

            var $target = $(this),
                that = e.data.sender;

            that.element.find(".radio-k").prop("checked", false).removeClass("k-state-selected");

            $target.prev(".radio-k").prop("checked", true).addClass("k-state-selected");

            var dataItem = that.dataItem();

            that.trigger("change", { dataItem: dataItem });
        },

        setDataSource: function (dataSource) {
            /// <summary>Sets the data source of the widget.</summary>

            this.options.dataSource = dataSource;
            this._dataSource();
            this.dataSource.fetch();
        },

        refresh: function (e) {
            /// <summary>Renders all radio buttons using the current data items.</summary>

            var template = this._template();

            // Remove all the existing items.
            this.element.empty();

            // Add each of the radio buttons.
            for (var idx = 0; idx < e.items.length; idx++) {
                this.element.append(template(e.items[idx]));
            }

            // Fire the dataBound event.
            this.trigger("dataBound");
        },

        dataItem: function () {
            /// <summary>Gets the dataItem for the selected radio button.</summary>

            var uid = this.element.find(".radio-k:checked").closest(".radio-info").attr("data-uid");

            return this.dataSource.getByUid(uid);
        },

        text: function () {
            /// <summary>Gets or sets the text of the radio button group.</summary>

            if (arguments.length === 0) {
                return this.element.find(".k-state-selected").closest(".radio-info").attr("data-text");
            } else {
                this.element.find(kendo.format(".radio-info[data-text='{0}']", arguments[0])).find(".radio-info-label").click();
            }
        },

        value: function () {
            /// <summary>Gets or sets the value of the radio button group.</summary>

            if (arguments.length === 0) {
                return this.element.find(".k-state-selected").closest(".radio-info").attr("data-value");
            } else {
                this.element.find(kendo.format(".radio-info[data-value='{0}']", arguments[0])).find(".radio-info-label").click();
            }
        }
    });
    kendo.ui.plugin(ExtRadioButtonGroup);
})(window.kendo.jQuery, window.kendo);
