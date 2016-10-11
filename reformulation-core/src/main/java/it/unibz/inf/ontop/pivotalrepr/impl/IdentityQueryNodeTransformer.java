package it.unibz.inf.ontop.pivotalrepr.impl;

import it.unibz.inf.ontop.pivotalrepr.*;

/**
 * Does nothing
 */
public class IdentityQueryNodeTransformer implements HomogeneousQueryNodeTransformer {
    @Override
    public FilterNode transform(FilterNode filterNode) {
        return filterNode;
    }

    @Override
    public ExtensionalDataNode transform(ExtensionalDataNode extensionalDataNode) {
        return extensionalDataNode;
    }

    @Override
    public LeftJoinNode transform(LeftJoinNode leftJoinNode) {
        return leftJoinNode;
    }

    @Override
    public UnionNode transform(UnionNode unionNode) {
        return unionNode;
    }

    @Override
    public IntensionalDataNode transform(IntensionalDataNode intensionalDataNode) {
        return intensionalDataNode;
    }

    @Override
    public InnerJoinNode transform(InnerJoinNode innerJoinNode) {
        return innerJoinNode;
    }

    @Override
    public ConstructionNode transform(ConstructionNode constructionNode) {
        return constructionNode;
    }

    @Override
    public GroupNode transform(GroupNode groupNode) {
        return groupNode;
    }

    @Override
    public EmptyNode transform(EmptyNode emptyNode) { return emptyNode; }

    @Override
    public TrueNode transform(TrueNode trueNode) { return trueNode; }

}

