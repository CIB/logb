var logb = require("./logb.js")

function v(name) {
    return new logb.Variable(name);
}

function s(statement_type, params) {
    return new logb.Statement(statement_type, params)
}

exports.AndIntroduction = new logb.InferenceType(
    ["x", "y"],
    [
        v("x"),
        v("y")
    ],
    s("and", [v("x"), v("y")])
);

exports.AndElimination = new logb.InferenceType(
    ["x", "y"],
    [
        s("and", [v("x"), v("y")])
    ],
    v("x")
);

exports.AndCommutation = new logb.InferenceType(
    ["x", "y"],
    [
        s("and", [v("x"), v("y")])
    ],
    s("and", [v("y"), v("x")])
);

exports.OrIntroduction = new logb.InferenceType(
    ["x", "y"],
    [
        v("x")
    ],
    s("or", [v("x"), v("y")])
);


exports.OrElimination = new logb.InferenceType(
    ["x", "y"],
    [
        s("or", [v("x"), v("y")]),
        s("not", v("y"))
    ],
    v("x")
);

exports.OrCommutation = new logb.InferenceType(
    ["x", "y"],
    [
        s("or", [v("x"), v("y")])
    ],
    s("or", [v("y"), v("x")])
);

exports.NotElimination = new logb.InferenceType(
    ["x"],
    [
        s("not", s("not", v("x")))
    ],
    v("x")
);

exports.NotIntroduction = new logb.InferenceType(
    ["x"],
    [
        v("x")
    ],
    s("not", s("not", v("x")))
);

exports.ImplicationElimination = new logb.InferenceType(
    ["x", "y"],
    [
        s("implies", [v("x"), v("y")]),
        v("x")
    ],
    v("y")
);

exports.ImplicationReversal = new logb.InferenceType(
    ["x", "y"],
    [
        s("implies", [v("x"), v("y")])
    ],
    s("implies", [s("not", v("y")), s("not", v("x"))])
);
