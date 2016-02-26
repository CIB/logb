var logb = require("./logb.js");
var rl = require("readline");

var definitions = {}
var kb = new logb.Block([])

function Relation(id, keys) {
	this.id = id;
	this.keys = keys;
}

function define_predicate(id, keys) {
	if(id in definitions) {
		throw new Error("Definition " + kb + " already exists!");
	}
	definitions[id] = new Relation(id, keys);
}

function add_statement(id, params, scope) {
	var statement = new Statement(id, params);
	scope.statements.push(statement);
}
