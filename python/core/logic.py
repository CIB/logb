from .inference import InferenceRule
from .variable import Variable
from .statement import Statement
from .knowledgebase import KnowledgeBase


def andElimination(kb: KnowledgeBase):
    x = kb.addEntity(Variable("x"))
    y = kb.addEntity(Variable("y"))
    return InferenceRule(
        ["x", "y"],
        x,
        [
            Statement("&", {"1": x, "2": y})
        ])


def andCommutation(kb: KnowledgeBase):
    x = kb.addEntity(Variable("x"))
    y = kb.addEntity(Variable("y"))
    return InferenceRule(
        ["x", "y"],
        Statement("&", {"1": x, "2": y}),
        [
            Statement("&", {"1": x, "2": y})
        ])


def andIntroduction(kb: KnowledgeBase):
    x = kb.addEntity(Variable("x"))
    y = kb.addEntity(Variable("y"))
    return InferenceRule(
        ["x", "y"],
        Statement("&", {"1": x, "2": y}),
        [
            x,
            y
        ])