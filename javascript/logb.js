utils = require("./utils.js");
pickle = require("./pickle.js");

logb = exports;

logb.Entity = function(type) {
    this.isEntity = true;
    this.type = type;
}

logb.Entity.prototype.equals = function(other) {
    return JSON.stringify(this) == JSON.stringify(other);
}

logb.Statement = function(type, parameters) {
    logb.Entity.call(this, "Statement");
    this.statementType = type;
    this.parameters = parameters.asObject();
}

logb.Statement.prototype = Object.create(logb.Entity.prototype);

logb.Number = function(num) {
    logb.Entity.call(this, "Number");
    this.value = num;
}

logb.String = function(str) {
    logb.Entity.call(this, "String");
    this.value = str;
}

logb.Structure = function(stype, ptr) {
    logb.Entity.call(this, "Structure");
    this.stype = stype;
    this.ptr = ptr;
}

logb.Variable = function(name) {
    logb.Entity.call(this, "Variable");
    this.name = name;
}

logb.Variable.prototype.equals = function(other) {
    return logb.isEntity(other) && 
            other.type == this.type &&
            other.name == this.name;
}

logb.isEntity = function(obj) {
    return obj.isEntity == true;
}

logb.isType = function(entity, type) {
    return entity.type == type;
}

logb.Block = function(boundVariables) {
    if(boundVariables.hasDuplicates()) {
        throw new Error("Duplicate bound variable names.");
    }
    this.boundVariables = boundVariables.map(function(variableName) {
        return new logb.Variable(variableName);
    });
    this.statements = [];
}

logb.InferenceType = function(variables, dependencies, conclusion) {
    this.variables = variables;
    this.dependencies = dependencies;
    this.conclusion = conclusion;
}

logb.InferenceInstance = function(inferenceType, variableValues) {
    this.inferenceType = inferenceType;
    this.variableValues = variableValues;
};

function substitute(object, substitutions) {
    if(object instanceof Object) {
        return object.substitute(substitutions);
    } else {
        return object;
    }
}

logb.Entity.prototype.substitute = function(substitutions) {
    return this;
}

logb.Variable.prototype.substitute = function(substitutions) {
    if(this.name in substitutions) {
        return substitutions[this.name];
    } else {
        return this;
    }
}

logb.Statement.prototype.substitute = function(substitutions) {
    var rval = this.deepcopy();
    var parameters = this.parameters;
    rval.parameters = rval.parameters.substitute(substitutions);
    return rval;
}

Array.prototype.substitute = function(substitutions) {
    var rval = [];
    for(var i=0; i < this.length; i++) {
        rval[i] = substitute(rval[i], substitutions);
    }
    return rval;
}

Object.prototype.substitute = function(substitutions) {
    var rval = this.deepcopy();
    Object.keys(rval).map(function(index) {
        rval[index] = substitute(rval[index], substitutions);
    });
    return rval;
}

logb.Block.prototype.contains = function(contains_what) {
    var matchingStatements = this.statements.filter(function(statement) {
        return statement.equals(contains_what);
    });
    return matchingStatements.length > 0;
}

logb.Block.prototype.applyInference = function(inferenceType, substitutions) {
    var foundDependencies = [];
    inferenceType.dependencies.forEach(function(dependency) {
        dependency = dependency.substitute(substitutions);
        
        if(!this.contains(dependency)) {
            return undefined;
        }

        foundDependencies.push(dependency);
    }, this);

    this.statements.push(inferenceType.conclusion.substitute(substitutions));
    return new logb.InferenceInstance(inferenceType, substitutions);
}

pickle.types["Entity"] = logb.Entity;
pickle.types["Statement"] = logb.Statement;
pickle.types["Block"] = logb.Block;
pickle.types["Structure"] = logb.Structure;
