from core import *

def prettifyEntity(entity):
    if isinstance(entity, Literal):
        return repr(entity.value)
    if isinstance(entity, Variable):
        return "@" + entity.name
    if isinstance(entity, Statement):
        return prettifyStatement(entity)

def prettifyStatement(statement):
    # If the keys are just numbers 1-n, don't print them, just give a list.
    try:
        keys = sorted([int(key) for key in statement.parameters.keys()])
    except ValueError:
        keys = None

    argString = None
    if keys == list(range(1, len(keys) + 1)):
        argString = ", ".join([prettifyEntity(entity) for (key, entity) in statement.parameters.items()])
    else:
        argString = ", ".join([key + ": " + prettifyEntity(entity) for (key, entity) in statement.parameters.items()])
    return "{}({})".format(statement.statementType, argString)