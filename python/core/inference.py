from core import KnowledgeBase


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
        # TODO: substitute environment-so-far into nodes further down
        def find_matches_recursion(dependencies):
            dependency = dependencies[0]
            for match in kb.query(dependency):
                rest = dependencies[1:]
                if len(rest):
                    for subresult in find_matches_recursion(rest):
                        yield [match] + subresult
                else:
                    yield [match]

        for result in find_matches_recursion(self.dependencies):
            yield result