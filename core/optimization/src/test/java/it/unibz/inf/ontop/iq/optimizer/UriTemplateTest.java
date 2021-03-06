package it.unibz.inf.ontop.iq.optimizer;

import com.google.common.collect.ImmutableSet;
import it.unibz.inf.ontop.iq.exception.EmptyQueryException;
import it.unibz.inf.ontop.iq.node.ConstructionNode;
import it.unibz.inf.ontop.iq.node.ExtensionalDataNode;
import it.unibz.inf.ontop.iq.node.InnerJoinNode;
import it.unibz.inf.ontop.iq.node.LeftJoinNode;
import it.unibz.inf.ontop.model.atom.DistinctVariableOnlyDataAtom;
import it.unibz.inf.ontop.model.term.impl.URITemplatePredicateImpl;
import it.unibz.inf.ontop.model.atom.AtomPredicate;
import it.unibz.inf.ontop.model.term.functionsymbol.URITemplatePredicate;
import it.unibz.inf.ontop.model.term.Constant;
import it.unibz.inf.ontop.model.term.ImmutableFunctionalTerm;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.Variable;
import it.unibz.inf.ontop.iq.*;
import org.junit.Ignore;
import org.junit.Test;

import static it.unibz.inf.ontop.OptimizationTestingTools.*;
import static it.unibz.inf.ontop.model.OntopModelSingletons.ATOM_FACTORY;
import static it.unibz.inf.ontop.model.OntopModelSingletons.SUBSTITUTION_FACTORY;
import static it.unibz.inf.ontop.model.term.functionsymbol.ExpressionOperation.CONCAT;
import static it.unibz.inf.ontop.model.term.functionsymbol.ExpressionOperation.EQ;
import static it.unibz.inf.ontop.iq.node.BinaryOrderedOperatorNode.ArgumentPosition.LEFT;
import static it.unibz.inf.ontop.iq.node.BinaryOrderedOperatorNode.ArgumentPosition.RIGHT;

public class UriTemplateTest {

    private static URITemplatePredicate URI_PREDICATE_ONE_VAR =  new URITemplatePredicateImpl(2);
    private static Constant URI_TEMPLATE_STR_1_PREFIX =  DATA_FACTORY.getConstantLiteral("http://example.org/ds1/");
    private static Constant URI_TEMPLATE_STR_1 =  DATA_FACTORY.getConstantLiteral(URI_TEMPLATE_STR_1_PREFIX.getValue() + "{}");
    private static Constant URI_TEMPLATE_STR_2_PREFIX =  DATA_FACTORY.getConstantLiteral("http://example.org/ds2/");
    private static Constant URI_TEMPLATE_STR_2 =  DATA_FACTORY.getConstantLiteral(URI_TEMPLATE_STR_2_PREFIX.getValue() + "{}");
    private static Constant URI_TEMPLATE_STR_3 =  DATA_FACTORY.getConstantLiteral("{}");

    private final static AtomPredicate TABLE1_PREDICATE = ATOM_FACTORY.getAtomPredicate("T1", 2);
    private final static AtomPredicate TABLE2_PREDICATE = ATOM_FACTORY.getAtomPredicate("T2", 2);
    private final static AtomPredicate TABLE3_PREDICATE = ATOM_FACTORY.getAtomPredicate("T3", 1);

    private final static Variable X = DATA_FACTORY.getVariable("x");
    private final static Variable Y = DATA_FACTORY.getVariable("y");
    private final static Variable Z = DATA_FACTORY.getVariable("z");
    private final static Variable A = DATA_FACTORY.getVariable("a");
    private final static Variable B = DATA_FACTORY.getVariable("b");
    private final static Variable C = DATA_FACTORY.getVariable("c");
    private final static Variable D = DATA_FACTORY.getVariable("d");
    private final static Variable E = DATA_FACTORY.getVariable("e");
    private final static Variable F = DATA_FACTORY.getVariable("f");

    private final static AtomPredicate ANS1_PREDICATE_1 = ATOM_FACTORY.getAtomPredicate("ans1", 1);

