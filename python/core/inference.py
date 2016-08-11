from core import KnowledgeBase
from .statement import merge_environments


class InferenceProvider(object):
    def getInferences(self, kb: KnowledgeBase):
        pass


class PatternInferenceProvider(InferenceProvider):
    def getInferences(self, kb: KnowledgeBase):
        for rule in kb.rules:
            for result in rule.getInferences(kb):
                yield result


class InferenceRule(object):
    def __init__(self, variables, conclusion, dependencies):
        self.variables = variables
        self.conclusion = conclusion
        self.dependencies = dependencies

        # TODO: ensure that only declared variables are used

    def getInferences(self, kb: KnowledgeBase):
        def find_matches_recursion(dependencies, env = {}):
            dependencyID = dependencies[0]
            dependency = kb[dependencyID]
            for match, newEnv in kb.query(kb[dependency.substitute(dependencyID, kb, env)]):
                newEnv = merge_environments(kb, env, newEnv)
                rest = dependencies[1:]
                if len(rest):
                    for subenv, subresult in find_matches_recursion(rest, newEnv):
                        yield subenv, [match] + subresult
                else:
                    yield newEnv, [match]

        for env, result in find_matches_recursion(self.dependencies):
            yield env, result, kb[self.conclusion].substitute(self.conclusion, kb, env)
