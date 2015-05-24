exports.types = {};
exports.serialize = function(instance) {
    return JSON.stringify(instance);
};

exports.deserialize = function(instance) {
    var rval = JSON.parse(instance);
    rval.__proto__ = types[rval.type];
    return rval;
};
