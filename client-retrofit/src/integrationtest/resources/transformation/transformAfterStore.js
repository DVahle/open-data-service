function transform(doc) {
	if (doc != null) {
		var newdoc = {};
		newdoc.erlangen = {};
		newdoc.duisburg = {};
		newdoc.erlangen.weather = doc.weatherErlangen;
		newdoc.duisburg.weather = doc.weatherDuisburg;
		newdoc.extension = doc.extension;
		newdoc.id = doc.someId;
		emit(new Date().getTime(), newdoc);
		emit(new Date().getTime(), { key: "value" });
	}
}
