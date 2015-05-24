Array.prototype.hasDuplicates = function() {
    for(var i = 0; i < this.length; i++) {
        if(this.lastIndexOf(this[i]) != i) {
            return true;
        }
    }
    return false;
}

function deepcopy(value) {
    var value_copy = value;
    if(value instanceof Object) {
        value_copy = value.deepcopy();
    }
    return value_copy;
}

Object.prototype.deepcopy = function() {
    var rval = {};
    this.forEachPair( function(key, value) {
        rval[key] = deepcopy(value);
    })
    rval.__proto__ = this.__proto__;
    return rval;
}

Array.prototype.deepcopy = function() {
    var rval = [];
    for(var i=0; i<this.length; i++) {
        rval[i] = deepcopy(this[i]);
    }
    return rval;
}

Object.prototype.asObject = function() {
    return this;
}

Array.prototype.asObject = function() {
    var rval = {};
    for(var i=0; i < this.length; i++) {
        rval[i] = this[i];
    }
    return rval;
}

Object.prototype.forEachPair = function(callback) {
    for(var key in this) {
        if(this.hasOwnProperty(key)) {
            callback(key, this[key]);
        }
    }
}
