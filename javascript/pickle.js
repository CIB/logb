exports.types = {};
exports.serialize = function(instance) {
    return JSON.stringify(instance, null, 2);
};

exports.deserialize = function(instance) {
    var rval = JSON.parse(instance);
    rval.__proto__ = exports.types[rval.type];
    return rval;
};
