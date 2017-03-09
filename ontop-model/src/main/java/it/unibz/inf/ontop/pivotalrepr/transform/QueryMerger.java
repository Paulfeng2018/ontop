package it.unibz.inf.ontop.pivotalrepr.transform;


import it.unibz.inf.ontop.pivotalrepr.ImmutableQueryModifiers;
import it.unibz.inf.ontop.pivotalrepr.IntermediateQuery;

import java.util.Collection;
import java.util.Optional;

public interface QueryMerger {

    Optional<IntermediateQuery> mergeDefinitions(Collection<IntermediateQuery> predicateDefinitions);

    /**
     * TODO: describe
     * The optional modifiers are for the top construction node above the UNION (if any).
     */
    Optional<IntermediateQuery> mergeDefinitions(Collection<IntermediateQuery> predicateDefinitions,
                                                 ImmutableQueryModifiers topModifiers);
}
