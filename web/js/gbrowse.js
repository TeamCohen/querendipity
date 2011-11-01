


function translate(chr,exc,inc) {
	var gb = new Gbrowse();
	gb.values.ref = chr.slice(3,chr.length);
	
	var exstr = exc + ";" + inc;
	while (exstr.indexOf("chr")>=0) exstr = exstr.replace("chr","");
	while (exstr.indexOf("_")>=0) {
		exstr = exstr.replace(gb.values.ref+"_","");
		exstr = exstr.replace("_","..");
	}
	var exs = exstr.split(";");
	//var ex_regions = "";//exs.join("%40%23"+col ");
	//var ex_coords = [];
	var highest,lowest;
	for (var e in exs) {
		if (typeof(exs[e]) !== "string" || exs[e] === "") continue;
		var parts = exs[e].split("..");
		gb.addRegion(parts[0],parts[1]);
		if (!lowest || +parts[0] < lowest) lowest = +parts[0];
		if (!highest || +parts[1] > highest) highest = +parts[1];
	}
	
	gb.values.start = Math.ceil(highest/1000) * 1000;
	gb.values.stop  = Math.floor(lowest/1000) * 1000;
	return gb.makeURL();
}

function Gbrowse() {
	// defaults
	this.href = {
		base: "http://flybase.org/cgi-bin/gbrowse/dmel/?",
		start: "start=", // 11000000
		stop: "stop=", // 11005000
		ref: "ref=", // 3R
		width: "width=", // 1024
		version: "version=", //100
		label: "label=", //gene-RNA
		region: "h_region=", //3R%3A11002156..11004001%40%2399ff99
		feature: "h_feat=", //3R%40%23FFD0D0
		id: "id=", //13d8ae2206a110b5f83b2a48ef4a4a4a
		grid: "grid=", //on
		delim: ";"
	};
	this.values = {
		width: "800", 
		version: "100", 
		label: "gene-RNA", 
		grid: "on",
	};
	
	var opts = ["ff","cc","99"];
	var cols = [];
	for (var i=0;i<opts.length;i++) {
		for(var j=0;j<opts.length;j++) {
			for (var k=0; k<opts.length; k++) {
				cols.push(opts[i]+opts[j]+opts[k]);
			}
		}
	}
	this.colors = cols;
}
Gbrowse.prototype.makeURL = function() {
	var url=this.href.base;
	for (var f in this.values) {
		url += this.href[f] + this.values[f] + this.href.delim;
	}
	return url;
};
Gbrowse.prototype.addRegion = function (start,stop) {
	var reg = this.values.ref 
		+ "%3A"
		+ start
		+ ".."
		+ stop
		+ "%40%23"
		+ this.colors.pop();
	if (this.values.region) this.values.region += " "; else this.values.region = "";
	this.values.region += reg;
};

gbrowse = {};

function init() {
	console.log("gbrowse link builder initialization complete");
};