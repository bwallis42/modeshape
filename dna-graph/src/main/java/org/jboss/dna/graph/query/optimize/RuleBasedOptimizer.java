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
package org.jboss.dna.graph.query.optimize;

import java.util.LinkedList;
import net.jcip.annotations.Immutable;
import org.jboss.dna.common.collection.Problems;
import org.jboss.dna.common.util.Logger;
import org.jboss.dna.graph.query.QueryContext;
import org.jboss.dna.graph.query.plan.PlanHints;
import org.jboss.dna.graph.query.plan.PlanNode;

/**
 * Optimizer implementation that optimizes a query using a stack of rules. Subclasses can override the
 * {@link #populateRuleStack(LinkedList, PlanHints)} method to define the stack of rules they'd like to use, including the use of
 * custom rules.
 */
@Immutable
public class RuleBasedOptimizer implements Optimizer {

    private final Logger logger = Logger.getLogger(getClass());

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.dna.graph.query.optimize.Optimizer#optimize(QueryContext, PlanNode)
     */
    public PlanNode optimize( QueryContext context,
                              PlanNode plan ) {
        LinkedList<OptimizerRule> rules = new LinkedList<OptimizerRule>();
        populateRuleStack(rules, context.getHints());

        Problems problems = context.getProblems();
        while (rules.peek() != null && !problems.hasErrors()) {
            OptimizerRule nextRule = rules.poll();
            logger.debug("Running query optimizer rule {0}", nextRule);
            plan = nextRule.execute(context, plan, rules);
        }

        return plan;
    }

    /**
     * Method that is used to create the initial rule stack. This method can be overridden by subclasses
     * 
     * @param ruleStack the stack where the rules should be placed; never null
     * @param hints the plan hints
     */
    protected void populateRuleStack( LinkedList<OptimizerRule> ruleStack,
                                      PlanHints hints ) {
        if (hints.hasJoin) {
            ruleStack.addFirst(ChooseJoinAlgorithm.USE_ONLY_NESTED_JOIN_ALGORITHM);
        }
        if (hints.hasCriteria) {
            ruleStack.addFirst(PushSelectCriteria.INSTANCE);
        }
        ruleStack.addFirst(AddAccessNodes.INSTANCE);
        ruleStack.addFirst(RightOuterToLeftOuterJoins.INSTANCE);
    }
}