    public UriTemplateTest() {
    }

    @Ignore
    @Test
    public void testCompatibleUriTemplates1() throws EmptyQueryException {
        IntermediateQueryBuilder initialQueryBuilder = createQueryBuilder(EMPTY_METADATA);

        DistinctVariableOnlyDataAtom projectionAtom = ATOM_FACTORY.getDistinctVariableOnlyDataAtom(ANS1_PREDICATE_1, X);
        ConstructionNode rootNode = IQ_FACTORY.createConstructionNode(projectionAtom.getVariables());
        initialQueryBuilder.init(projectionAtom, rootNode);

        LeftJoinNode leftJoinNode = IQ_FACTORY.createLeftJoinNode();
        initialQueryBuilder.addChild(rootNode, leftJoinNode);

        InnerJoinNode joinNode = IQ_FACTORY.createInnerJoinNode();
        initialQueryBuilder.addChild(leftJoinNode, joinNode, LEFT);

        ConstructionNode leftConstructionNode = IQ_FACTORY.createConstructionNode(ImmutableSet.of(X),
                SUBSTITUTION_FACTORY.getSubstitution(X, generateOneVarURITemplate(URI_TEMPLATE_STR_1, A)));
        initialQueryBuilder.addChild(joinNode, leftConstructionNode);

        ExtensionalDataNode leftDataNode = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_PREDICATE, A, B));
        initialQueryBuilder.addChild(leftConstructionNode, leftDataNode);

        ConstructionNode middleConstructionNode = IQ_FACTORY.createConstructionNode(ImmutableSet.of(X),
                SUBSTITUTION_FACTORY.getSubstitution(X, generateOneVarURITemplate(URI_TEMPLATE_STR_3, C)));
        initialQueryBuilder.addChild(joinNode, middleConstructionNode);

        ExtensionalDataNode middleDataNode = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE2_PREDICATE, C, D));
        initialQueryBuilder.addChild(middleConstructionNode, middleDataNode);

        ConstructionNode rightConstructionNode = IQ_FACTORY.createConstructionNode(ImmutableSet.of(X),
                SUBSTITUTION_FACTORY.getSubstitution(X, generateOneVarURITemplate(URI_TEMPLATE_STR_2, E)));
        initialQueryBuilder.addChild(leftJoinNode, rightConstructionNode, RIGHT);

        ExtensionalDataNode rightDataNode = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE3_PREDICATE, E));
        initialQueryBuilder.addChild(rightConstructionNode, rightDataNode);

        IntermediateQuery initialQuery = initialQueryBuilder.build();
        System.out.println("\nBefore optimization: \n" +  initialQuery);


        IntermediateQueryBuilder expectedQueryBuilder = initialQuery.newBuilder();
        expectedQueryBuilder.init(projectionAtom, leftConstructionNode);

        InnerJoinNode newJoinNode = IQ_FACTORY.createInnerJoinNode(
                DATA_FACTORY.getImmutableExpression(EQ,
                        DATA_FACTORY.getImmutableExpression(CONCAT, URI_TEMPLATE_STR_1_PREFIX, A),
                        C));

        expectedQueryBuilder.addChild(leftConstructionNode, newJoinNode);
        expectedQueryBuilder.addChild(newJoinNode, leftDataNode);
        expectedQueryBuilder.addChild(newJoinNode, middleDataNode);

        IntermediateQuery expectedQuery = expectedQueryBuilder.build();
        System.out.println("\n Expected query : \n" +  expectedQuery);

        IntermediateQuery optimizedQuery = BINDING_LIFT_OPTIMIZER.optimize(initialQuery);

        System.out.println("\n After optimization: \n" +  optimizedQuery);
    }


    private static ImmutableFunctionalTerm generateOneVarURITemplate(Constant templateString, ImmutableTerm value) {
        return DATA_FACTORY.getImmutableFunctionalTerm(URI_PREDICATE_ONE_VAR, templateString, value);
    }
}
