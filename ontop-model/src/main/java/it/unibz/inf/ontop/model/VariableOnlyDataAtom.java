package it.unibz.inf.ontop.model;

import com.google.common.collect.ImmutableList;

/**
 * Data atom that only has variables as arguments
 */
public interface VariableOnlyDataAtom extends DataAtom {

    @Override
    ImmutableList<Variable> getArguments();
}