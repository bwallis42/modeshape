/*
 * JBoss DNA (http://www.jboss.org/dna)
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * See the AUTHORS.txt file in the distribution for a full listing of 
 * individual contributors.
 *
 * JBoss DNA is free software. Unless otherwise indicated, all code in JBoss DNA
 * is licensed to you under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * JBoss DNA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.dna.graph.query.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.jcip.annotations.Immutable;
import org.jboss.dna.common.util.CheckArg;
import org.jboss.dna.common.util.ObjectUtil;

/**
 * 
 */
@Immutable
public class Query extends QueryCommand {

    public static final boolean IS_DISTINCT_DEFAULT = false;

    private final Source source;
    private final Constraint constraint;
    private final List<Column> columns;
    private final boolean distinct;

    /**
     * Create a new query that uses the supplied source.
     * 
     * @param source the source
     * @throws IllegalArgumentException if the source is null
     */
    public Query( Source source ) {
        super();
        CheckArg.isNotNull(source, "source");
        this.source = source;
        this.constraint = null;
        this.columns = Collections.<Column>emptyList();
        this.distinct = IS_DISTINCT_DEFAULT;
    }

    /**
     * Create a new query that uses the supplied source, constraint, orderings, columns and limits.
     * 
     * @param source the source
     * @param constraint the constraint (or composite constraint), or null or empty if there are no constraints
     * @param orderings the specifications of how the results are to be ordered, or null if the order is to be implementation
     *        determined
     * @param columns the columns to be included in the results, or null or empty if there are no explicit columns and the actual
     *        result columns are to be implementation determiend
     * @param limit the limit for the results, or null if all of the results are to be included
     * @param isDistinct true if duplicates are to be removed from the results
     * @throws IllegalArgumentException if the source is null
     */
    public Query( Source source,
                  Constraint constraint,
                  List<Ordering> orderings,
                  List<Column> columns,
                  Limit limit,
                  boolean isDistinct ) {
        super(orderings, limit);
        CheckArg.isNotNull(source, "source");
        this.source = source;
        this.constraint = constraint;
        this.columns = columns != null ? columns : Collections.<Column>emptyList();
        this.distinct = isDistinct;
    }

    /**
     * Get the source for the results.
     * 
     * @return the query source; never null
     */
    public final Source getSource() {
        return source;
    }

    /**
     * Get the constraints, if there are any.
     * 
     * @return the constraint; may be null
     */
    public final Constraint getConstraint() {
        return constraint;
    }

    /**
     * Return the columns defining the query results. If there are no columns, then the columns are implementation determined.
     * 
     * @return the list of columns; never null
     */
    public final List<Column> getColumns() {
        return columns;
    }

    /**
     * Determine whether this query is to return only distinct values.
     * 
     * @return true if the query is to remove duplicate tuples, or false otherwise
     */
    public boolean isDistinct() {
        return distinct;
    }

    public Query distinct() {
        return new Query(source, constraint, getOrderings(), columns, getLimits(), true);
    }

    public Query noDistinct() {
        return new Query(source, constraint, getOrderings(), columns, getLimits(), false);
    }

    public Query constrainedBy( Constraint constraint ) {
        return new Query(source, constraint, getOrderings(), columns, getLimits(), distinct);
    }

    public Query orderedBy( List<Ordering> orderings ) {
        return new Query(source, constraint, orderings, columns, getLimits(), distinct);
    }

    public Query withLimit( int rowLimit ) {
        return new Query(source, constraint, getOrderings(), columns, getLimits().withRowLimit(rowLimit), distinct);
    }

    public Query withOffset( int offset ) {
        return new Query(source, constraint, getOrderings(), columns, getLimits().withOffset(offset), distinct);
    }

    public Query returning( List<Column> columns ) {
        return new Query(source, constraint, getOrderings(), columns, getLimits(), distinct);
    }

    public Query adding( Ordering... orderings ) {
        List<Ordering> newOrderings = null;
        if (this.getOrderings() != null) {
            newOrderings = new ArrayList<Ordering>(getOrderings());
            for (Ordering ordering : orderings) {
                newOrderings.add(ordering);
            }
        } else {
            newOrderings = Arrays.asList(orderings);
        }
        return new Query(source, constraint, newOrderings, columns, getLimits(), distinct);
    }

    public Query adding( Column... columns ) {
        List<Column> newColumns = null;
        if (this.columns != null) {
            newColumns = new ArrayList<Column>(this.columns);
            for (Column column : columns) {
                newColumns.add(column);
            }
        } else {
            newColumns = Arrays.asList(columns);
        }
        return new Query(source, constraint, getOrderings(), newColumns, getLimits(), distinct);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Visitors.readable(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if (obj == this) return true;
        if (obj instanceof Query) {
            Query that = (Query)obj;
            if (this.distinct != that.distinct) return false;
            if (!this.source.equals(that.source)) return false;
            if (!ObjectUtil.isEqualWithNulls(this.getLimits(), that.getLimits())) return false;
            if (!ObjectUtil.isEqualWithNulls(this.constraint, that.constraint)) return false;
            if (!ObjectUtil.isEqualWithNulls(this.columns, that.columns)) return false;
            if (!ObjectUtil.isEqualWithNulls(this.getOrderings(), that.getOrderings())) return false;
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.dna.graph.query.model.Visitable#accept(org.jboss.dna.graph.query.model.Visitor)
     */
    public void accept( Visitor visitor ) {
        visitor.visit(this);
    }
}
